package it.polito.mad.noticeboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import it.polito.mad.noticeboard.maps.GMapsHintsDLTask;

/**
 * Created by luigi on 13/06/15.
 */
public class Util {

    private Util(){}

    public static boolean networkIsUp(Context context){
        ConnectivityManager cm = (ConnectivityManager)
                context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifiInfo.isConnected() || mobileInfo.isConnected();
    }

    public static boolean checkNetworkUp(Context context){
        boolean result = networkIsUp(context);
        if (!result){
            Toast.makeText(context, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public static Bitmap getBitmap(byte[] rawData){
        if (rawData == null){
            return null;
        }
        return BitmapFactory.decodeByteArray(rawData, 0, rawData.length);
    }



    public static void registerExceptionHandler(){
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    private static class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
        private final String LINE_SEPARATOR = "\n";
        public static final String LOG_TAG = ExceptionHandler.class.getSimpleName();

        @SuppressWarnings("deprecation")
        public void uncaughtException(Thread thread, Throwable exception) {
            StringWriter stackTrace = new StringWriter();
            exception.printStackTrace(new PrintWriter(stackTrace));

            StringBuilder errorReport = new StringBuilder();
            errorReport.append(stackTrace.toString());

            Log.e(LOG_TAG, errorReport.toString());

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }


    public static void setAutoCompleteGMaps(final AutoCompleteTextView actv){
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(actv.getContext(),
                android.R.layout.simple_list_item_1);
        adapter.setNotifyOnChange(true);

        actv.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count % 3 == 1) {
                    GMapsHintsDLTask task = new GMapsHintsDLTask(actv.getContext(),
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

        actv.setAdapter(adapter);
    }

    public static LatLng getFirstAddress(Context context, String address){
        if (address == null){
            return null;
        }
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
        if (addresses.isEmpty()){
            return null;
        }
        Address a = addresses.get(0);
        return new LatLng(a.getLatitude(), a.getLongitude());
    }

    public static void setValuesWithStep(NumberPicker numberPicker,
                                         final int min, final int max, final int step){
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue((int) Math.ceil((max-min)/step));
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                int temp = value * step;
                return "" + temp;
            }
        });
    }

}
