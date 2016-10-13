package it.polito.mad.noticeboard.advertiser.publish;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseException;

import java.util.ArrayList;

import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db.Advertiser;
import it.polito.mad.noticeboard.db.DBCallbacks;
import it.polito.mad.noticeboard.db.Notice;
import it.polito.mad.noticeboard.db.NoticeBoardDB;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;

public class AddNoticeActivity extends ActionBarActivity
        implements DBCallbacks.UpdateCallback<Notice> {

    private PNoticeData data = new PNoticeData();

    private ViewPager pager;
    private Fragment[] pages = new Fragment[]{
            new Page1(), new Page2()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return pages[position];
            }
            @Override
            public int getCount() {
                return pages.length;
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_adv_notices_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_publish_notice:
                //here 'data' is populated with user typed data from all 3 pages
                for (Fragment page : pages){
                    if (page instanceof NoticeUpdater){
                        ((NoticeUpdater) page).update(data);
                    }
                }
                NoticeBoardDB.publishNewNotice(data, this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdateError(ParseException exception) {
        Toast.makeText(this, "error while publishing notice", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateSuccess(Notice newNotice) {
        //PNoticeData wrapper = new PNoticeData();
        //wrapper.fillFrom(newNotice);
        Intent backIntent = new Intent();
        //backIntent.putExtra("notice", wrapper);
        setResult(Activity.RESULT_OK, backIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, backIntent);
        finish();
    }

}
