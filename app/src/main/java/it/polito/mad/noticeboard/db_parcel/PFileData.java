package it.polito.mad.noticeboard.db_parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.Arrays;

/**
 * Parcelable wrapper for a ParseFile, so its data can be forwarded through intents
 * Created by luigi on 09/06/15.
 */
public class PFileData implements Parcelable, DBObjectBuilder<ParseFile> {

    private String name;
    private byte[] data;

    public PFileData(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public PFileData(Parcel in){
        name = in.readString();
        int dataSize = in.readInt();
        byte[] b = new byte[dataSize];
        in.readByteArray(b);
        data = b;
    }

    public String getName() {
        return name;
    }
    public byte[] getData() {
        return data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(data.length);
        dest.writeByteArray(data);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PFileData createFromParcel(Parcel in) {
            return new PFileData(in);
        }
        public PFileData[] newArray(int size) {
            return new PFileData[size];
        }
    };

    @Override
    public ParseFile build() {
        return new ParseFile(name, data);
    }

    @Override
    public void fillFrom(ParseFile obj) {
        name = obj.getName();
        try{
            data = obj.getData();
        }
        catch(ParseException e){

        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PFileData)) return false;
        PFileData pfd = (PFileData) o;
        return Arrays.equals(this.data, pfd.data);
    }
}
