package lb.edu.aust.sellerdroid;

import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * Created by AUST Student on 18/12/2015.
 */
public abstract class BaseContentView extends FrameLayout {

    protected BaseActivity _activity;

    protected BaseContentView(BaseActivity activity, int layoutResourceId)
    {
        super(activity);
        _activity = activity;
        LayoutInflater.from(activity).inflate(layoutResourceId, this);
        activity.setContentView(this);
    }

}
