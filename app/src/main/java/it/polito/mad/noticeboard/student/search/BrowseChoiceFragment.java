package it.polito.mad.noticeboard.student.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ViewSwitcher;

import it.polito.mad.noticeboard.Util;
import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.student.SortDialog;

/**
 * Created by luigi on 07/06/15.
 */
public class BrowseChoiceFragment extends Fragment {

    private ViewSwitcher simpleSearchContainer;
    private AutoCompleteTextView listACTV, mapACTV;
    private Button searchButton, sortButton;
    private View[] views;


    private int curMode = -1;   //invalid initial value

    public interface BrowseModeChangeCallback {
        void onOffersBrowseModeChanged(int mode);
    }

    public interface SimpleSearchCallback {
        void onSimpleSearch(int mode, String param);
    }



    private AutoCompleteTextView getCurrentSearchField(){
        return curMode == BrowseActivity.LIST_MODE ? listACTV : mapACTV;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_offers_mode_choice, container, false);
        simpleSearchContainer = (ViewSwitcher) root.findViewById(R.id.simple_search_container);
        listACTV = (AutoCompleteTextView) root.findViewById(R.id.list_simple_search);
        mapACTV = (AutoCompleteTextView) root.findViewById(R.id.map_simple_search);
        searchButton = (Button) root.findViewById(R.id.simple_search_button);
        sortButton = (Button) root.findViewById(R.id.sort_button);

        views = new View[]{ simpleSearchContainer.getChildAt(0), simpleSearchContainer.getChildAt(1)};

        root.findViewById(R.id.student_choice_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMode(BrowseActivity.LIST_MODE);
            }
        });
        root.findViewById(R.id.advertiser_choice_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMode(BrowseActivity.MAP_MODE);
            }

        });
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SortDialog().show(getActivity().getSupportFragmentManager(),null);
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchParam = getCurrentSearchField().getText().toString();
                if (getActivity() instanceof SimpleSearchCallback) {
                    ((SimpleSearchCallback) getActivity()).onSimpleSearch(curMode, searchParam);
                }
            }
        });
        return root;
    }

    private void switchToMode(int mode){
        if (curMode != mode){
            curMode = mode;

            AutoCompleteTextView actv = getCurrentSearchField();
            actv.setEnabled(true);

            searchButton.setEnabled(true);
            sortButton.setEnabled(true);
            while (simpleSearchContainer.getCurrentView() != views[curMode]){
                simpleSearchContainer.showNext();
            }

            if (mode == BrowseActivity.MAP_MODE){
                Util.setAutoCompleteGMaps(actv);
            }
            actv.setHint((mode == BrowseActivity.LIST_MODE) ?
                    R.string.list_search_hint : R.string.map_search_hint);

            if (getActivity() instanceof BrowseModeChangeCallback){
                ((BrowseModeChangeCallback) getActivity()).onOffersBrowseModeChanged(curMode);
            }
        }
    }
}
