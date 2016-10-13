package it.polito.mad.noticeboard.db;

import android.os.AsyncTask;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import it.polito.mad.noticeboard.db.DBCallbacks.DownloadCallback;

/**
 * Created by luigi on 26/05/15.
 */
public class DownloadTask<T extends ParseObject> extends AsyncTask<Object, Void, List<T>> {

    private ParseQuery<T> parseQuery;
    private DownloadCallback<T> mCallback;

    public DownloadTask(ParseQuery<T> query, int limit, DownloadCallback<T> callback){
        parseQuery = query;
        parseQuery.setLimit(limit);
        mCallback = callback;
    }

    public DownloadTask(ParseQuery<T> query, DownloadCallback<T> callback){
        this(query, -1,callback);
    }

    public DownloadTask(int limit, ParseQuery query){
        this(query, limit, null);
    }

    @Override
    protected List<T> doInBackground(Object... params) {
        try{
            return parseQuery.find();
        }
        catch(Exception e){ }
        return null;
    }



    @Override
    protected void onPostExecute(List<T> result) {
        super.onPostExecute(result);
        if (mCallback != null){
            if (result == null) {
                mCallback.onDownloadError();
            }
            else {
                mCallback.onNewDataReturned(result);
            }
        }
    }
}