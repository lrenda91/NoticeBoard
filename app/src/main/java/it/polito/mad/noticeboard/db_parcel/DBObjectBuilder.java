package it.polito.mad.noticeboard.db_parcel;

import com.parse.ParseObject;

/**
 * Created by luigi on 09/06/15.
 */
public interface DBObjectBuilder<T> {

    T build();
    void fillFrom(T obj);

}
