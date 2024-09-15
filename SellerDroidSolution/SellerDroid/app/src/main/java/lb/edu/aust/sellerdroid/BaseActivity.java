package lb.edu.aust.sellerdroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

import lb.edu.aust.sellerdroid.components.SellerDroidClient;
import lb.edu.aust.sellerdroid.content_views.AboutContentView;
import lb.edu.aust.sellerdroid.content_views.HelpContentView;
import lb.edu.aust.sellerdroid.content_views.LoginContentView;
import lb.edu.aust.sellerdroid.content_views.RegisterContentView;
import lb.edu.aust.sellerdroid.content_views.SearchByTextContentView;
import lb.edu.aust.sellerdroid.content_views.SearchResultContentView;
import lb.edu.aust.sellerdroid.content_views.SearchSettingsContentView;
import lb.edu.aust.sellerdroid.dto.ProductSummary;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static int SEARCH_BY_IMAGE_RESULT_CODE = 1010;
    public final static int SEARCH_BY_BARCODE_RESULT_CODE = 1020;
    public final static int SEARCH_BY_VOICE_RESULT_CODE = 1030;

    public static SellerDroidClient sellerDroidClient = new SellerDroidClient("http://192.168.1.106:56565");

    Stack<BaseContentView> viewStack;
    NavigationView navigationView;

    String temporaryCapturedImage;
    AlertDialog progressDialog;

    @Override
    public void setContentView(int resourceId) {
        FrameLayout included_content = (FrameLayout) findViewById(R.id.included_content);
        included_content.removeAllViewsInLayout();
        LayoutInflater.from(this).inflate(resourceId, included_content);
    }

    public void setContentView(BaseContentView contentView) {
        if(!viewStack.contains(contentView))
            viewStack.push(contentView);
        FrameLayout included_content = (FrameLayout) findViewById(R.id.included_content);
        included_content.removeAllViewsInLayout();
        included_content.addView(contentView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    protected void getImage(int code) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File image = getOutputMediaFile();
            temporaryCapturedImage = image.getAbsolutePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
            startActivityForResult(intent, code);
        }
        catch (Exception ex) {
            Toast.makeText(this, "Could not launch camera", Toast.LENGTH_SHORT).show();
        }
    }

    TextView txt_welcome, txt_welcome_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main_layout);

        // Set the default/saved base url for the service client
        sellerDroidClient.setBaseUrl(SearchSettingsContentView.getBaseUrl(this));

        viewStack = new Stack<BaseContentView>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            final String searchType = SearchSettingsContentView.getPreferredSearchType(BaseActivity.this);
            // In case the search is for text
            if ("Text".equals(searchType)) {
                CreateContentView(SearchByTextContentView.class);
            } else if ("Voice".equals(searchType)) {
                promptSpeechInput();
            } else if("Barcode".equals(searchType)) {
                Intent intent = new Intent(BaseActivity.this, BarcodeScannerActivity.class);
                startActivityForResult(intent, SEARCH_BY_BARCODE_RESULT_CODE);
            } else {
                int code = searchType.equals("Image")
                        ? SEARCH_BY_IMAGE_RESULT_CODE
                        : SEARCH_BY_BARCODE_RESULT_CODE;
                getImage(code);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Showing google speech input dialog
     * */
    protected void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What are you looking for?");
        try {
            startActivityForResult(intent, SEARCH_BY_VOICE_RESULT_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this,"Speech Recognition on this device is not supported", Toast.LENGTH_SHORT).show();
        }
    }

    public View DeepFind(ViewGroup parent, int resourceId, Class tClass) {
        for(int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if(child.getId() ==  resourceId && tClass.isInstance(child))
                return child;
            else
                if(ViewGroup.class.isInstance(child)) {
                    View found = DeepFind((ViewGroup) child, resourceId, tClass);
                    if(found != null)
                        return found;
                }
        }
        return null;
    }

    public void FindWelcomeText() {
        FrameLayout included_content = (FrameLayout) findViewById(R.id.included_content);
        txt_welcome = (TextView)DeepFind(included_content, R.id.txt_welcome, TextView.class);
        txt_welcome_2 = (TextView)DeepFind(included_content, R.id.txt_welcome_2, TextView.class);
    }

    public void setLoggedIn(String username) {
        FindWelcomeText();
        if(txt_welcome != null) {
            txt_welcome.setText("You are logged in as " + username);
            txt_welcome_2.setText(txt_welcome_2.getHint());
        }

        navigationView.getMenu().getItem(0).setVisible(false);
        navigationView.getMenu().getItem(1).setTitle("Logout " + username);
    }

    public void setLoggedOut() {
        FindWelcomeText();
        if(txt_welcome != null) {
            txt_welcome.setText(txt_welcome.getHint());
            txt_welcome_2.setText("Register or login to begin working!");
        }
        navigationView.getMenu().getItem(0).setVisible(true);
        navigationView.getMenu().getItem(1).setTitle("Login");
    }



    public BaseContentView PopContentView()
    {
        BaseContentView popped = null;

        if(!viewStack.empty()) {
            popped = viewStack.pop();
            if(!viewStack.empty())
                setContentView(viewStack.peek());
            else
                setContentView(R.layout.content_main);
        }

        return popped;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (viewStack.empty())
                super.onBackPressed();
            else {
                PopContentView();
            }
        }
    }

    public void showProgress(final String title, final String message)
    {
        if(progressDialog == null)
        {
            progressDialog = ProgressDialog.show(this, title, message, true, false);
        }
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setTitle(title);
                    progressDialog.setMessage(message);
                }
            });
        }
    }

    public void hideProgress()
    {
        if(progressDialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            });
        }
    }

    public byte[] getScaledCapturedImage() throws java.io.IOException
    {
        Bitmap captured = BitmapFactory.decodeFile(temporaryCapturedImage);
        Bitmap resized = Bitmap.createScaledBitmap(captured, 800, captured.getHeight() * 800 / captured.getWidth(), true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        captured.recycle();
        resized.recycle();
        return stream.toByteArray();
    }

    @Override
    protected void onActivityResult(final int requestCode, int requestResult, final Intent data)
    {
        super.onActivityResult(requestCode, requestResult, data);

        switch (requestCode) {
            case SEARCH_BY_BARCODE_RESULT_CODE:
                if (requestResult == RESULT_OK && data != null) {
                    final String barcode = data.getStringExtra("BARCODE");

                    showProgress("Barcode Search", "Searching for barcode:\n" + barcode + "\nPlease Wait....");
                    AsyncTask task = new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                        final ProductSummary[] results = MainActivity.sellerDroidClient
                                .doSearchByBarcode(barcode);
                        if (results != null && results.length > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                final ArrayList<ProductSummary> productSummaries = new ArrayList<ProductSummary>();
                                for (ProductSummary p : results)
                                    productSummaries.add(p);
                                SearchResultContentView resultsView = new SearchResultContentView(BaseActivity.this, productSummaries);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                Toast.makeText(BaseActivity.this, "No results for barcode " + barcode, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        hideProgress();
                        return results;
                        }
                    };
                    task.execute();
                }
                break;
            case SEARCH_BY_IMAGE_RESULT_CODE:
                if(requestResult == RESULT_OK)
                {
                    try {
                        final byte[] imageBytes = getScaledCapturedImage();
                        showProgress("Search", "Search in progress, please wait....");
                        AsyncTask task = new AsyncTask() {
                            @Override
                            protected Object doInBackground(Object[] params) {
                                final String searchMode = requestCode == SEARCH_BY_IMAGE_RESULT_CODE ? "Image" : "Barcode";
                                final ProductSummary[] results = MainActivity.sellerDroidClient
                                        .doSearchByImage(searchMode, imageBytes);
                                if (results != null && results.length > 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final ArrayList<ProductSummary> productSummaries = new ArrayList<ProductSummary>();
                                            for (ProductSummary p : results)
                                                productSummaries.add(p);
                                            SearchResultContentView resultsView = new SearchResultContentView(BaseActivity.this, productSummaries);
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(BaseActivity.this, "No results for search", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                hideProgress();
                                return results;
                            }
                        };

                        task.execute();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(BaseActivity.this, "Error processing search by image:" + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                break;

            case SEARCH_BY_VOICE_RESULT_CODE:
                if (requestResult == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    new SearchByTextContentView(BaseActivity.this, result.get(0));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.nav_Login:
                if(sellerDroidClient.getLoggedInUser() != null) {
                    // Logging Out
                    showProgress("Logout", "Logging out...");
                    AsyncTask task = new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            Boolean done = sellerDroidClient.doLogout();
                            if(done) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setLoggedOut();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(BaseActivity.this, "Could not log out. Check your connection.", Toast.LENGTH_LONG).show();
                            }
                            hideProgress();
                            return done;
                        }
                    };
                    task.execute();
                }
                else {
                    CreateContentView(LoginContentView.class);
                }
                break;
            case R.id.nav_About:
                CreateContentView(AboutContentView.class);
                break;
            case R.id.nav_Help:
                CreateContentView(HelpContentView.class);
                break;
            case R.id.nav_Search_Type:
                CreateContentView(SearchSettingsContentView.class);
                break;
            case  R.id.nav_Register:
                CreateContentView(RegisterContentView.class);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    protected BaseContentView CreateContentView(Class type) {
        BaseContentView result = null;
        for (BaseContentView pushed : viewStack) {
            if (pushed.getClass().equals(type)) {
                result = pushed;
                setContentView(pushed);
            }
        }
        if (result == null) {
            try {
                result = (BaseContentView) ((type.getDeclaredConstructors())[0]).newInstance(this);
            } catch(Exception ex) {
            }
        }
        return result;
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SellerDroid");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }
}