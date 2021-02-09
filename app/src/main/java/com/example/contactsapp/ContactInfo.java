package com.example.contactsapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class ContactInfo extends AppCompatActivity {

    TextView tvchar, tvname;
    ImageView ivcall, ivmail, ivedit, ivdelete;
    EditText etname, etmail, etphone;
    Button btnsubmit;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    Boolean edit = false; //for editing our data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        tvchar = findViewById(R.id.tvchar);
        tvname = findViewById(R.id.tvname);

        ivcall = findViewById(R.id.ivcall);
        ivmail = findViewById(R.id.ivmail);
        ivedit = findViewById(R.id.ivedit);
        ivdelete = findViewById(R.id.ivdelete);

        etname = findViewById(R.id.etname);
        etmail = findViewById(R.id.etmail);
        etphone = findViewById(R.id.etphone);

        btnsubmit = findViewById(R.id.btnsubmit);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        etname.setVisibility(View.GONE);
        etmail.setVisibility(View.GONE);
        etphone.setVisibility(View.GONE);
        btnsubmit.setVisibility(View.GONE);

        final int index = getIntent().getIntExtra("index", 0);

        etname.setText(Application.contacts.get(index).getName());
        etmail.setText(Application.contacts.get(index).getEmail());
        etphone.setText(Application.contacts.get(index).getNumber());

        tvchar.setText(Application.contacts.get(index).getName().toUpperCase().charAt(0)+"");
        tvname.setText(Application.contacts.get(index).getName());

        ivcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = "tel:" + Application.contacts.get(index).getNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);

            }
        });

        ivmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, Application.contacts.get(index).getEmail());
                startActivity(Intent.createChooser(intent,
                        "Send mail to " + Application.contacts.get(index).getName()));

            }
        });

        ivedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit = !edit;

                if (edit)
                {
                    etname.setVisibility(View.VISIBLE);
                    etmail.setVisibility(View.VISIBLE);
                    etphone.setVisibility(View.VISIBLE);
                    btnsubmit.setVisibility(View.VISIBLE);
                }
                else
                {
                    etname.setVisibility(View.GONE);
                    etmail.setVisibility(View.GONE);
                    etphone.setVisibility(View.GONE);
                    btnsubmit.setVisibility(View.GONE);
                }
            }
        });

        ivdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dialog = new AlertDialog.Builder(ContactInfo.this);
                dialog.setMessage("Are you sure you want to delete the contact?");

                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        showProgress(true);
                        tvLoad.setText("Deleting contact...please wait...");

                        Backendless.Persistence.of(Contact.class).remove(Application.contacts.get(index), new AsyncCallback<Long>() {
                            @Override
                            public void handleResponse(Long response) {

                                Application.contacts.remove(index);
                                Toast.makeText(ContactInfo.this, "Contact successfully deleted!", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                ContactInfo.this.finish();

                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {

                                Toast.makeText(ContactInfo.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                showProgress(false);

                            }
                        });

                    }
                });

                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialog.show();

            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etname.getText().toString().isEmpty() || etmail.getText().toString().isEmpty() ||
                    etphone.getText().toString().isEmpty())
                {
                    Toast.makeText(ContactInfo.this, "Please enter all details!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Application.contacts.get(index).setName(etname.getText().toString().trim());
                    Application.contacts.get(index).setEmail(etmail.getText().toString().trim());
                    Application.contacts.get(index).setNumber(etphone.getText().toString().trim());

                    showProgress(true);
                    tvLoad.setText("Updating contact...Please wait...");

                    Backendless.Persistence.save(Application.contacts.get(index), new AsyncCallback<Contact>() {
                        @Override
                        public void handleResponse(Contact response) {

                            tvchar.setText(Application.contacts.get(index).getName().toUpperCase().charAt(0) + "");
                            tvname.setText(Application.contacts.get(index).getName());

                            Toast.makeText(ContactInfo.this, "Contact succesfully updated!", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Toast.makeText(ContactInfo.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);

                        }
                    });
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
