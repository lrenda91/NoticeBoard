package it.polito.mad.noticeboard;

import android.app.Application;

import java.util.Locale;

import it.polito.mad.noticeboard.db.Advertiser;
import it.polito.mad.noticeboard.db.Notice;
import it.polito.mad.noticeboard.db.NoticeBoardDB;
import it.polito.mad.noticeboard.db.Report;
import it.polito.mad.noticeboard.db.Student;

/**
 * Created by luigi on 29/05/15.
 */
public class NoticeBoardApp extends Application {

    /**
     * Application startup. First, initialize Parse DB environment
     * and register subclasses
     */
    @Override
    public void onCreate() {
        super.onCreate();

        NoticeBoardDB.initialize(this,
                Student.class,
                Advertiser.class,
                Notice.class,
                Report.class);
        Util.registerExceptionHandler();
    }

}
