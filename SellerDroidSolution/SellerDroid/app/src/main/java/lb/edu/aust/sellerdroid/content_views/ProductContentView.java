package lb.edu.aust.sellerdroid.content_views;

import android.media.Rating;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import lb.edu.aust.sellerdroid.BaseActivity;
import lb.edu.aust.sellerdroid.BaseContentView;
import lb.edu.aust.sellerdroid.MainActivity;
import lb.edu.aust.sellerdroid.R;
import lb.edu.aust.sellerdroid.dto.Product;
import lb.edu.aust.sellerdroid.dto.ProductSummary;
import lb.edu.aust.sellerdroid.dto.Supplier;

/**
 * Created by AUST Student on 20/12/2015.
 */
public class ProductContentView extends BaseContentView {

    Product _product;
    WebView webView;
    Button btnViewSupplier;
    TextView txtProductName, txtYourRating, txtAverageRating;
    RatingBar rbAverage;
    RatingBar rbYours;

    public ProductContentView(final BaseActivity activity, final Product product) {
        super(activity, R.layout.cv_product_details);

        this._product = product;

        txtProductName = (TextView)findViewById(R.id.txtProductName);
        txtAverageRating=(TextView)findViewById(R.id.txtAverageRating);
        txtYourRating = (TextView)findViewById(R.id.txtYourRating);
        webView = (WebView) findViewById(R.id.webView);
        btnViewSupplier = (Button) findViewById(R.id.btnViewSupplier);
        rbAverage = (RatingBar)findViewById(R.id.rbAverage);
        rbYours = (RatingBar)findViewById(R.id.rbYours);

        txtProductName.setText(product.name);

        UpdateProductAverageRating();

        rbYours.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {

                if(!fromUser)
                    return;

                if(BaseActivity.sellerDroidClient.getLoggedInUser() == null) {
                    Toast.makeText(activity, "Cannot Rate product. Please Login first.", Toast.LENGTH_SHORT).show();
                    ProductContentView.this.UpdateProductAverageRating();
                    return;
                }

                product.currentUserRating = rating;
                AsyncTask task = new AsyncTask() {
                    @SuppressWarnings("ResourceType")
                    @Override
                    protected Object doInBackground(Object[] params) {
                        product.averageRating = BaseActivity.sellerDroidClient
                                .UpdateMyProductRating(product.id, product.currentUserRating);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "Rating of " + rating + " set for " + product.name, Toast.LENGTH_SHORT).show();
                            }
                        });
                        ProductContentView.this.UpdateProductAverageRating();
                        return product.averageRating;
                    }
                };
                task.execute();
            }
        });

        StringBuilder html = new StringBuilder();
        html.append(product.longDescription != null ? product.longDescription : product.description);

        if(product.imageUri != null)
            html.insert(0,"<img src='"+ _product.imageUri + "' border='1' style='float:right' width=64 hspace=5 vspace=5 />");

        webView.loadDataWithBaseURL(
                BaseActivity.sellerDroidClient.getBaseUrl(),
                html.toString(),
                "text/html",
                "utf8",
                null
        );

        btnViewSupplier.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showProgress("Supplier", "Loading supplier details, please wait....");
                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        final Supplier supplier = MainActivity.sellerDroidClient.doGetSupplier(product.supplier.id);
                        if (supplier != null && supplier.id != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new HtmlContentView(activity, "<html><h1>" + product.supplier.name + "</h1>" +
                                            supplier.description +
                                            "</html>");
                                }
                            });
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Could not load supplier details!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        activity.hideProgress();
                        return supplier;
                    }
                };

                task.execute();
            }
        });
    }

    void UpdateProductAverageRating()
    {
        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(_product.averageRating >= 0) {
                    rbAverage.setRating(_product.averageRating);
                    txtAverageRating.setText("Average Rating: " + _product.averageRating);
                } else {
                    rbAverage.setRating(0);
                    txtAverageRating.setText("Not Rated");
                }

                if(_product.currentUserRating >= 0) {
                    rbYours.setRating(_product.currentUserRating);
                    txtYourRating.setText("Your rating: " + _product.currentUserRating);
                } else {
                    rbYours.setRating(0);
                    txtYourRating.setText("Rate this Product:");
                }
            }
        });
    }
}
