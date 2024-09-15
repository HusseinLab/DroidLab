package lb.edu.aust.sellerdroid.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by AUST Student on 18/12/2015.
 */
public class GenericAdapter<T> extends BaseAdapter {

    public interface IViewPopulator<T> {
        void populate(LinearLayout linearLayout, T item);
    }

    Context _context;
    List<T> _objects;
    int _listViewItemResourceId;
    IViewPopulator<T> _populator;

    public GenericAdapter(Context context, Collection<T> objects, int listViewItemResourceId, IViewPopulator<T> populator) {
        super();
        _context = context;
        _listViewItemResourceId = listViewItemResourceId;
        _objects = new ArrayList<>();
        _objects.addAll(objects);
        _populator = populator;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout;

        if(convertView != null)
            layout = (LinearLayout) convertView;
        else
            layout = (LinearLayout) LayoutInflater.from(_context).inflate(_listViewItemResourceId, null);

        _populator.populate(layout, _objects.get(position));

        return layout;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public T get(int position) {
        return _objects.get(position);
    }

    @Override
    public Object getItem(int position) {
        return _objects.get(position);
    }

    @Override
    public int getCount() {
        return _objects.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }
}
