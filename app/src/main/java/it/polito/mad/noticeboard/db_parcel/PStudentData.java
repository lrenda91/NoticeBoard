package it.polito.mad.noticeboard.db_parcel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;

import it.polito.mad.noticeboard.db.Advertiser;
import it.polito.mad.noticeboard.db.Student;

/**
 * Parcelable wrapper for a Student instance, so that its data can be forwarded through intents
 * Created by luigi on 10/06/15.
 */
public class PStudentData implements DBObjectBuilder<Student>, Parcelable {

    private String firstName, lastName, eMail;

    public String getFirstName(){ return firstName; }
    public String getLastName(){ return lastName; }
    public String getEmail(){ return eMail; }

    public PStudentData firstName(String value){ firstName = value; return this; }
    public PStudentData lastName(String value){ lastName = value; return this; }
    public PStudentData eMail(String value){ eMail = value; return this; }

    public PStudentData(){

    }

    public PStudentData(Parcel in){
        firstName = in.readString();
        lastName = in.readString();
        eMail = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(eMail);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public Student build() {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEMail(eMail);
        return student;
    }

    @Override
    public void fillFrom(Student obj) {
        firstName(obj.getFirstName())
                .lastName(obj.getLastName())
                .eMail(obj.getEmail());
    }
}
