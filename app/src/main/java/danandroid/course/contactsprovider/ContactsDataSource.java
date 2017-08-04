package danandroid.course.contactsprovider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import danandroid.course.contactsprovider.models.Contact;

/**
 * Contacts content provider data
 * <uses-permission android:name="android.permission.READ_CONTACTS"/>
 */

public class ContactsDataSource {
    public interface OnContactsArrivedListener{
        void onContactsArrived(List<Contact> Data);
    }
    public static void getContactAsync(final Context context, final OnContactsArrivedListener listener){
        new AsyncTask<Void, Void, List<Contact>>() {

            //do in background thread...
            @Override
            protected List<Contact> doInBackground(Void... voids) {
                return getContacts(context);
            }

            //report the result from the UI Thread.
            @Override
            protected void onPostExecute(List<Contact> contacts) {
                listener.onContactsArrived(contacts);
            }
        }.execute();

    }

    private static ArrayList<Contact> getContacts(Context context) {
        //id, displayName
        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
        ArrayList<Contact> contacts = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(contactUri, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            //TODO: notify listener - No result
            return new ArrayList<>();
        }
        do {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));


            ArrayList<String> phones = getPhones(context, id);
            ArrayList<String> emails = getEmails(context, id);
            Contact contact = new Contact(name, phones, emails);
            contacts.add(contact);
        } while (cursor.moveToNext());
        cursor.close();
        return contacts;
    }

    private static ArrayList<String> getPhones(Context context, String id) {
        //goto Phones table-> aquire the phones.
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

        Cursor phonesCursor = context.getContentResolver().query(
                phonesUri,
                null/*specific columns*/,
                where,
                whereArgs,
                null /*sortOrder*/
        );

        if (phonesCursor == null || !phonesCursor.moveToFirst()){
            //TODO: return something...
            return new ArrayList<>();
        }
        do {
            String phone = phonesCursor.getString(phonesCursor.getColumnIndex(colNumber));
            phones.add(phone);
        }while (phonesCursor.moveToNext());
        phonesCursor.close();

        return new ArrayList<String>(phones);
    }
    private static ArrayList<String> getEmails(Context context, String id) {
        Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String colEmail = ContactsContract.CommonDataKinds.Email.DATA;
        String colContactID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;

        Cursor cursor =
                context.getContentResolver().query(uri, null, colContactID + "=" + id, null, null);


        if (cursor==null || !cursor.moveToFirst())
            return new ArrayList<>(); //throw

        HashSet<String> emails = new HashSet<>();
        do{

            String email = cursor.getString(cursor.getColumnIndex(colEmail));
            emails.add(email);
            /*
            int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String colI = cursor.getString(i);
                String colName = cursor.getColumnName(i);
            }
            */

        }while (cursor.moveToNext());


        cursor.close();
        return new ArrayList<>(emails);
    }


    //Uri.
    //column names.

    //object to query the uri ContentResolver.
}
