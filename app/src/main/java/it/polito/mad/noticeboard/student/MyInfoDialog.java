package it.polito.mad.noticeboard.student;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db.NoticeBoardDB;
import it.polito.mad.noticeboard.db_parcel.PStudentData;

/**
 * Created by luigi on 15/06/15.
 */
public class MyInfoDialog extends Dialog {

    private PStudentData editingData = new PStudentData();
    private boolean editMode = false;
    private ViewSwitcher[] switchableViews;


    public MyInfoDialog(Context context) {
        super(context);
        setContentView(R.layout.layout_dialog_student_info);

        editingData.fillFrom(NoticeBoardDB.getCurrentStudent());
        ((TextView) findViewById(R.id.completeNameTextView)).setText(
                editingData.getFirstName() + " " + editingData.getLastName());
        ((TextView) findViewById(R.id.mailTextView)).setText(editingData.getEmail());
        ((EditText) findViewById(R.id.firstNameEditText)).setText(editingData.getFirstName());
        ((EditText) findViewById(R.id.lastNameEditText)).setText(editingData.getLastName());
        ((EditText) findViewById(R.id.mailEditText)).setText(editingData.getEmail());

        setTitle("My informations");

        switchableViews = new ViewSwitcher[] {
                (ViewSwitcher) findViewById(R.id.completeNameViewSwitcher),
                (ViewSwitcher) findViewById(R.id.mailViewSwitcher)
        };

        ToggleButton tb = (ToggleButton)findViewById(R.id.editButton);
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchEditMode(isChecked);
                buttonView.setBackgroundResource(isChecked ? R.drawable.ic_edit : R.drawable.ic_done);
            }
        });

    }

    private void switchEditMode(boolean editable){
        for (final ViewSwitcher vs : switchableViews){
            vs.getCurrentView().animate().alpha(0f).setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            vs.getNextView().setAlpha(0f);
                            vs.showNext();
                            vs.getCurrentView().animate().alpha(1f).setDuration(300).setListener(null).start();
                        }
                    });
        }
        if (!editable){
            String firstName = ((EditText) findViewById(R.id.firstNameEditText)).getText().toString();
            String lastName = ((EditText) findViewById(R.id.lastNameEditText)).getText().toString();
            String mail = ((EditText) findViewById(R.id.mailEditText)).getText().toString();
            ((TextView) findViewById(R.id.completeNameTextView)).setText(
                    firstName + " " + lastName);
            ((TextView) findViewById(R.id.mailTextView)).setText(mail);
            editingData.firstName(firstName).lastName(lastName).eMail(mail);
            NoticeBoardDB.updateStudentData(editingData, null);
        }
        editMode = editable;
    }
}
