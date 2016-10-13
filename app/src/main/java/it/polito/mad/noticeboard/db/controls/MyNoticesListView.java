package it.polito.mad.noticeboard.db.controls;

import android.content.Context;

import com.parse.ParseQuery;

import it.polito.mad.noticeboard.db.Notice;
import it.polito.mad.noticeboard.db.NoticeBoardDB;

/**
 * Created by luigi on 25/06/15.
 */
public class MyNoticesListView extends AdvNoticesListView {

    public MyNoticesListView(Context context) {
        super(context);
    }

    @Override
    protected ParseQuery<Notice> getQuery() {
        ParseQuery<Notice> query = ParseQuery.getQuery(Notice.class);
        query.whereEqualTo("owner", NoticeBoardDB.getCurrentAdvertiser());
        return query;
    }
}
