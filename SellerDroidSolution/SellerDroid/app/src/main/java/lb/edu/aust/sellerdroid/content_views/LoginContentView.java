package lb.edu.aust.sellerdroid.content_views;

import lb.edu.aust.sellerdroid.BaseActivity;
import lb.edu.aust.sellerdroid.BaseContentView;
import lb.edu.aust.sellerdroid.MainActivity;
import lb.edu.aust.sellerdroid.R;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by AUST Student on 18/12/2015.
 */
public class LoginContentView extends BaseContentView {

    EditText txtUsername;
    EditText txtPassword;
    Button btnLogin;

    public LoginContentView(final BaseActivity activity) {
        super(activity, R.layout.cv_login);

        txtUsername = (EditText)findViewById(R.id.txtUsername);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        txtUsername.setText(SearchSettingsContentView.GetSetting(activity, "Username", ""));
        txtPassword.setText(SearchSettingsContentView.GetSetting(activity, "Password", ""));

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.showProgress("Login", "Login in progress. Please wait...");

                final String username = txtUsername.getText().toString();
                final String password = txtPassword.getText().toString();

                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        final String response = MainActivity.sellerDroidClient.doLogin(username, password);
                        if(response != null)
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                Toast.makeText(activity, "Login: " + response, Toast.LENGTH_LONG).show();
                                if("Success".equals(response)){
                                    activity.PopContentView();
                                    activity.setLoggedIn(username);
                                    SearchSettingsContentView.SaveSetting(activity, "Username", username);
                                    SearchSettingsContentView.SaveSetting(activity, "Password", password);
                                }
                                }
                            });
                        activity.hideProgress();
                        return response;
                    }
                };

                task.execute();
            }
        });

        /*
         try {
                    Intent cropIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, _mainActivity.getApplicationContext().getCacheDir().getAbsolutePath());
                    cropIntentg.putExtra("crop", "true");
                    cropIntent.putExtra("aspectX", 0);
                    cropIntent.putExtra("aspectY", 0);
                    cropIntent.putExtra("outputX", 320);
                    cropIntent.putExtra("outputY", 240);
                    cropIntent.putExtra("return-data", true);
                    _mainActivity.startActivityForResult(cropIntent, MainActivity.PICTURE_RESULT_CODE);
                }
                // respond to users whose devices do not support the crop action
                catch (ActivityNotFoundException anfe) {
                    // display an error message
                    String errorMessage = "Whoops - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(_mainActivity, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
        */
    }
}
