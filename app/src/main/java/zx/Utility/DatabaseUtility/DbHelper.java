package zx.Utility.DatabaseUtility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import zx.Utility.DataObject;

/**
 * Created by Lesley on 2016/3/11.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static DbHelper instance = null;

    private DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static DbHelper initiateDatabase(Context context) {
        if (instance == null) {
            instance = new DbHelper(context, "heartrate.db", null, 1);
        }
        return instance;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists user("
                + "username varchar primary key,"
                + "online varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static String queryUserData() {
        String result = "";
        SQLiteDatabase db = instance.getReadableDatabase();
        Log.e("zxdebug", "start query user data");
        try {
            String status = "yes";
            Cursor cursor = db.query("user", null, "online='" + status + "'", null, null, null, null);
            //获取name列的索引
            int usernameIndex = cursor.getColumnIndex("username");
            //获取level列的索引
            //int onelineIndex = cursor.getColumnIndex("online");
            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                result = result + cursor.getString(usernameIndex);
                // result = result + cursor.getInt(onelineIndex) + "\n";
            }
            Log.e("zxdebug", "queryUserData result: " + result + ".");
            cursor.close();//关闭结果集
        } catch (Exception e) {
            Log.e("zxdebug", Log.getStackTraceString(e));
        }
        db.close();//关闭数据库对象
        return result;
    }

    public static String queryTestData(String tableName) {
        //List<String> datelist = new ArrayList<String>();
        //List<String> resultlist = new ArrayList<String>();
        //DataObject ob = new DataObject();
        String result = null;
        SQLiteDatabase db = instance.getReadableDatabase();
        //查询表中的数据
        //Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        Cursor cursor = db.query(tableName, null, "uploadstate='no'", null, null, null, null);
        //获取name列的索引
        int ttIndex = cursor.getColumnIndex("testtime");
        //获取level列的索引
        int tvlIndex = cursor.getColumnIndex("testvalue");

        try {
            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                result = result + cursor.getString(ttIndex) + "#";
                //datelist.add(cursor.getString(ttIndex));
                //resultlist.add(cursor.getString(tvlIndex));
            }
            //ob.setList(datelist, resultlist);
        } catch (Exception e) {
            Log.e("zxdebug", Log.getStackTraceString(e));
        }
        cursor.close();//关闭结果集
        db.close();//关闭数据库对象
        return result;
    }

    //tested
    public static void updateUserData(String valueSet) {
        String[] values = valueSet.split("#");
        String[] tableName = User.getUsername().split("@");
        SQLiteDatabase db = instance.getWritableDatabase();
        try {
            //username#online#yes
            if (values[1] == "online") {
                ContentValues contentValues = new ContentValues();
                contentValues.put("online", values[2]);
                db.update("user", contentValues, "username = " + values[0], null);
                Log.i("zxdebug", "insert online to local database success");
            } else {
                //username#yes
                Log.i("zxdebug", "insert into user(username,online) values('" + values[0] + "','" + values[1] + "')");
                db.execSQL("insert into user(username,online) values('" + values[0] + "','" + values[1] + "')");
                Log.i("zxdebug", "create table if not exists " + tableName[0] + tableName[1].split("\\.")[0] + "("
                        + "testtime varchar,"
                        + "testvalue varchar)");
                db.execSQL("create table if not exists " + tableName[0] + tableName[1].split("\\.")[0] + "("
                        + "testtime varchar,"
                        + "testvalue varchar,"
                        + "uploadstate varchar)");
                Log.i("zxdebug", "insert new user to local database success");
            }
        } catch (Exception e) {
            Log.e("zxdebug", Log.getStackTraceString(e));
        }
    }


    public static void updateTestData(String tableName, String valueSet) {
        String[] values = valueSet.split("#");
        SQLiteDatabase db = instance.getWritableDatabase();
        Log.e("zxdebug", "Start update testdata at local database.");
        try {
            db.execSQL("insert into " + tableName + "(testtime,testvalue,uploadstate) values('" + values[0] + "','" + values[1] + "','no')");
            Log.e("zxdebug", "Update testdata at local database success.");
        } catch (Exception e) {
            Log.e("zxdebug", Log.getStackTraceString(e));
        }
    }

}
