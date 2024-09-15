package lb.edu.aust.sellerdroid.content_views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import lb.edu.aust.sellerdroid.BaseActivity;
import lb.edu.aust.sellerdroid.BaseContentView;
import lb.edu.aust.sellerdroid.MainActivity;
import lb.edu.aust.sellerdroid.R;
import lb.edu.aust.sellerdroid.adapters.GenericAdapter;
import lb.edu.aust.sellerdroid.dto.Product;
import lb.edu.aust.sellerdroid.dto.ProductSummary;

/**
 * Created by AUST Student on 18/12/2015.
 */
public class SearchResultContentView extends BaseContentView {

    ListView lvSearchResults;

    public SearchResultContentView(final BaseActivity activity, Collection<ProductSummary> items) {

        super(activity, R.layout.cv_search_results);

        lvSearchResults = (ListView)findViewById(R.id.lvSearchResults);

        final GenericAdapter<ProductSummary> adapter = new GenericAdapter<>(activity, items, R.layout.lvi_search_result_item, new GenericAdapter.IViewPopulator<ProductSummary>() {
            @Override
            public void populate(LinearLayout container, ProductSummary item) {
            TextView txtName = (TextView)container.findViewById(R.id.txtName);
            txtName.setText(item.name);

            TextView txtDescription = (TextView)container.findViewById(R.id.txtDescription);
            txtDescription.setText(item.description);

            final ImageView imageView = (ImageView)container.findViewById(R.id.imageView);
            if(item.imageUri != null) {
                imageView.setVisibility(VISIBLE);
                final String imageUrl = BaseActivity.sellerDroidClient.getBaseUrl() + item.imageUri;
                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            URL url = new URL(imageUrl);
                            final Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(bitmap);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                task.execute();
            }
            else {
                imageView.setVisibility(INVISIBLE);
            }
            }
        });

        lvSearchResults.setAdapter(adapter);
        lvSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.showProgress("Product", "Please wait, loading product details....");
                final int itemPos = position;
                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        final ProductSummary productSummary = adapter.get(itemPos);
                        final Product product = BaseActivity.sellerDroidClient.doGetProduct(productSummary.id);
                        if (product != null && product.id != null)
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new ProductContentView(activity, product);
                                }
                            });
                        else
                            Toast.makeText(activity, "Could not load details for " + product.name, Toast.LENGTH_LONG).show();
                        activity.hideProgress();
                        return product;
                    }
                };
                task.execute();
            }
        });

        TextView txtSearchResults = (TextView)findViewById(R.id.txtSearchResults);
        txtSearchResults.setText(txtSearchResults.getText().toString().replace("0", Integer.toString(items.size())));
    }
}