package it.polito.mad.noticeboard.db_parcel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by luigi on 09/06/15.
 */
public class PNoticeFilterParams implements Parcelable {

    private String location, contractType, propertyType;
    private int minSize, maxSize, minPrice, maxPrice;
    private List<String> tags = new LinkedList<>();

    public PNoticeFilterParams(){

    }

    public PNoticeFilterParams(Parcel in){
        location = in.readString();
        contractType = in.readString();
        propertyType = in.readString();
        minSize = in.readInt();
        maxSize = in.readInt();
        minPrice = in.readInt();
        maxPrice = in.readInt();
        in.readStringList(tags);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
        dest.writeString(contractType);
        dest.writeString(propertyType);
        dest.writeInt(minSize);
        dest.writeInt(maxSize);
        dest.writeInt(minPrice);
        dest.writeInt(maxPrice);
        dest.writeStringList(tags);
    }


    public PNoticeFilterParams location(String value){ location = value; return this; }
    public PNoticeFilterParams contractType(String value){ contractType = value; return this; }
    public PNoticeFilterParams propertyType(String value){ propertyType = value; return this; }
    public PNoticeFilterParams minSize(int value){ minSize = value; return this; }
    public PNoticeFilterParams maxSize(int value){ maxSize = value; return this; }
    public PNoticeFilterParams minPrice(int value){ minPrice = value; return this; }
    public PNoticeFilterParams maxPrice(int value){ maxPrice = value; return this; }
    public PNoticeFilterParams newTag(String value){ tags.add(value); return this; }
    public PNoticeFilterParams removeTag(String value){ tags.remove(value); return this; }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PNoticeFilterParams createFromParcel(Parcel in) {
            return new PNoticeFilterParams(in);
        }
        public PNoticeFilterParams[] newArray(int size) {
            return new PNoticeFilterParams[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
