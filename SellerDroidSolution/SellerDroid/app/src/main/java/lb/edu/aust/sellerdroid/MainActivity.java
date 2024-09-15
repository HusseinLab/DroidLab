package lb.edu.aust.sellerdroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import java.util.ArrayList;

import lb.edu.aust.sellerdroid.components.SellerDroidClient;
import lb.edu.aust.sellerdroid.content_views.AboutContentView;
import lb.edu.aust.sellerdroid.content_views.HelpContentView;
import lb.edu.aust.sellerdroid.content_views.LoginContentView;
import lb.edu.aust.sellerdroid.content_views.RegisterContentView;
import lb.edu.aust.sellerdroid.content_views.SearchResultContentView;
import lb.edu.aust.sellerdroid.content_views.SearchSettingsContentView;
import lb.edu.aust.sellerdroid.dto.Product;
import lb.edu.aust.sellerdroid.dto.ProductSummary;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
    }

}