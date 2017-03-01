package zx.Network.Controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class Signup extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private int resultType = 0;
    private String name = null;
    private String email = null;
    private String password = null;
    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed("");
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Signup.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        name = _nameText.getText().toString();
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.
        Log.i("zxdebug", "start signup");
        new Thread() {
            public void run() {
                try {
                    DataOutputStream outputStream = null;
                    DataInputStream inputStream = null;
                    String Statement = null;
                    String[] strarray = null;
                    outputStream = ServerConnector.getOutputStream();
                    inputStream = ServerConnector.getInputStream();
                    if (outputStream != null) {
                        Statement = "signup" + "#" + email + "#" + password;
                        Log.i("zxdebug", "start sending signup request");
                        outputStream.writeUTF(Statement);
                        if (inputStream != null) {
                            Statement = inputStream.readUTF();
                            strarray = Statement.split("#");
                            if (strarray[0] .equals("signup")  && strarray[1] .equals("success") ) {
                                // TODO: add database logic here
                                Log.i("zxdebug", "signup success, start insert user to local database");
                                User.setUsername(email);
                                DbHelper.updateUserData(email + "#yes");
                                resultType=1;
                            } else if (strarray[0] .equals("signup")  && strarray[1] .equals("fail") ) {
                                resultType=2;
                            }
                        } else {
                            resultType=3;
                        }
                    } else {
                        resultType=3;
                    }
                } catch (IOException e) {
                    Log.e("zxdebug", Log.getStackTraceString(e));
                }catch (Exception e) {
                    Log.e("zxdebug", Log.getStackTraceString(e));
                }
            }
        }.start();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        if (resultType == 1) {
                            onSignupSuccess();
                        } else if(resultType==2){
                            onSignupFailed("Cannot create user.");
                        }else{
                            onSignupFailed("Network unavailable.");
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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