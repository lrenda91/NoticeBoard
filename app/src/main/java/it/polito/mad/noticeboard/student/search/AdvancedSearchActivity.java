package it.polito.mad.noticeboard.student.search;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.Util;
import it.polito.mad.noticeboard.db.Notice;

public class AdvancedSearchActivity extends AppCompatActivity {

    private Notice.FilterData params = new Notice.FilterData();

    private ListView tagsList;

    private EditText newTagET;
    private TextView within;
    private Spinner contractTypeSpinner, propertyTypeSpinner;
    private SeekBar minPriceSeekBar, maxPriceSeekBar, withinSeekBar, minSizeSeekBar, maxSizeSeekBar;
    private AutoCompleteTextView locationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        findViewById(R.id.new_tag_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = newTagET.getText().toString();
                if (!tag.isEmpty()) {
                    params.tags.add(tag);
                    ((BaseAdapter) tagsList.getAdapter()).notifyDataSetChanged();
                    newTagET.getText().clear();
                }
            }
        });
        newTagET = (EditText) findViewById(R.id.new_tag_edit_text);
        tagsList = (ListView) findViewById(R.id.tags_list);
        tagsList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        tagsList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return params.tags.size();
            }

            @Override
            public String getItem(int position) {
                return params.tags.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null){
                    convertView = LayoutInflater.from(AdvancedSearchActivity.this)
                            .inflate(R.layout.layout_tags_list_item, parent, false);
                }
                final String item = getItem(position);
                ((TextView) convertView.findViewById(R.id.tag_value)).setText("#"+item);
                convertView.findViewById(R.id.cancel_tag).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        params.tags.remove(item);
                        notifyDataSetChanged();
                    }
                });
                return convertView;
            }
        });


        locationEditText = (AutoCompleteTextView) findViewById(R.id.location);
        Util.setAutoCompleteGMaps(locationEditText);

        contractTypeSpinner = (Spinner) findViewById(R.id.contract_type);
        propertyTypeSpinner = (Spinner) findViewById(R.id.property_type);

        within = (TextView) findViewById(R.id.within);

        minPriceSeekBar = (SeekBar) findViewById(R.id.minPriceSeekBar);
        maxPriceSeekBar = (SeekBar) findViewById(R.id.maxPriceSeekBar);
        minSizeSeekBar = (SeekBar) findViewById(R.id.minSizeSeekBar);
        maxSizeSeekBar = (SeekBar) findViewById(R.id.maxSizeSeekBar);
        withinSeekBar = (SeekBar) findViewById(R.id.withinSeekBar);
        withinSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                within.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        minPriceSeekBar.setProgress(0);
        minPriceSeekBar.setMax(1500);
        minPriceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 100;
                progress = progress * 100;
                seekBar.setProgress(progress);
                if (maxPriceSeekBar.getProgress() < progress) {
                    maxPriceSeekBar.setProgress(progress);
                }
                params.minPrice(progress);
                TextView seekBarValue = (TextView) findViewById(R.id.minPriceLabel);
                seekBarValue.setText(String.valueOf(progress));
            }
        });
        maxPriceSeekBar.setProgress(0);
        maxPriceSeekBar.setMax(1500);
        maxPriceSeekBar.setProgress(maxPriceSeekBar.getMax());
        maxPriceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 100;
                progress = progress * 100;
                seekBar.setProgress(progress);
                if (minPriceSeekBar.getProgress() > progress){
                    minPriceSeekBar.setProgress(progress);
                }
                params.maxPrice(progress);
                TextView seekBarValue = (TextView) findViewById(R.id.maxPriceLabel);
                seekBarValue.setText(String.valueOf(progress));
            }
        });

        minSizeSeekBar.setProgress(0);
        minSizeSeekBar.setMax(1500);
        minSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 100;
                progress = progress * 100;
                seekBar.setProgress(progress);
                if (maxSizeSeekBar.getProgress() < progress){
                    maxSizeSeekBar.setProgress(progress);
                }
                params.minSize(progress);
                TextView seekBarValue = (TextView) findViewById(R.id.minSizeLabel);
                seekBarValue.setText(String.valueOf(progress));
            }
        });
        maxSizeSeekBar.setProgress(0);
        maxSizeSeekBar.setMax(1500);
        maxSizeSeekBar.setProgress(maxSizeSeekBar.getMax());
        maxSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 100;
                progress = progress * 100;
                seekBar.setProgress(progress);
                if (minSizeSeekBar.getProgress() > progress){
                    minSizeSeekBar.setProgress(progress);
                }
                params.maxSize(progress);
                TextView seekBarValue = (TextView) findViewById(R.id.maxSizeLabel);
                seekBarValue.setText(String.valueOf(progress));
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null){
            outState.putStringArrayList("tags", new ArrayList<>(params.tags));
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            params.tags = savedInstanceState.getStringArrayList("tags");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_adv_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_filter:
                String unspecified = getResources().getString(R.string.unspecified);
                String locationName = locationEditText.getText().toString();
                String propType = propertyTypeSpinner.getSelectedItem().toString();
                String contractType = contractTypeSpinner.getSelectedItem().toString();
                params.contractType(contractType.equals(unspecified) ? null : contractType)
                        .propertyType(propType.equals(unspecified) ? null : propType)
                        .location(locationName.isEmpty() ? null : locationName)
                        .minPrice(minPriceSeekBar.getProgress())
                        .maxPrice(maxPriceSeekBar.getProgress())
                        .minSize(minSizeSeekBar.getProgress())
                        .maxSize(maxSizeSeekBar.getProgress());

                LatLng coordinates = Util.getFirstAddress(this, locationName);
                if (coordinates != null){
                    params.latitude(coordinates.latitude)
                            .longitude(coordinates.longitude)
                            .within(1);
                }

                Intent backIntent = new Intent();
                backIntent.putExtra("params", params);
                setResult(Activity.RESULT_OK, backIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, backIntent);
        finish();
    }
}
