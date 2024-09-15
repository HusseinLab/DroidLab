package lb.edu.aust.sellerdroid.content_views;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;

import lb.edu.aust.sellerdroid.BaseActivity;
import lb.edu.aust.sellerdroid.BaseContentView;
import lb.edu.aust.sellerdroid.R;
import lb.edu.aust.sellerdroid.dto.Product;
import lb.edu.aust.sellerdroid.dto.ProductSummary;

/**
 * Created by fachamieh on 2016-02-26.
 */
public class SearchByTextContentView extends BaseContentView {

    public SearchByTextContentView(final BaseActivity activity, String text) {
        super(activity, R.layout.cv_search_by_text);
        initialize(activity, text);
    }

    void initialize(final BaseActivity activity, String text) {
        Button btnSearch = (Button)findViewById(R.id.btnSearch);
        Button btnCancel = (Button)findViewById(R.id.btnCancel);
        final EditText txtSearchFor = (EditText)findViewById(R.id.txtSearchFor);

        if(text != null) txtSearchFor.setText(text);

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.PopContentView();
            }
        });

        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String searchText = txtSearchFor.getText().toString();

                if (searchText == null || searchText.trim().length() < 2){
                    Toast.makeText(activity, "Please specify something to search for", Toast.LENGTH_SHORT).show();
                    return;
                }

                activity.showProgress("Search", "Searching by text, please wait...");

                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        ProductSummary[] productSummaries = BaseActivity.sellerDroidClient.doSearchProducts(searchText.trim());
                        activity.hideProgress();
                        if (productSummaries == null || productSummaries.length == 0) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "No products found", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            final ArrayList<ProductSummary> products = new ArrayList<ProductSummary>();
                            for (ProductSummary ps : productSummaries) products.add(ps);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new SearchResultContentView(activity, products);
                                }
                            });
                        }
                        return null;
                    }
                }.execute();
            }
        });
    }

    public SearchByTextContentView(final BaseActivity activity) {
        super(activity, R.layout.cv_search_by_text);
        initialize(activity, null);
    }
}