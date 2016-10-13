package it.polito.mad.noticeboard.db.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.LinkedList;
import java.util.List;

import it.polito.mad.noticeboard.db.DBCallbacks;
import it.polito.mad.noticeboard.db.DownloadTask;

/**
 * Created by luigi on 27/05/15.
 */
public abstract class FillOnScrollListView<T extends ParseObject> extends ListView {

    private OnItemClickListener mItemClickListener;
    private List<T> data = new LinkedList<>();
    private ParseQuery<T> query;
    private DownloadTask<T> download = null;
    private int cursor = 0;

    private final DBCallbacks.DownloadCallback<T> mCallback = new DBCallbacks.DownloadCallback<T>() {
        @Override
        public void onNewDataReturned(List<T> newData) {
            synchronized (FillOnScrollListView.this){
                data.addAll(newData);
                ((BaseAdapter) getAdapter()).notifyDataSetChanged();
                download = null;
            }
        }
        @Override
        public void onDownloadError() {

        }
    };

    protected abstract View getView(T item, View convertView, ViewGroup parent);
    protected abstract int getItemsPerDownload();
    protected abstract ParseQuery<T> getQuery();

    private void init(){
        query = getQuery();
        if (query == null){
            throw new RuntimeException("Query mustn't be null!!");
        }
        query.setLimit(getItemsPerDownload());
        setAdapter(new MyAdapter());
        setOnScrollListener(new MyScroller());
    }

    public FillOnScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FillOnScrollListView(Context context) {
        this(context, null, 0);
    }

    public FillOnScrollListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void refresh(){
        ParseQuery<T> refreshQuery = getQuery();
        new DownloadTask(refreshQuery, cursor, new DBCallbacks.DownloadCallback<T>() {
            @Override
            public void onNewDataReturned(List<T> newData) {
                synchronized (FillOnScrollListView.this){
                    data = newData;
                    ((BaseAdapter) getAdapter()).notifyDataSetChanged();
                }
            }
            @Override
            public void onDownloadError() {

            }
        }).execute();
    }

    public List<T> getData() {
        return data;
    }

    @Override
    public BaseAdapter getAdapter() {
        return (BaseAdapter) super.getAdapter();
    }

    public synchronized void setData(List<T> data) {
        this.data = data;
        ((BaseAdapter)getAdapter()).notifyDataSetChanged();
    }

    private class MyAdapter extends BaseAdapter {
        public int getCount() { return data.size(); }
        public T getItem(int pos) { return data.get(pos); }
        public long getItemId(int pos) { return pos; }
        public View getView(int pos, View convertView, ViewGroup parent) {
            T item = getItem(pos);
            return FillOnScrollListView.this.getView(item, convertView, parent);
        }
    }


    private class MyScroller implements AbsListView.OnScrollListener {
        private int priorFirstVisible = -1;
        @Override
        public void onScroll(AbsListView view,
        int firstVisible, int visibleCount, int totalCount) {
            if (firstVisible == priorFirstVisible){
                return;
            }
            priorFirstVisible = firstVisible;
            synchronized (this){
                boolean mustLoadMore = firstVisible + visibleCount >= data.size();
                if (mustLoadMore && download == null){
                    download = new DownloadTask(query, getItemsPerDownload(), mCallback);
                    query.setSkip(cursor);
                    download.execute();
                    cursor += getItemsPerDownload();
                }
            }

        }
        @Override
        public void onScrollStateChanged(AbsListView v, int s) { }
    }

}
