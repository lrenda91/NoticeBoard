package it.polito.mad.noticeboard.db;

import com.parse.ParseClassName;
import com.parse.ParseRelation;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by luigi on 29/05/15.
 */
@ParseClassName("Advertiser")
public class Advertiser extends ParseEntity {

    static final String FIRST_NAME = "firstName";
    static final String LAST_NAME = "lastName";
    static final String EMAIL = "eMail";
    static final String PHONE_NUMBER = "phoneNumber";
    static final String NOTICES = "notices";

    public Advertiser() {

    }

    public String getFirstName() {
        return (String) get(FIRST_NAME);
    }
    public void setFirstName(String value){
        put(FIRST_NAME, value);
    }

    public String getLastName() {
        return (String) get(LAST_NAME);
    }
    public void setLastName(String value){
        put(LAST_NAME, value);
    }

    public String getEMail() {
        return (String) get(EMAIL);
    }
    public void setEMail(String value){
        put(EMAIL, value);
    }

    public String getPhoneNumber(){
        return (String) get(PHONE_NUMBER);
    }
    public void setPhoneNumber(String value){
        put(PHONE_NUMBER, value);
    }

    public ParseRelation<Notice> getNotices(){
        return getRelation(NOTICES);
    }
    public void addNotice(Notice value){
        getRelation(NOTICES).add(value);
    }
    public void removeNotice(Notice value){
        getRelation(NOTICES).remove(value);
    }

}
