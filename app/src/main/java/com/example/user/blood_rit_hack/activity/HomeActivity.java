package com.example.user.blood_rit_hack.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.blood_rit_hack.R;
import com.example.user.blood_rit_hack.models.User;
import com.google.android.gms.common.api.Response;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HomeActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private DatabaseReference mDatabase, database;
    private ArrayList<User> users;
    private HashMap<String, User> M_users;
    private FirebaseUser current_user;
    private FirebaseAuth firebaseAuth;
    User current_requested_user;
    User user;

    String urlstr ="http://www.yourapi.com";
    ListView list_adapter;
    String current_user_key;
    NotificationActivity notify_obj;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    Intent intent_contact = new Intent(HomeActivity.this, ContactActivity.class);
                    finish();
                    startActivity(intent_contact);

                    return true;
                case R.id.navigation_notifications:
                    Intent intent_notification = new Intent(HomeActivity.this, NotificationActivity.class);
                    finish();
                    startActivity(intent_notification);

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        firebaseAuth = FirebaseAuth.getInstance();
        current_user = firebaseAuth.getCurrentUser();
        M_users = new HashMap<String, User>();

        current_user = firebaseAuth.getCurrentUser();
        System.out.println(current_user.getDisplayName());
        updateUI();


        database = FirebaseDatabase.getInstance().getReference("users");

        database.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_requested_user = dataSnapshot.getValue(User.class);
                updateUI();
                System.out.print("LAtitudee " + current_requested_user.latitude);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query allUsers = mDatabase.orderByChild("name");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                User new_user = dataSnapshot.getValue(User.class);
                M_users.put(new_user.user_id, new_user);
                System.out.println(new_user.name);
                updateUI();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                User changed_user = (User)dataSnapshot.getValue(User.class);
                M_users.put(changed_user.user_id, changed_user);
                Toast.makeText(HomeActivity.this, changed_user.name, Toast.LENGTH_SHORT);
                System.out.println(changed_user.name);
                updateUI();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User changed_user = (User)dataSnapshot.getValue(User.class);
                M_users.remove(changed_user);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Some Error occured", Toast.LENGTH_SHORT);
            }
        };
        allUsers.addChildEventListener(childEventListener);


        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void updateUI() {
        String current_key = current_user.getUid();
        User tem_user = M_users.get(current_key);
        if(tem_user != null && tem_user.is_user_requested == true) {
            putRequestedUI();
        } else {
            putNormalUI();
        }
    }

    public void putRequestedUI() {
        setContentView(R.layout.user_requested);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void putNormalUI() {
        setContentView(R.layout.activity_home);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    public void userRequested(View view) {

        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Do you really want send it to all Users over 4KM radius?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        User user = M_users.get(current_user.getUid());
                        System.out.println("----------------------------------------------------------------------->");

                        if(user != null) {
                            if(user.is_user_requested == true) return;
                            user.is_user_requested = true;
                            user.request_text = "In Need of blood...";
                            System.out.println("----------------------------------------------------------------------->");
                            mDatabase.child(current_user.getUid()).setValue(user);

                            DatabaseReference rDatabase = FirebaseDatabase.getInstance().getReference("requested_users");
                            rDatabase.child(current_user.getUid()).setValue(user);
                            System.out.println("----------------------------------------------------------------------->");

                            for(User item : M_users.values()) {
                                if(getDistance(current_requested_user.latitude, current_requested_user.longitude, item.latitude, item.longitude) < 4.0 &&
                                        !current_user.getUid().equals(item.user_id) && current_requested_user.blood_group.equals(item.blood_group))
                                urlstr = "http://bloodappteam.000webhostapp.com/a.php?email=" + item.email;
                                Thread th=new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            System.out.println("----------------------------------------------------------------------->"+urlstr);
                                            URL url = new URL(urlstr);
                                            url.openConnection().getInputStream();


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                th.start();
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, "Some Error occured in updating", Toast.LENGTH_SHORT).show();
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();


    }

    public static double getDistance(double lat1, double lon1,double  lat2,double lon2) {
        double p = 0.017453292519943295;    // Math.PI / 180
        double a = 0.5 - Math.cos((lat2 - lat1) * p)/2 +
                Math.cos(lat1 * p) * Math.cos(lat2 * p) *
                        (1 - Math.cos((lon2 - lon1) * p))/2;
        System.out.println("########" + 12742 * Math.asin(Math.sqrt(a)));
        return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km
    }

    public void userCanceled(View view) {
        User user = M_users.get(current_user.getUid());
        if(user != null) {
            user.is_user_requested = false;
            mDatabase.child(current_user.getUid()).setValue(user);

            DatabaseReference rDatabase = FirebaseDatabase.getInstance().getReference("requested_users");
            rDatabase.child(current_user.getUid()).removeValue();
            DatabaseReference aDatabase = FirebaseDatabase.getInstance().getReference("accepted_users");
            aDatabase.child(current_user.getUid()).removeValue();

        } else {
            Toast.makeText(this, "Some Error occured in updating", Toast.LENGTH_SHORT).show();
        }
    }
}



//class RequestTask extends AsyncTask<String, String, String>{
//
//    @Override
//    protected String doInBackground(String... str) {
//        String responseString = null;
//        try {
//            URL url = new URL(str[0]);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
//                // Do normal input or output stream reading
//            }
//            else {
////                response = "FAILED"; // See documentation for more info on response handling
//            }
//        } catch (Exception e) {
//            //TODO Handle problems..
//        }
//        return "";
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//        super.onPostExecute(result);
//        //Do anything with response..
//    }
//}
