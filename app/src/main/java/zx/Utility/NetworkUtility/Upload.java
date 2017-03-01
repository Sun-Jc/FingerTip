package zx.Utility.NetworkUtility;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import zx.Utility.DataObject;
import zx.Utility.DatabaseUtility.DbHelper;
import zx.Utility.DatabaseUtility.User;

/**
 * Created by ZX on 2016/3/2.
 */
public class Upload implements Runnable {
    public void run() {
        try {
            Log.e("zxdebug", "Start update data.");
            String username = User.getUsername();
            Log.e("zxdebug", "Start query local unuploaded data");
            String resultSet = "";
            resultSet += DbHelper.queryTestData(username.split("@")[0] + username.split("@")[1].split("\\.")[0]);
            if(resultSet!=""){
                Log.e("zxdebug", "Start update testdata to server.");
                ServerConnector.getOutputStream().writeUTF("update#" + username + "#" + resultSet);
                String respond = ServerConnector.getInputStream().readUTF();
                if(respond.split("#")[1].equals("success")){
                    Log.e("zxdebug", "Update to server success.");
                    // Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
                }else{
                    Log.e("zxdebug", "Update to server fail.");
                }
            }else{
                Log.e("zxdebug", "No data.");
            }

        } catch (IOException e) {
            Log.e("zxdebug", Log.getStackTraceString(e));
        } catch (Exception e) {
            Log.e("zxdebug", Log.getStackTraceString(e));
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.toString() != "") {
                Bundle b = msg.getData();
                //txt.setText(b.getString("test"));
            }
        }
    };

}
