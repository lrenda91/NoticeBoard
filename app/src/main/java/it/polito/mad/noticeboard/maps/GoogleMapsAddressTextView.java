package it.polito.mad.noticeboard.maps;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.List;

/**
 * Created by luigi on 17/06/15.
 */
public class GoogleMapsAddressTextView extends AutoCompleteTextView {

    public GoogleMapsAddressTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1);
        //adapter.setNotifyOnChange(true);

        addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count % 3 == 1) {
                    GMapsHintsDLTask task = new GMapsHintsDLTask(getContext(),
                            new GMapsHintsDLTask.HintsDownloadedCallback() {
                                @Override
                                public void onDownloadCompleted(List<String> result) {
                                    adapter.clear();
                                    adapter.addAll(result);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onDownloadError(Exception exception) {

                                }
                            });
                    task.execute(s.toString());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        //setAdapter(adapter);
    }

    public GoogleMapsAddressTextView(Context context) {
        this(context, null, 0);
    }

    public GoogleMapsAddressTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int proposedWidth = MeasureSpec.getSize(widthMeasureSpec);
        int proposedHeight = MeasureSpec.getSize(heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        int desiredWidth = 200;
        int desiredHeight = 50;

        int finalWidth = 0, finalHeight = 0;

        switch (wMode){
            case MeasureSpec.EXACTLY:
                finalWidth = proposedWidth;
                break;
            case MeasureSpec.AT_MOST:
                finalWidth = Math.min(desiredWidth, proposedWidth);
                break;
            case MeasureSpec.UNSPECIFIED:
                finalWidth = desiredWidth;
                break;
        }

        switch (hMode){
            case MeasureSpec.EXACTLY:
                finalHeight = proposedHeight;
                break;
            case MeasureSpec.AT_MOST:
                finalHeight = Math.min(desiredHeight, proposedHeight);
                break;
            case MeasureSpec.UNSPECIFIED:
                finalHeight = desiredHeight;
                break;
        }

        setMeasuredDimension(finalWidth,finalHeight);
    }
}
