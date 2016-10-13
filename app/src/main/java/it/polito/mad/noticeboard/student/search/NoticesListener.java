package it.polito.mad.noticeboard.student.search;

import java.util.List;

import it.polito.mad.noticeboard.db.Notice;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;


/**
 * Created by luigi on 07/06/15.
 */
public interface NoticesListener {

    void update(List<PNoticeData> noticesData);

}
