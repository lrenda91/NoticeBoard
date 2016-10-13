package it.polito.mad.noticeboard.advertiser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import it.polito.mad.noticeboard.EditableFragment;
import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.Util;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;

/**
 * Created by luigi on 09/06/15.
 */
public class NoticeInfosFragment extends Fragment implements EditableFragment {

    private View root;

    private ViewSwitcher[] switchableViews;

    private TextView titleTV, descriptionTV, costTV, locTV, typeTV, contractTV;
    private EditText titleET, descriptionET;

    private SeekBar costSB;
    private AutoCompleteTextView locationAC;
    private Spinner typeSpinner, contractSpinner;

    private PNoticeData noticeData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notice_infos_editable, container, false);

        String[] labels = {"Title","Description","Cost", "Location", "Type", "Contract"};
        int[] layouts = {
                R.id.title_layout,
                R.id.description_layout,
                R.id.cost_layout,
                R.id.location_layout,
                R.id.type_layout,
                R.id.contract_layout};
        for (int i=0;i<layouts.length;i++){
            ((TextView) root.findViewById(layouts[i]).findViewById(R.id.name)).setText(labels[i]);
        }

        //get views for values in EDIT mode
        titleET = (EditText) root.findViewById(R.id.title_editable);
        descriptionET = (EditText) root.findViewById(R.id.description_editable);
        costSB = (SeekBar) root.findViewById(R.id.cost_editable);
        locationAC = (AutoCompleteTextView) root.findViewById(R.id.location_editable);
        Util.setAutoCompleteGMaps(locationAC);
        typeSpinner = (Spinner) root.findViewById(R.id.type_editable);
        contractSpinner = (Spinner) root.findViewById(R.id.contract_editable);

        costSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView) root.findViewById(R.id.cost_value)).setText(progress+" euros");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        //get view for values in NON-EDIT mode
        titleTV = (TextView) root.findViewById(R.id.title_layout).findViewById(R.id.value);
        descriptionTV = (TextView) root.findViewById(R.id.description_layout).findViewById(R.id.value);
        costTV = (TextView) root.findViewById(R.id.cost_layout).findViewById(R.id.value);
        locTV = (TextView) root.findViewById(R.id.location_layout).findViewById(R.id.value);
        typeTV = (TextView) root.findViewById(R.id.type_layout).findViewById(R.id.value);
        contractTV = (TextView) root.findViewById(R.id.contract_layout).findViewById(R.id.value);
        return root;
    }

    private int getIndex(Spinner spinner, String myString){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        noticeData = ((NoticeDetailsActivity) getActivity()).noticeData;

        switchableViews = new ViewSwitcher[] {
                (ViewSwitcher) root.findViewById(R.id.title_view_switcher),
                (ViewSwitcher) root.findViewById(R.id.description_view_switcher),
                (ViewSwitcher) root.findViewById(R.id.cost_view_switcher),
                (ViewSwitcher) root.findViewById(R.id.location_view_switcher),
                (ViewSwitcher) root.findViewById(R.id.type_view_switcher),
                (ViewSwitcher) root.findViewById(R.id.contract_view_switcher)
        };

        int[] layouts1 = {
                R.id.title_layout,
                R.id.description_layout,
                R.id.cost_layout,
                R.id.location_layout,
                R.id.type_layout,
                R.id.contract_layout};
        String[] values = {
                noticeData.getTitle(),
                noticeData.getDescription(),
                noticeData.getCost()+"",
                noticeData.getLocation(),
                noticeData.getPropertyType(),
                noticeData.getContractType()
        };

        //SET VALUES FOR NON EDITABLE VIEWS
        for (int i=0;i<layouts1.length;i++){
            ((TextView) root.findViewById(layouts1[i]).findViewById(R.id.value)).setText(values[i]);
        }

        //SET VALUES FOR EDITABLE VIEWS
        titleET.setText(noticeData.getTitle());
        descriptionET.setText(noticeData.getDescription());
        costSB.setProgress(noticeData.getCost());
        locationAC.setText(noticeData.getLocation());
        typeSpinner.setSelection(getIndex(typeSpinner, noticeData.getPropertyType()));
        contractSpinner.setSelection(getIndex(contractSpinner, noticeData.getContractType()));

    }

    @Override
    public void passToEditMode(boolean editMode) {
        if (!editMode){
            //get new user-typed data
            String newTitle = titleET.getText().toString();
            String newDescription = descriptionET.getText().toString();
            int newCost = costSB.getProgress();
            String newLocation = locationAC.getText().toString();
            String newType = typeSpinner.getSelectedItem().toString();
            String newContract = contractSpinner.getSelectedItem().toString();

            //set fixed widgets
            titleTV.setText(newTitle);
            descriptionTV.setText(newDescription);
            costTV.setText(""+newCost+" euros");
            locTV.setText(newLocation);
            typeTV.setText(newType);
            contractTV.setText(newContract);

            //finally update data structure
            LatLng coordinates = Util.getFirstAddress(getActivity(), newLocation);
            if (coordinates != null){
                noticeData.latitude(coordinates.latitude).longitude(coordinates.longitude);
            }
            noticeData.title(newTitle)
                    .description(newDescription)
                    .cost(newCost)
                    .location(newLocation)
                    .propertyType(newType)
                    .contractType(newContract);
        }

        //in any case, switch all views
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
    }

}
