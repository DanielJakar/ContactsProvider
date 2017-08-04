package danandroid.course.contactsprovider.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Model class for storing contact data
 */

public class Contact implements Parcelable {
    private String name;
    private List<String> phones;
    private List<String> emails;

    public Contact(String name, List<String> phones, List<String> emails) {
        this.name = name;
        this.phones = phones;
        this.emails = emails;
    }



    public String getName() {
        return name;
    }

    public List<String> getPhones() {
        return phones;
    }

    public List<String> getEmails() {
        return emails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeStringList(this.phones);
        dest.writeStringList(this.emails);
    }

    protected Contact(Parcel in) {
        this.name = in.readString();
        this.phones = in.createStringArrayList();
        this.emails = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
