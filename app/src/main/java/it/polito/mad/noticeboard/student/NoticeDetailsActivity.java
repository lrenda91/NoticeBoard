package it.polito.mad.noticeboard.student;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseException;

import java.io.PrintWriter;
import java.io.StringWriter;

import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db.DBCallbacks;
import it.polito.mad.noticeboard.db.Notice;
import it.polito.mad.noticeboard.db.NoticeBoardDB;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;


public class NoticeDetailsActivity extends AppCompatActivity
        implements DBCallbacks.SingleFetchCallback<Notice>,DBCallbacks.SetFavoriteCallback {

    PNoticeData noticeData;

    private boolean loaded = false;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_details);

        noticeData = getIntent().getParcelableExtra("noticeData");

        if (savedInstanceState != null){
            loaded = savedInstanceState.getBoolean("load");
            isFavorite = savedInstanceState.getBoolean("flag");
        }

        NoticeBoardDB.getFavorite(noticeData.getObjID(), this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null){
            outState.putBoolean("load", loaded);
            outState.putBoolean("flag", isFavorite);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            loaded = savedInstanceState.getBoolean("load");
            isFavorite = savedInstanceState.getBoolean("flag");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuID = 0;
        if (!loaded){
            menuID = R.menu.menu_home;
        }
        else {
            menuID = isFavorite ? R.menu.menu_notice_details_favorite :
                    R.menu.menu_notice_details_non_favorite;
        }
        getMenuInflater().inflate(menuID, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_bookmark:
                NoticeBoardDB.toggleFavoriteNotice(noticeData.getObjID(), this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFetchSuccess(Notice result) {
        loaded = true;
        isFavorite = true;
        invalidateOptionsMenu();
    }

    @Override
    public void onFetchError(ParseException exception) {
        loaded = true;
        isFavorite = false;
        invalidateOptionsMenu();
    }

    @Override
    public void onSetFavoriteSuccess(boolean added) {
        loaded = true;
        isFavorite = added;
        String msg = added ? "This notice was added to your favorites" :
                "This notice was removed from your favorites";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        invalidateOptionsMenu();
    }

    @Override
    public void onSetFavoriteError(ParseException exception) {

    }
}
