package lb.edu.aust.sellerdroid.content_views;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import lb.edu.aust.sellerdroid.BaseActivity;
import lb.edu.aust.sellerdroid.BaseContentView;
import lb.edu.aust.sellerdroid.MainActivity;
import lb.edu.aust.sellerdroid.R;

/**
 * Created by AUST Student on 18/12/2015.
 */
public class SearchSettingsContentView extends BaseContentView {

    Switch sw_Image, sw_Barcode, sw_ProductName, sw_Voice;

    EditText txtHostAddress;

    Switch[] switches;

    public static void SaveSetting(Context context, String settingName, String value)
    {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .putString(settingName, value)
                .commit();
    }

    public static String GetSetting(Context context, String settingName, String defaultValue)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(settingName, defaultValue);
    }

    public static String getPreferredSearchType(Context context)
    {
        return GetSetting(context, "preferredSearchType", "Text");
    }

    public static String getBaseUrl(Context context) {
        return GetSetting(context, "baseURL", "http://192.168.173.1:56565");
    }

    public SearchSettingsContentView(final BaseActivity activity) {

        super(activity, R.layout.cv_search_settings);

        txtHostAddress = (EditText)findViewById(R.id.txtHostAddress);
        txtHostAddress.setText(BaseActivity.sellerDroidClient.getBaseUrl());
        Button btnChangeHost = (Button)findViewById(R.id.btnChangeHost);
        btnChangeHost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String baseUrl = txtHostAddress.getText().toString().trim();
                BaseActivity.sellerDroidClient.setBaseUrl(baseUrl);
                SaveSetting(activity, "baseURL", baseUrl);
                Toast.makeText(activity, "Server address changed", Toast.LENGTH_SHORT).show();
            }
        });

        String preferredSearchType = getPreferredSearchType(activity);

        sw_Image = (Switch)findViewById(R.id.sw_Image);
        sw_Barcode = (Switch)findViewById(R.id.sw_Barcode);
        sw_ProductName = (Switch)findViewById(R.id.sw_ProductName);
        sw_Voice = (Switch)findViewById(R.id.sw_Voice);


        sw_Image.setTag("Image");
        sw_Barcode.setTag("Barcode");
        sw_ProductName.setTag("Text");
        sw_Voice.setTag("Voice");

        switches = new Switch[]
        {
            sw_Image,
            sw_Barcode,
            sw_ProductName,
            sw_Voice
        };

        for(Switch sw : switches) {
            sw.setChecked(sw.getTag().toString().equals(preferredSearchType));
        }

        for(Switch sw : switches) {
            sw.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Switch clickedSwitch = (Switch) v;
                    if (clickedSwitch.isChecked()) {
                        for (Switch otherSw : switches) {
                            if (otherSw != clickedSwitch) {
                                otherSw.setChecked(false);
                            }
                        }
                        SaveSetting(_activity, "preferredSearchType", clickedSwitch.getTag().toString());
                    } else {
                        sw_Image.performClick();
                    }
                }
            });
        }
    }
}
