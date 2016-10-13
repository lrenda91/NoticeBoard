package it.polito.mad.noticeboard.db_parcel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;

import it.polito.mad.noticeboard.db.Advertiser;

/**
 * Parcelable wrapper for an Advertiser instance, so that its data can be forwarded through intents
 * Created by luigi on 10/06/15.
 */
public class PAdvertiserData implements DBObjectBuilder<Advertiser>, Parcelable {

    private String firstName, lastName, eMail, phone;

    public String getFirstName(){ return firstName; }
    public String getLastName(){ return lastName; }
    public String geteMail(){ return eMail; }
    public String getPhoneNumber(){ return phone; }

    public PAdvertiserData firstName(String value){ firstName = value; return this; }
    public PAdvertiserData lastName(String value){ lastName = value; return this; }
    public PAdvertiserData eMail(String value){ eMail = value; return this; }
    public PAdvertiserData phoneNumber(String value){ phone = value; return this; }

    public PAdvertiserData(){

    }

    public PAdvertiserData(Parcel in){
        firstName = in.readString();
        lastName = in.readString();
        eMail = in.readString();
        phone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(eMail);
        dest.writeString(phone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public Advertiser build() {
        Advertiser adv = new Advertiser();
        adv.setFirstName(firstName);
        adv.setLastName(lastName);
        adv.setEMail(eMail);
        adv.setPhoneNumber(phone);
        return adv;
    }

    @Override
    public void fillFrom(Advertiser obj) {
        firstName(obj.getFirstName())
                .lastName(obj.getLastName())
                .eMail(obj.getEMail())
                .phoneNumber(obj.getPhoneNumber());
    }
}
