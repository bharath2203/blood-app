package com.example.user.blood_rit_hack.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.blood_rit_hack.R;
import com.example.user.blood_rit_hack.activity.NotificationActivity;
import com.example.user.blood_rit_hack.models.User;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomAdapter extends ArrayAdapter<User> {

    private ArrayList<User> dataSet;
    private Boolean accepted;
    Context mContext;
    TextView txtText, txtName, txtNumber;
    Button button;
    NotificationActivity notify_obj;
    FirebaseAuth firebaseAuth;
    FirebaseUser current_user;
    DatabaseReference mdatabase;
    User accepted_user, requested_user;


    public CustomAdapter(ArrayList<User> data, Context context) {
        super(context, R.layout.list_item, data);
        this.dataSet = data;
        this.mContext=context;
        this.accepted = accepted;
    }


    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final User dataModel = getItem(position);
        notify_obj = new NotificationActivity();

        // Check if an existing view is being reused, otherwise inflate the view

        final View result;

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }
        txtName = (TextView) convertView.findViewById(R.id.name);
        txtNumber = (TextView) convertView.findViewById(R.id.number);
        txtText = (TextView) convertView.findViewById(R.id.text);
        button = (Button) convertView.findViewById(R.id.accept);
        firebaseAuth = FirebaseAuth.getInstance();
        current_user = firebaseAuth.getCurrentUser();
        mdatabase = FirebaseDatabase.getInstance().getReference();
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                requested_user = dataModel;


                mdatabase.child("users").child(current_user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        accepted_user = snapshot.getValue(User.class);
                        mdatabase.child("accepted_users").child(requested_user.user_id).child(current_user.getUid()).setValue(accepted_user);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });

        txtName.setText(dataModel.name);
        txtNumber.setText(dataModel.phone_no);
        txtText.setText(dataModel.request_text);
        return convertView;
    }
}
