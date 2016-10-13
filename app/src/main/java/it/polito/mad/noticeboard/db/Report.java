package it.polito.mad.noticeboard.db;

import com.parse.ParseClassName;

import java.io.Serializable;

/**
 * Created by luigi on 29/05/15.
 */
@ParseClassName("Report")
public class Report extends ParseEntity {

    static final String REASON = "reason";
    static final String DETAILS = "details";
    static final String EMAIL = "reporter";
    static final String NOTICE = "notice";
    static final String DISPLAYED = "displayed";

    public Notice getNotice(){
        return (Notice) get(NOTICE);
    }
    public void setNotice(Notice notice){ put(NOTICE, notice); }

    public String getReason(){
        return (String) get(REASON);
    }
    public void setReason(String value){
        put(REASON, value);
    }

    public String getDetails(){
        return (String) get(DETAILS);
    }
    public void setDetails(String value){
        put(DETAILS, value);
    }

    public String getReporterEmail(){
        return (String) get(EMAIL);
    }
    public void setReporterEmail(String value){
        put(EMAIL, value);
    }

    public boolean displayed(){
        return (boolean) get(DISPLAYED);
    }
    public void setDisplayed(boolean value){
        put(DISPLAYED, value);
    }

}
