package it.polito.mad.noticeboard.advertiser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseException;

import java.util.List;

import it.polito.mad.noticeboard.HomeActivity;
import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db.DBCallbacks.*;
import it.polito.mad.noticeboard.db.NoticeBoardDB;
import it.polito.mad.noticeboard.advertiser.publish.AddNoticeActivity;
import it.polito.mad.noticeboard.db.Report;
import it.polito.mad.noticeboard.db_parcel.PAdvertiserData;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;

public class AdvertiserMainActivity extends ActionBarActivity
        implements UserLogoutCallback, MultipleFetchCallback<Report> {

    PAdvertiserData editingData = new PAdvertiserData();

    private static final int PUBLISH_REQUEST_CODE = 10;

    private ViewPager pager;
    private boolean editMode = false;

    private AdvertiserProfileFragment profile = new AdvertiserProfileFragment();
    private AdvertiserNoticesListFragment list = new AdvertiserNoticesListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeowner_main_page);

        editingData.fillFrom(NoticeBoardDB.getCurrentAdvertiser());

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }
        });
        pager.setAdapter(new CustomPagerAdapter(getSupportFragmentManager(), this));

        Bundle b = getIntent().getExtras();
        if (getIntent().getBooleanExtra("fromNotification",false)){
            pager.setCurrentItem(1);
        }
        else{
            NoticeBoardDB.lookForNewReport(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuLayoutID = 0;
        switch (pager.getCurrentItem()){
            case 0:
                menuLayoutID = editMode ? R.menu.menu_adv_profile_editable :
                        R.menu.menu_adv_profile;
                break;
            case 1:
                menuLayoutID = R.menu.menu_adv_notices_add;
                break;
        }
        getMenuInflater().inflate(menuLayoutID, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                switchEditMode(true);
                break;
            case R.id.action_edit_done:
                switchEditMode(false);
                break;
            case R.id.action_add_notice:
                Intent toPublishActivity = new Intent(this, AddNoticeActivity.class);
                startActivityForResult(toPublishActivity, PUBLISH_REQUEST_CODE);
                break;
            case R.id.action_logout:
                NoticeBoardDB.logOut(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PUBLISH_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    list.refreshList();
                    Toast.makeText(this, "successfully published", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "publish notice cancelled", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
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
    public void onFetchSuccess(List<Report> result) {
        if (!result.isEmpty())
            sendBroadcast(new Intent(this, NewReportsBReceiver.class));
    }

    @Override
    public void onFetchError(ParseException exception) {

    }


    class CustomPagerAdapter extends FragmentPagerAdapter {
        Context mContext;
        public CustomPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return profile;
                case 1:
                    return list;
            }
            return null;
        }
        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0: return getResources().getString(R.string.my_info);
                case 1: return getResources().getString(R.string.my_notices);
            }
            return "";
        }
    }

    private void switchEditMode(boolean editable){
        profile.passToEditMode(editable);
        editMode = editable;
        invalidateOptionsMenu();
    }
}
