package lb.edu.aust.sellerdroid.content_views;

import lb.edu.aust.sellerdroid.BaseActivity;
import lb.edu.aust.sellerdroid.MainActivity;

/**
 * Created by AUST Student on 18/12/2015.
 */
public class HelpContentView extends HtmlContentView {

    public HelpContentView(BaseActivity activity) {
        super(activity,
                "<html>" +
                        "<body>" +
                        "<h1 style='color:Green'>Help me</h1>" +
                        "<p>Are you a client looking for certain item or products in Lebanon</p>" +
                        "<ul>" +
                        "<li>You can use our App to search for specific items</li>" +
                        "<li> you can choose one of those searching methods </li>" +
                        "<li>Search by Image</li>" +
                        "<li>Search by BarCode</li>" +
                        "<li>Search by voice commands</li>" +
                        "<li>& Search by Text</li>" +
                        "</ul>" +
                        "<p> Are you a supplier or a retailer! <br> You can register as a supplier on <b>SellerDroid.com</b> and place your products for sale <p>" +
                        "<body>" +
                        "</html>");
    }
}
