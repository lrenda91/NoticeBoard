package it.polito.mad.noticeboard.advertiser.publish;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;

import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.Util;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;


public class Page1 extends Fragment implements NoticeUpdater {

    private Spinner contractTypeSpinner, propertyTypeSpinner;
    private AutoCompleteTextView locationEditText;
    private EditText titleEditText, descrEditText, costEditText;
    private DatePicker datePicker;
    private NumberPicker np;
    private RadioGroup contractRG, propertyRG;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_page1, container, false);
        contractTypeSpinner = (Spinner) root.findViewById(R.id.contract_type);
        propertyTypeSpinner = (Spinner) root.findViewById(R.id.property_type);
        locationEditText = (AutoCompleteTextView) root.findViewById(R.id.location);
        Util.setAutoCompleteGMaps(locationEditText);
        titleEditText = (EditText) root.findViewById(R.id.title);
        descrEditText = (EditText) root.findViewById(R.id.description);
        datePicker = (DatePicker) root.findViewById(R.id.availableDatePicker);
        costEditText = (EditText) root.findViewById(R.id.price);
        contractRG = (RadioGroup) root.findViewById(R.id.contract_type_radio_group);
        propertyRG = (RadioGroup) root.findViewById(R.id.property_type_radio_group);

        np = (NumberPicker) root.findViewById(R.id.size_number_picker);
        int min = 50, max = 2000, step = 50;
        Util.setValuesWithStep(np, min, max, step);
        np.setWrapSelectorWheel(true);
        return root;
    }

    @Override
    public void update(PNoticeData data) {
        String typedLocation = locationEditText.getText().toString();
        RadioButton contractRB = (RadioButton) contractRG.findViewById(contractRG.getCheckedRadioButtonId());
        RadioButton propertyRB = (RadioButton) propertyRG.findViewById(propertyRG.getCheckedRadioButtonId());
        data.title(titleEditText.getText().toString())
                .description(descrEditText.getText().toString())
                .availableFrom(
                    datePicker.getDayOfMonth(),
                    datePicker.getMonth(),
                    datePicker.getYear())
                .contractType(contractRB.getText().toString())
                .propertyType(propertyRB.getText().toString())
                //.contractType(contractTypeSpinner.getSelectedItem().toString())
                //.propertyType(propertyTypeSpinner.getSelectedItem().toString())
                .cost(Integer.parseInt(costEditText.getText().toString()))
                .size(np.getValue())
                .location(typedLocation);
        if (!typedLocation.isEmpty()){
            LatLng geoPosition = Util.getFirstAddress(getActivity(), typedLocation);
            if (geoPosition != null)
                data.latitude(geoPosition.latitude).longitude(geoPosition.longitude);
        }
    }

}
