package com.example.user.blood_rit_hack.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.blood_rit_hack.Adapters.CustomAdapter;
import com.example.user.blood_rit_hack.R;
import com.example.user.blood_rit_hack.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.icu.lang.UProperty.MATH;

public class NotificationActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent_home = new Intent(NotificationActivity.this, HomeActivity.class);
                    finish();
                    startActivity(intent_home);
                    return true;
                case R.id.navigation_dashboard:
                    Intent intent_contact = new Intent(NotificationActivity.this, ContactActivity.class);
                    finish();
                    startActivity(intent_contact);

                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };


    public HashMap<String, HashMap<String, User>> M_users;
    public HashMap<String, User> R_users;
    private DatabaseReference mDatabase, mdatabase, database;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser current_user;
    ListView list;
    private  ArrayList<User> requested_users;
    private User current_requested_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        mDatabase = FirebaseDatabase.getInstance().getReference("requested_users");
        mdatabase = FirebaseDatabase.getInstance().getReference("accepted_users");
        database = FirebaseDatabase.getInstance().getReference("users");
        firebaseAuth = FirebaseAuth.getInstance();
        current_user = firebaseAuth.getCurrentUser();
        M_users = new HashMap<String, HashMap<String, User>>();
        R_users = new HashMap<>();
        requested_users = new ArrayList<>();
        current_requested_user = new User("name", "email", "9090909090", "user_id", "D+", 1.0, 1.0);

        current_user = firebaseAuth.getCurrentUser();
        System.out.println(current_user.getDisplayName());

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

        Query all = database;

        ChildEventListener lchildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                User new_user = dataSnapshot.getValue(User.class);
                R_users.put(new_user.user_id, new_user);
                System.out.println(new_user.name);
//                updateUI();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                User changed_user = dataSnapshot.getValue(User.class);
                R_users.put(changed_user.user_id, changed_user);
                System.out.println(changed_user.name);
//                updateUI();
            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User changed_user = (User)dataSnapshot.getValue(User.class);
                R_users.remove(changed_user.user_id);
//                updateUI();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        all.addChildEventListener(lchildEventListener);



        Query allUsers = mDatabase.orderByChild("name");



        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                User new_user = dataSnapshot.getValue(User.class);
                R_users.put(new_user.user_id, new_user);
                System.out.println(new_user.name);
                updateUI();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                User changed_user = dataSnapshot.getValue(User.class);
                R_users.put(changed_user.user_id, changed_user);
                System.out.println(changed_user.name);
                updateUI();
            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User changed_user = (User)dataSnapshot.getValue(User.class);
                R_users.remove(changed_user.user_id);
                updateUI();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        allUsers.addChildEventListener(childEventListener);


        Query allAUsers = mdatabase;

        ChildEventListener rchildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                M_users.remove(dataSnapshot.getKey());
                HashMap<String, User> temp = new HashMap<>();
                for(DataSnapshot preSnapshot: dataSnapshot.getChildren()) {
                    temp.put(preSnapshot.getKey(), preSnapshot.getValue(User.class));
                    System.out.println("HERE - " + preSnapshot.getKey());
                }
                M_users.put(dataSnapshot.getKey(), temp);
                updateUI();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                M_users.remove(dataSnapshot.getKey());
                HashMap<String, User> temp = new HashMap<>();
                for(DataSnapshot preSnapshot: dataSnapshot.getChildren()) {
                    temp.put(preSnapshot.getKey(), preSnapshot.getValue(User.class));
                    System.out.println("HERE - " + preSnapshot.getKey());
                }
                M_users.put(dataSnapshot.getKey(), temp);
                updateUI();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User changed_user = (User)dataSnapshot.getValue(User.class);
                R_users.remove(changed_user);
                updateUI();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NotificationActivity.this, "Some Error occured", Toast.LENGTH_SHORT);
            }
        };

        allAUsers.addChildEventListener(rchildEventListener);



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void updateUI() {
        list = findViewById(R.id.requested_users_list);
        requested_users.clear();
           for(User item : R_users.values()) {
               System.out.println(current_requested_user.latitude + " " + current_requested_user.longitude + " " + item.latitude + " " +  item.longitude);
                if(getDistance(current_requested_user.latitude, current_requested_user.longitude, item.latitude, item.longitude) < 4.0 &&
                        !current_user.getUid().equals(item.user_id) && current_requested_user.blood_group.equals(item.blood_group))requested_users.add(item);
            }
            for (Map.Entry<String, HashMap<String, User>> entry : M_users.entrySet()) {
                   for(Map.Entry<String, User> entry1 : entry.getValue().entrySet()) {
                       if(entry1.getValue().user_id.equals(current_user.getUid())) {
                           requested_users.remove(R_users.get(entry.getKey()));
                       }
                   }
            }


        CustomAdapter adapter = new CustomAdapter(requested_users, this);
        list.setAdapter(adapter);
    }

    public static double getDistance(double lat1, double lon1,double  lat2,double lon2) {
        double p = 0.017453292519943295;    // Math.PI / 180
        double a = 0.5 - Math.cos((lat2 - lat1) * p)/2 +
                Math.cos(lat1 * p) * Math.cos(lat2 * p) *
                        (1 - Math.cos((lon2 - lon1) * p))/2;
        System.out.println("########" + 12742 * Math.asin(Math.sqrt(a)));
        return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km
    }


}
