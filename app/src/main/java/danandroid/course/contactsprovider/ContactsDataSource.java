package danandroid.course.contactsprovider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Contacts contant provider data
 *  <uses-permission android:name="android.permission.READ_CONTACTS"/>
 */

public class ContactsDataSource {

    public static void getContacts(Context context){
        Uri uriContactName = ContactsContract.Contacts.CONTENT_URI;

        Cursor cursor = context.getContentResolver().query(uriContactName, null, null, null, null);

        if (cursor == null || !cursor.moveToFirst()){
            //TODO: notify listener - No result

            return;
        }

        do {
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            getPhones(context,id);
            ;
        }while (cursor.moveToNext());

//        cursor.moveToNext();
//        cursor.getColumnCount();
//        cursor.getString(...);
//        cursor.getColumnIndex("");

        cursor.close();

    }

    public static ArrayList<String> getPhones (Context context, String id){
        //goto Phones table -> aquire the phones.

        //URI
        Uri phonesUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        HashSet<String> phones = new HashSet<>();

        //Column names:
        String colNumber = ContactsContract.CommonDataKinds.Phone.NUMBER;
        String colContactID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;


        //String selection:
        String where = colContactID + "=?";
        //String[] selectionArgs:
        String[] whereArgs = {id};

        Cursor phoneCursor = context.getContentResolver().query(
                phonesUri,
                null/*specific columns*/,
                where,
                whereArgs,
                null/*sortOrder*/);

        if (phoneCursor == null || !phoneCursor.moveToFirst()){
            //TODO: return something...
            return null;
        }

        do{
            String phone = phoneCursor.getString(phoneCursor.getColumnIndex(colNumber));
            phones.add(phone);

        }while (phoneCursor.moveToNext());

        phoneCursor.close();

        return new ArrayList<String>(phones);
    }
    //Uri
    //column name
    //object to query the uri contantresolver.
}
