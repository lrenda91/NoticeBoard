package it.polito.mad.noticeboard.student.search;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;

import java.util.LinkedList;
import java.util.List;

import it.polito.mad.noticeboard.HomeActivity;
import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.Util;
import it.polito.mad.noticeboard.db.DBCallbacks;
import it.polito.mad.noticeboard.db.Notice;
import it.polito.mad.noticeboard.db.NoticeBoardDB;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;
import it.polito.mad.noticeboard.student.MyInfoDialog;
import it.polito.mad.noticeboard.student.SortDialog;

public class BrowseActivity extends AppCompatActivity implements
        DBCallbacks.FilterCallback<Notice>,
        BrowseChoiceFragment.BrowseModeChangeCallback,
        BrowseChoiceFragment.SimpleSearchCallback,
        UpdateRequestReceiver, SortDialog.SortingCallback,
        DBCallbacks.UserLogoutCallback {

    public static final int LIST_MODE = 0;
    public static final int MAP_MODE = 1;

    private List<PNoticeData> allData = new LinkedList<>();

    private Fragment[] fragments = new Fragment[]{
        new MyListFragment(), new MyMapFragment()
    };
    private int curMode = -1;
    private int cursor = 0;
    private final int perDL = 5;

    private ProgressBar waitPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_browse);
        waitPB = (ProgressBar) findViewById(R.id.wait);
        waitPB.setVisibility(View.VISIBLE);
        onUpdateRequestReceived();
    }


    //replaces current fragment basing on the current working mode
    @Override
    public void onOffersBrowseModeChanged(int mode) {
        if (curMode != mode){
            curMode = mode;
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.frame_holder, fragments[mode]).commit();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof NoticesListener){
            ((NoticesListener) fragment).update(allData);
        }
    }

    @Override
    public void onDataFiltered(List<Notice> result) {
        List<PNoticeData> list = new LinkedList<>();
        for (Notice n : result){
            PNoticeData pNoticeData = new PNoticeData();
            pNoticeData.fillFrom(n);
            list.add(pNoticeData);
        }
        for (Fragment f : fragments) {
            if (f instanceof NoticesListener) {
                ((NoticesListener) f).update(list);
            }
        }
        waitPB.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSortingComplete(List<Notice> notices) {
        onDataFiltered(notices);
        waitPB.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFilterError(ParseException exception) {
        Toast.makeText(this,"Error:"+exception.getMessage(), Toast.LENGTH_SHORT).show();
        waitPB.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSimpleSearch(int mode, final String param) {
        if (!Util.checkNetworkUp(this)){
            return;
        }
        switch (mode){
            case LIST_MODE:
                NoticeBoardDB.simpleNoticeFilter(param, null, null, null,
                        new DBCallbacks.FilterCallback<Notice>() {
                    @Override
                    public void onDataFiltered(List<Notice> result) {
                        Toast.makeText(BrowseActivity.this, result.size()+" notices found matching '"+
                                        param+"'",
                                Toast.LENGTH_SHORT).show();
                        if (fragments[LIST_MODE] instanceof NoticesListener) {
                            List<PNoticeData> list = new LinkedList<>();
                            for (Notice n : result){
                                PNoticeData pNoticeData = new PNoticeData();
                                pNoticeData.fillFrom(n);
                                list.add(pNoticeData);
                            }
                            ((NoticesListener) fragments[LIST_MODE]).update(list);
                        }
                        waitPB.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onFilterError(ParseException exception) {
                        waitPB.setVisibility(View.INVISIBLE);
                        Toast.makeText(BrowseActivity.this,"Error:"+exception.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case MAP_MODE:
                String locationName = param;
                LatLng pos = Util.getFirstAddress(this, locationName);
                Double lat = (pos != null) ? pos.latitude : null;
                Double lng = (pos != null) ? pos.longitude : null;
                NoticeBoardDB.simpleNoticeFilter(null, locationName, lat, lng,
                        new DBCallbacks.FilterCallback<Notice>() {
                            @Override
                            public void onDataFiltered(List<Notice> result) {
                                Toast.makeText(BrowseActivity.this, result.size()+" notices found near '"+
                                                param+"'",
                                        Toast.LENGTH_SHORT).show();
                                if (fragments[MAP_MODE] instanceof NoticesListener) {
                                    List<PNoticeData> list = new LinkedList<>();
                                    for (Notice n : result){
                                        PNoticeData pNoticeData = new PNoticeData();
                                        pNoticeData.fillFrom(n);
                                        list.add(pNoticeData);
                                    }
                                    ((NoticesListener) fragments[MAP_MODE]).update(list);
                                }
                                waitPB.setVisibility(View.INVISIBLE);
                            }
                            @Override
                            public void onFilterError(ParseException exception) {
                                waitPB.setVisibility(View.INVISIBLE);
                            }
                        });
                waitPB.setVisibility(View.VISIBLE);
                break;
        }
    }




    @Override
    public void onUpdateRequestReceived() {
        if (!Util.checkNetworkUp(this)){
            return;
        }
        NoticeBoardDB.downloadNewNoticeChunk(perDL, cursor, new DBCallbacks.DownloadCallback<Notice>() {
            @Override
            public void onNewDataReturned(List<Notice> newData) {
                List<PNoticeData> list = new LinkedList<>();
                for (Notice n : newData) {
                    PNoticeData pNoticeData = new PNoticeData();
                    pNoticeData.fillFrom(n);
                    list.add(pNoticeData);
                }
                allData.addAll(list);
                for (Fragment f : fragments) {
                    if (f instanceof NoticesListener) {
                        ((NoticesListener) f).update(allData);
                    }
                }
                cursor += perDL;

                waitPB.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDownloadError() {
                Toast.makeText(BrowseActivity.this, "Can't download new notices",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onLogoutSuccess() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onLogoutError(ParseException exception) {

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10){
            switch(resultCode){
                case Activity.RESULT_OK:
                    if (!Util.checkNetworkUp(this)){
                        return;
                    }
                    Notice.FilterData searchParams = (Notice.FilterData) data.getSerializableExtra("params");
                    NoticeBoardDB.advancedNoticeFilter(searchParams, this);
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_offers_browse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_adv_search:
                Intent toAdvSearchPage = new Intent(BrowseActivity.this, AdvancedSearchActivity.class);
                startActivityForResult(toAdvSearchPage, 10);
                break;
            case R.id.action_info:
                new MyInfoDialog(this).show();
                break;
            case R.id.action_logout:
                NoticeBoardDB.logOut(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
