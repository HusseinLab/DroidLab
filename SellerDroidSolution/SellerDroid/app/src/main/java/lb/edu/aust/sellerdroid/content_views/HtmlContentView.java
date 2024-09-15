package lb.edu.aust.sellerdroid.content_views;

import android.webkit.WebView;

import lb.edu.aust.sellerdroid.BaseActivity;
import lb.edu.aust.sellerdroid.BaseContentView;
import lb.edu.aust.sellerdroid.MainActivity;
import lb.edu.aust.sellerdroid.R;
/**
 * Created by AUST Student on 18/12/2015.
 */
public class HtmlContentView extends BaseContentView {

    WebView webView;

    public HtmlContentView(BaseActivity activity, String html) {
        super(activity, R.layout.cv_html);

        webView = (WebView) findViewById(R.id.webView);

        webView.loadData(html,
                "text/html",
                "utf8"
        );
    }
}
