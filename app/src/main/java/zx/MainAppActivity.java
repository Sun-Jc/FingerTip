package zx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import sunjc.materialdesigntest.MainActivity;
import sunjc.materialdesigntest.R;
import zx.Network.Controller.Login;
import zx.Utility.DatabaseUtility.DbHelper;
import zx.Utility.DatabaseUtility.User;

/**
 * Created by Lesley on 2016/3/11.
 */
public class MainAppActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        //setContentView(R.layout.database_test_father);
        DbHelper.initiateDatabase(this);
        final String online_user = DbHelper.queryUserData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (online_user != "") {
                    User.setUsername(online_user);
                    Intent intent = new Intent(MainAppActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainAppActivity.this, Login.class);
                    startActivity(intent);
                    //finish();
                }
               // MainAppActivity.this.finish();
            }
        }, 3000);
    }
}