package com.example.user.blood_rit_hack.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.blood_rit_hack.Adapters.ContactAdapter;
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
import java.util.UUID;

public class ContactActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent_home = new Intent(ContactActivity.this, HomeActivity.class);
                    finish();
                    startActivity(intent_home);
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    Intent intent_notification = new Intent(ContactActivity.this, NotificationActivity.class);
                    finish();
                    startActivity(intent_notification);
                    return true;
            }
            return false;
        }
    };


    private DatabaseReference mDatabase, mdatabase;
    private ArrayList<User> users;
    private HashMap<String, User> M_users;
    private FirebaseUser current_user;
    private FirebaseAuth firebaseAuth;
    private User current_requested_user;
    private ArrayList<User> accepted_users;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mDatabase = FirebaseDatabase.getInstance().getReference("accepted_users");
        mdatabase = FirebaseDatabase.getInstance().getReference("requested_users");
        firebaseAuth = FirebaseAuth.getInstance();
        current_user = firebaseAuth.getCurrentUser();
        M_users = new HashMap<String, User>();
        accepted_users = new ArrayList<User>();
        updateUI();


        Query allUsers = mDatabase.child(current_user.getUid());

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                User new_user = dataSnapshot.getValue(User.class);
                M_users.put(new_user.user_id, new_user);
                Toast.makeText(ContactActivity.this, new_user.name, Toast.LENGTH_SHORT);
                System.out.println(new_user.name);
                updateUI();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                User changed_user = (User)dataSnapshot.getValue(User.class);
                M_users.put(changed_user.user_id, changed_user);
                Toast.makeText(ContactActivity.this, changed_user.name, Toast.LENGTH_SHORT);
                System.out.println(changed_user.name);
                updateUI();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User changed_user = (User)dataSnapshot.getValue(User.class);
                M_users.remove(changed_user);
                updateUI();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ContactActivity.this, "Some Error occured", Toast.LENGTH_SHORT);
            }
        };

        allUsers.addChildEventListener(childEventListener);




        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void updateUI() {

        if(M_users.size() == 0) {
            showNoAcceptsUI();
            return;
        }

        accepted_users.clear();
        for(User user: M_users.values()) {
            accepted_users.add(user);
        }
        setContentView(R.layout.activity_contact);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        list = findViewById(R.id.accepted_users_list);
        ContactAdapter adapter = new ContactAdapter(accepted_users, this);
        list.setAdapter(adapter);
    }

    public void showNoAcceptsUI() {

    }
}
