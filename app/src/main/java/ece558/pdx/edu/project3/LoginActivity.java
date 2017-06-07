package ece558.pdx.edu.project3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Hiral on 7/28/2016.
 */

/**
 * A login screen that offers login via email/password.
 * Provides Functionality to Signup for new account
 */
public class LoginActivity extends AppCompatActivity {

    // UI references
    private EditText mEmail;
    private EditText mPasswordView;
    Button mSignInButton;
    Button mSignUpButton;
    LoginDataBaseAdapter loginDataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Creates object for LoginDataBaseAdapter to gain access to database
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();

        mEmail = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        //OnClick Listener for SignInButton
        Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // Code below gets the User name and Password
                String userName = mEmail.getText().toString();
                String password = mPasswordView.getText().toString();
                /*
                 * TODO_DONE: Code here to fetch the Password from database for respective user name
                 */
                String passwordDB = loginDataBaseAdapter.getSingleEntry(userName);

                if (password.equals(passwordDB)) {
                    Intent intent = LocationUpdate.newIntent(LoginActivity.this, userName);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Username or password does not match", Toast.LENGTH_SHORT).show();
                }
            }
        });



        Button mSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignUPActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        loginDataBaseAdapter.close();
    }
}