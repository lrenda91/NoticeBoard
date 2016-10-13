package it.polito.mad.noticeboard.db;

import android.util.Log;

import com.parse.ParseObject;
import org.json.JSONObject;

/**
 * Created by luigi on 29/05/15.
 */
public class ParseEntity extends ParseObject {

    protected ParseEntity(){

    }

    @Override
    public Object get(String key) {
        try {
            Object obj = super.get(key);
            return (obj == JSONObject.NULL) ? null : obj;
        }
        catch(java.lang.IllegalStateException exception){
            Log.e("err",exception.getMessage());
            return null;
        }
    }

    @Override
    public void put(String key, Object value) {
        super.put(key, (value!=null) ? value : JSONObject.NULL);
    }

}