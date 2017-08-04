package danandroid.course.contactsprovider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import danandroid.course.contactsprovider.models.Contact;

public class MainActivity extends AppCompatActivity implements ContactsDataSource.OnContactsArrivedListener {

    private static final int RC_CONTACTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContacts();
            }
        });
    }

    private void getContacts() {
        if (!checkContactPermission()){
            requestContactsPermission();
            return;
        }

        ContactsDataSource.getContactAsync(this/*context*/, this/*litener*/);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_CONTACTS &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getContacts();
        }else {
            Toast.makeText(this, "No Permission...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri packageUri = Uri.parse("package:" + getPackageName());
            intent.setData(packageUri);
            startActivity(intent);
        }
    }

    private void requestContactsPermission() {
        String[] requestedPermissions = {Manifest.permission.READ_CONTACTS};
        ActivityCompat.requestPermissions(this, requestedPermissions, RC_CONTACTS);
    }

    private boolean checkContactPermission() {
        // check for permission -> No Permission request it.
        int permissionResult =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        return permissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContactsArrived(List<Contact> data) {
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        rvContacts.setAdapter(new ContactAdapter(this, data));
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
    }

    static class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
        private Context context;
        private List<Contact> data;

        public ContactAdapter(Context context, List<Contact> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.contact_item, parent, false);
            return new ContactViewHolder(v);//view->Context -> Inflater
        }
        @Override
        public void onBindViewHolder(ContactViewHolder holder, int position) {
            Contact contact = data.get(position);

            holder.tvNames.setText(contact.getName());
            holder.tvEmails.setText(contact.getEmails().toString());
            holder.tvPhones.setText(contact.getPhones().toString());

            holder.model = contact;
            holder.context = context;
        }
        @Override
        public int getItemCount() {
            return data.size();
        }

        //ViewHolder//Adapter
        static class ContactViewHolder extends RecyclerView.ViewHolder {
            TextView tvNames;
            TextView tvEmails;
            TextView tvPhones;

            Context context;
            Contact model;

            public ContactViewHolder(View itemView) {
                super(itemView);
                tvEmails = itemView.findViewById(R.id.tvEmails);
                tvNames = itemView.findViewById(R.id.tvNames);
                tvPhones = itemView.findViewById(R.id.tvPhones);
            }
        }
    }
}
