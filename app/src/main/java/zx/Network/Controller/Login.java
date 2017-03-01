package zx.Network.Controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import sunjc.materialdesigntest.MainActivity;
import sunjc.materialdesigntest.R;
import zx.Utility.DatabaseUtility.DbHelper;
import zx.Utility.NetworkUtility.ServerConnector;
import zx.Utility.DatabaseUtility.User;

public class Login extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private int resultType = 0;
    //private final DbHelper db_helper = new DbHelper(getApplicationContext(), "heartrate.db", null, 1);

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), Signup.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d("zxdebug", "Login");
        if (!validate()) {
            onLoginFailed("Login Failed");
            return;
        }
        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Login.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();


        // TODO: Implement your own authentication logic here.
        Log.d("zxdebug", "start authenticating");

        new Thread()
        {
            public void run()
            {
                DataOutputStream outputStream = null;
                DataInputStream inputStream = null;
                String Statement = null;
                String[] strarray = null;
                //访问网络代码
                try {
                    outputStream = ServerConnector.getOutputStream();
                    inputStream = ServerConnector.getInputStream();
                    if (outputStream != null) {
                        Statement = "login" + "#" + email + "#" + password;
                        Log.d("zxdebug", "start sending login request");
                        outputStream.writeUTF(Statement);
                        if (inputStream != null) {
                            Statement = inputStream.readUTF();
                            strarray = Statement.split("#");
                            if (strarray[0].equals("login")  && strarray[1] .equals("success") ) {
                                // TODO: add database logic here
                                Log.i("zxdebug", "login success, start update local user data 'online'");
                                User.setUsername(email);
                                DbHelper.updateUserData(email + "#online#yes");
                                resultType=1;//Login success
                            } else if (strarray[0].equals("login")  && strarray[1] .equals("fail") ) {
                                resultType=2;//Login Fail
                            }
                        } else {
                            resultType=3;//Login Fail
                        }
                    } else {
                        resultType=3;
                    }
                } catch (IOException e) {
                    Log.e("zxdebug", Log.getStackTraceString(e));
                }
                catch (Exception e) {
                    Log.e("zxdebug", Log.getStackTraceString(e));
                }
            }
        }.start();

       new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (resultType == 1) {
                            onLoginSuccess();
                        } else if(resultType==2){
                            onLoginFailed("Wrong username or password.");
                        }else{
                            onLoginFailed("Network unavailable.");
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    private void showResult(final int type, final String message) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (type == 1) {
                            onLoginSuccess();
                        } else {
                            onLoginFailed(message);
                        }

                    }
                }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
