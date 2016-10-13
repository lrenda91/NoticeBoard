package it.polito.mad.noticeboard.db;

import com.parse.ParseClassName;
import com.parse.ParseRelation;

import java.io.Serializable;

/**
 * Created by luigi on 29/05/15.
 */
@ParseClassName("Student")
public class Student extends ParseEntity {

    static final String FIRST_NAME = "firstName";
    static final String LAST_NAME = "lastName";
    static final String EMAIL = "eMail";
    static final String FAVORITE_NOTICES = "favoriteNotices";

    public String getFirstName(){
        return (String) get(FIRST_NAME);
    }
    public void setFirstName(String value){
        put(FIRST_NAME, value);
    }

    public String getLastName(){
        return (String) get(LAST_NAME);
    }
    public void setLastName(String value){
        put(LAST_NAME, value);
    }

    public String getEmail(){
        return (String) get(EMAIL);
    }
    public void setEMail(String value){
        put(EMAIL, value);
    }

    public ParseRelation<Notice> getFavorites(){
        return getRelation(FAVORITE_NOTICES);
    }
    public void setFavorites(ParseRelation<Notice> n){
        put(FAVORITE_NOTICES, n);
    }
    public void addFavorite(Notice value){
        getRelation(FAVORITE_NOTICES).add(value);
    }
    public void removeFavorite(Notice value){
        getRelation(FAVORITE_NOTICES).remove(value);
    }

}
