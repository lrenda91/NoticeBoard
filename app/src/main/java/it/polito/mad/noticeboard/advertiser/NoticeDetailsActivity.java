package it.polito.mad.noticeboard.advertiser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.parse.ParseException;

import it.polito.mad.noticeboard.EditableFragment;
import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db.DBCallbacks;
import it.polito.mad.noticeboard.db.Notice;
import it.polito.mad.noticeboard.db.NoticeBoardDB;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;
import it.polito.mad.noticeboard.db_parcel.PStudentData;

public class NoticeDetailsActivity extends AppCompatActivity {

    private ViewSwitcher[] switchers;

    PNoticeData noticeData;

    private boolean editMode = false;
    private Fragment gallery, infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertiser_notice_details);
        switchers = new ViewSwitcher[]{
                (ViewSwitcher) findViewById(R.id.title_view_switcher),
                (ViewSwitcher) findViewById(R.id.description_view_switcher)
        };

        noticeData = getIntent().getParcelableExtra("noticeData");

        gallery = getSupportFragmentManager().findFragmentById(R.id.gallery_fragment);
        infos = getSupportFragmentManager().findFragmentById(R.id.infos_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuRes = editMode ?
                R.menu.menu_adv_notice_details_editable : R.menu.menu_adv_notice_details;
        getMenuInflater().inflate(menuRes, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchEditMode(boolean editable){
        editMode = editable;
        /*if (!editMode){
            String newTitle = titleET.getText().toString();
            noticeData.title(newTitle);
            titleTV.setText(newTitle);
            String newDescription = descriptionET.getText().toString();
            noticeData.description(newDescription);
            descriptionTV.setText(newDescription);
        }*/
        for (final ViewSwitcher vs : switchers) {
            vs.getCurrentView().animate().alpha(0f).setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            vs.getNextView().setAlpha(0f);
                            vs.showNext();
                            vs.getCurrentView().animate().alpha(1f)
                                    .setDuration(300).setListener(null).start();
                        }
                    });
        }
        if (gallery instanceof EditableFragment){
            ((EditableFragment) gallery).passToEditMode(editable);
        }
        if (infos instanceof EditableFragment){
            ((EditableFragment) infos).passToEditMode(editable);
        }

        if (!editMode){
            NoticeBoardDB.updateNoticeData(noticeData, new DBCallbacks.UpdateCallback<Notice>() {
                @Override
                public void onUpdateSuccess(Notice updated) {

                }

                @Override
                public void onUpdateError(ParseException exception) {

                }
            });
        }
        invalidateOptionsMenu();
    }

}
