package com.example.contactsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class Register extends AppCompatActivity {

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    EditText etname, etmail, etpassword, etreenter;
    Button btnregister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mLoginFormView = (LinearLayout) findViewById(R.id.login_form);
        mProgressView = (ProgressBar) findViewById(R.id.login_progress);
        tvLoad = (TextView) findViewById(R.id.tvLoad);

        etname= (EditText) findViewById(R.id.etname);
        etmail= (EditText) findViewById(R.id.etmail);
        etpassword = (EditText) findViewById(R.id.etpassword);
        etreenter= (EditText) findViewById(R.id.etreenter);
        btnregister = (Button) findViewById(R.id.btnregister);

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String jina = etname.getText().toString().trim();
                String mail = etmail.getText().toString().trim();
                String pass = etpassword.getText().toString().trim();
                String repass = etreenter.getText().toString().trim();

              /* if (etname.getText().toString().isEmpty() || etmail.getText().toString().isEmpty() ||
                       etpassword.getText().toString().isEmpty() || etreenter.getText().toString().isEmpty())*/
              if (jina.isEmpty() || mail.isEmpty() || pass.isEmpty() || repass.isEmpty())
               {
                   Toast.makeText(Register.this, "Please enter all fields!!!!!", Toast.LENGTH_SHORT).show();
               }
                else
                {
                    if (etpassword.getText().toString().equals(etreenter.getText().toString()))
                    {
                        String name = etname.getText().toString().trim();
                        String email = etmail.getText().toString().trim();
                        String password = etpassword.getText().toString().trim();

                        BackendlessUser user = new BackendlessUser();
                        user.setEmail(email);
                        user.setPassword(password);
                        user.setProperty("name" /* name of the coloumn in the database */, name);

                        showProgress(true);
                        tvLoad.setText("Busy registering user....Please wait"); //changing the details of the pre loader

                        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser response) {

                                showProgress(false);
                                Toast.makeText(Register.this, "User successfully registered !!", Toast.LENGTH_LONG).show();
                                Register.this.finish(); //closes this activity and takes you back to the previous activity.....login
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {   //if there is an error in registering the user it will return a message which we will show to the user

                                Toast.makeText(Register.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                showProgress(false);
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(Register.this, "Please  make sure that your password and re-type password is the same!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    /**
     * Shows the progress UI and hides the login form.
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
