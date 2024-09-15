package lb.edu.aust.sellerdroid.content_views;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import lb.edu.aust.sellerdroid.BaseActivity;
import lb.edu.aust.sellerdroid.BaseContentView;
import lb.edu.aust.sellerdroid.MainActivity;
import lb.edu.aust.sellerdroid.R;
import lb.edu.aust.sellerdroid.dto.RegistrationRequest;


/**
 * Created by AUST Student on 18/12/2015.
 */
public class RegisterContentView extends BaseContentView {

    Button btnRegister;
    EditText txtName, txtEmail, txtPassword, txtConfirmPassword;

    public RegisterContentView(final BaseActivity activity) {

        super(activity, R.layout.cv_register);

        btnRegister = (Button)findViewById(R.id.btnRegister);
        txtName = (EditText)findViewById(R.id.txtName);
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText)findViewById(R.id.txtConfirmPassword);

        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final RegistrationRequest registrationRequest = new RegistrationRequest();
                registrationRequest.Name = txtName.getText().toString();
                registrationRequest.Email = txtEmail.getText().toString();
                registrationRequest.Password = txtPassword.getText().toString();
                registrationRequest.ConfirmPassword = txtConfirmPassword.getText().toString();

                if(registrationRequest.Name.length() < 3) { txtName.setError("Full name is required"); return; }
                if(registrationRequest.Email.length() < 5) { txtEmail.setError("Email is required"); return; }
                if(registrationRequest.Password.length() < 5) { txtPassword.setError("Password of at least 5 characters is required"); return; }
                if(!registrationRequest.ConfirmPassword.equals(registrationRequest.Password)) { txtConfirmPassword.setError("Please confirm password"); return; }

                activity.showProgress("Register", "Registration in progress. Please wait...");

                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        String response = MainActivity.sellerDroidClient.doRegister(registrationRequest);
                        if(response == null) response = "Error: no response from server. Please check your connection.";
                        final String message = response;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "Registration: " + message, Toast.LENGTH_SHORT).show();
                                if("Success".equals(message))
                                    activity.PopContentView();
                            }
                        });

                        activity.hideProgress();
                        return response;
                    }
                };

                task.execute();
            }
        });
    }
}