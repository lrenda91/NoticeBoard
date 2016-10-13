package it.polito.mad.noticeboard.advertiser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import it.polito.mad.noticeboard.EditableFragment;
import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db.NoticeBoardDB;
import it.polito.mad.noticeboard.db_parcel.PAdvertiserData;

/**
 * Created by luigi on 01/06/15.
 */
public class AdvertiserProfileFragment extends Fragment implements EditableFragment {

    private View root;
    private ViewSwitcher[] switchableViews;
    private EditText firstNameET, lastNameET, mailET, phoneET;
    private TextView nameTV, mailTV, phoneTV;

    private PAdvertiserData editingData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_homeowner_profile_infos, container, false);
        switchableViews = new ViewSwitcher[] {
                (ViewSwitcher) root.findViewById(R.id.completeNameViewSwitcher),
                (ViewSwitcher) root.findViewById(R.id.mailViewSwitcher),
                (ViewSwitcher) root.findViewById(R.id.phoneViewSwitcher)
        };
        firstNameET = (EditText) root.findViewById(R.id.first_name_edit_text);
        lastNameET = (EditText) root.findViewById(R.id.last_name_edit_text);
        mailET = (EditText) root.findViewById(R.id.mailEditText);
        phoneET = (EditText) root.findViewById(R.id.phone_edit_text);
        nameTV = (TextView) root.findViewById(R.id.completeNameTextView);
        mailTV = (TextView) root.findViewById(R.id.mailTextView);
        phoneTV = (TextView) root.findViewById(R.id.phone_text_view);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editingData = ((AdvertiserMainActivity) getActivity()).editingData;

        //SET VALUES FOR NON EDITABLE VIEWS
        nameTV.setText(editingData.getFirstName() + " " + editingData.getLastName());
        mailTV.setText(editingData.geteMail());
        phoneTV.setText(editingData.getPhoneNumber());

        //SET VALUES FOR EDITABLE VIEWS
        firstNameET.setText(editingData.getFirstName());
        lastNameET.setText(editingData.getLastName());
        mailET.setText(editingData.geteMail());
        phoneET.setText(editingData.getPhoneNumber());
    }

    @Override
    public void passToEditMode(boolean editMode) {
        if (!editMode){
            //get new user-typed data
            String fName = firstNameET.getText().toString();
            String lName = lastNameET.getText().toString();
            String mail = mailET.getText().toString();
            String phone = phoneET.getText().toString();

            //set fixed widgets
            nameTV.setText(fName + " " + lName);
            mailTV.setText(mail);
            phoneTV.setText(phone);

            //finally update data structure
            editingData.firstName(fName)
                    .lastName(lName)
                    .eMail(mail)
                    .phoneNumber(phone);
            NoticeBoardDB.updateHomeownerData(editingData, null);
        }

        //in any case, switch all views
        for (final ViewSwitcher vs : switchableViews){
            vs.getCurrentView().animate().alpha(0f).setDuration(400)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            vs.getNextView().setAlpha(0f);
                            vs.showNext();
                            vs.getCurrentView().animate().alpha(1f).setDuration(400).setListener(null).start();
                        }
                    });
        }
    }
}
