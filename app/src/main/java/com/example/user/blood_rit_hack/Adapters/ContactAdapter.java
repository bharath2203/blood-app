package com.example.user.blood_rit_hack.Adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

public class ContactAdapter extends ArrayAdapter<User> {

    private ArrayList<User> dataSet;
    Context mContext;
    TextView txtName, txtNumber;
    Button button;


    public ContactAdapter(ArrayList<User> data, Context context) {
        super(context, R.layout.list_item_contact, data);
        this.dataSet = data;
        this.mContext=context;

    }


    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final User dataModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        final View result;

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_contact, parent, false);
        }
        txtName = (TextView) convertView.findViewById(R.id.name);
        txtNumber = (TextView) convertView.findViewById(R.id.number);
        button = (Button) convertView.findViewById(R.id.call);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL
                        , Uri.parse("tel:" + dataModel.phone_no));
                mContext.startActivity(intent);
            }
        });

        txtName.setText(dataModel.name);
        txtNumber.setText(dataModel.phone_no);
        return convertView;
    }
}
