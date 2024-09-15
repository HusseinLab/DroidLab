package lb.edu.aust.sellerdroid.content_views;

import android.webkit.WebView;

import lb.edu.aust.sellerdroid.BaseActivity;
import lb.edu.aust.sellerdroid.MainActivity;
import lb.edu.aust.sellerdroid.R;

/**
 * Created by AUST Student on 18/12/2015.
 */
public class AboutContentView extends HtmlContentView {

    public AboutContentView(BaseActivity activity) {
        super(activity,
                "<html>" +
                        "<body>" +
                        "<h1 style='color:Navy'>About SellerDroid</h1>" +
                        "<p>SellerDroid is a revolutionary <b>shopping search app</b> for products in <b>local</b> stores across Lebanon! </p>" +
                        "<body>" +
                "</html>");
    }
}
