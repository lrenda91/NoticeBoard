package it.polito.mad.noticeboard.db_parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import it.polito.mad.noticeboard.db.Notice;

/**
 * Parcelable wrapper for a notice, so its data can be forwarded through intents.<BR>
 * It implements Builder design pattern, by chaining setter methods to build a Notice instance.
 * Created by luigi on 09/06/15.
 */
public class PNoticeData implements Parcelable, DBObjectBuilder<Notice> {

    private String objID, title, description, propertyType, contractType, location;
    private int size, cost;
    private Date availableFrom, publishedAt;
    private double latitude, longitude;
    private List<PFileData> photos = new LinkedList<>();
    private List<String> tags = new LinkedList<>();

    private static final double MAX_LATITUDE = 90.0;
    private static final double MAX_LONGITUDE = 180.0;

    public PNoticeData(){}

    public PNoticeData(Parcel in){
        objID = in.readString();
        title = in.readString();
        description = in.readString();
        propertyType = in.readString();
        contractType = in.readString();
        location = in.readString();
        availableFrom = new Date(in.readLong());
        publishedAt = new Date(in.readLong());
        latitude = in.readDouble();
        longitude = in.readDouble();
        size = in.readInt();
        cost = in.readInt();
        in.readStringList(tags);
        int numPhotos = in.readInt();
        for (int i=0;i<numPhotos;i++){
            photos.add((PFileData) in.readParcelable(PFileData.class.getClassLoader()));
        }
    }

    public boolean hasValidCoordinates(){
        return Math.abs(latitude) < MAX_LATITUDE && Math.abs(longitude) < MAX_LONGITUDE;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objID);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(propertyType);
        dest.writeString(contractType);
        dest.writeString(location);
        dest.writeLong(availableFrom.getTime());
        dest.writeLong(publishedAt.getTime());
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(size);
        dest.writeInt(cost);
        dest.writeStringList(tags);
        dest.writeInt(photos.size());
        for (PFileData fh : photos){
            dest.writeParcelable(fh, 0);
        }
    }

    public String getObjID(){ return objID; }
    public String getTitle(){ return title; }
    public String getDescription(){ return description; }
    public String getPropertyType(){ return propertyType; }
    public String getContractType(){ return contractType; }
    public String getLocation(){ return location; }
    public Date getAvailableFrom(){ return availableFrom; }
    public Date getPublishedAt(){ return publishedAt; }
    public double getLatitude(){ return latitude; }
    public double getLongitude(){ return longitude; }
    public int getSize(){ return size; }
    public int getCost(){ return cost; }
    public List<String> getTags(){ return tags; }
    public List<PFileData> getPhotos(){ return photos; }

    public PNoticeData objID(String value){ objID = value; return this; }
    public PNoticeData title(String value){ title = value; return this; }
    public PNoticeData description(String value){ description = value; return this; }
    public PNoticeData propertyType(String value){ propertyType = value; return this; }
    public PNoticeData contractType(String value){ contractType = value; return this; }
    public PNoticeData location(String value){ location = value; return this; }
    public PNoticeData availableFrom(int dd, int mm, int yyyy){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, dd);
        cal.set(Calendar.MONTH, mm);
        cal.set(Calendar.YEAR, yyyy);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        availableFrom = cal.getTime();
        return this;
    }
    public PNoticeData publishedAt(int dd, int mm, int yyyy){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, dd);
        cal.set(Calendar.MONTH, mm);
        cal.set(Calendar.YEAR, yyyy);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        publishedAt = cal.getTime();
        return this;
    }
    public PNoticeData latitude(double value){ latitude = value; return this; }
    public PNoticeData longitude(double value){ longitude = value; return this; }
    public PNoticeData size(int value){ size = value; return this; }
    public PNoticeData cost(int value){ cost = value; return this; }
    public PNoticeData newTag(String value){ tags.add(value); return this; }
    public PNoticeData removeTag(String value){ tags.remove(value); return this; }
    public PNoticeData newPhoto(String name, byte[] data){
        photos.add(new PFileData(name, data));
        return this;
    }
    public PNoticeData removePhoto(String name, byte[] data){
        photos.remove(new PFileData(name, data));
        return this;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PNoticeData createFromParcel(Parcel in) {
            return new PNoticeData(in);
        }
        public PNoticeData[] newArray(int size) {
            return new PNoticeData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public Notice build() {
        Notice result = new Notice();
        result.setTitle(title);
        result.setDescription(description);
        result.setPropertyType(propertyType);
        result.setContractType(contractType);
        result.setAvailableFrom(availableFrom);
        //TODO manca pubblicazione
        result.setLocationName(location);
        result.setLocationPoint(new ParseGeoPoint(latitude, longitude));
        result.setSize(size);
        result.setCost(cost);
        result.addTags(tags);
        return result;
    }

    @Override
    public void fillFrom(Notice item) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(item.getAvailableFrom());
        objID(item.getObjectId());
        title(item.getTitle());
        description(item.getDescription());
        contractType("" + item.getContractType());
        cost(item.getCost());
        propertyType("" + item.getType());
        availableFrom(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
        cal.setTime(item.getPublishedAt());
        publishedAt(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
        location(item.getLocationName());
        latitude(item.getLocationPoint() == null ? MAX_LATITUDE+1 : item.getLocationPoint().getLatitude());
        longitude(item.getLocationPoint() == null ? MAX_LONGITUDE+1 : item.getLocationPoint().getLongitude());
        for (String tag : item.getTags())
            newTag(tag);
        try {
            for (ParseFile f : item.getPhotos())
                newPhoto(f.getName(), f.getData());
        } catch (ParseException e){

        }
    }
}