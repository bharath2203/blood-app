package com.example.user.blood_rit_hack.activity;

import android.content.Intent;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.user.blood_rit_hack.R;
import com.example.user.blood_rit_hack.models.User;
import com.example.user.blood_rit_hack.utils.GPSTracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class RegisterActivity extends AppCompatActivity {

    String user_name, user_email, user_password, user_phone_no, user_blood_group;
    User user;

    LocationManager mLocationManager;

    // Global declaration

    private TextInputEditText register_email, register_password, register_name, register_phone_no;
    private Button register_submit, register_getlocation;
    private FirebaseAuth firebase_auth;
    private ProgressBar register_progress;
    private FirebaseDatabase database;
    GPSTracker gps;
    private double latitude, longitude;

    

    MaterialBetterSpinner materialBetterSpinner;
    String BLOOD_GROUPS[] = {"A+", "AB+", "AB-", "O+", "O-"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get Firebase auth Instance
        FirebaseApp.initializeApp(this);
        firebase_auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        register_name = (TextInputEditText)findViewById(R.id.register_name);
        register_email = (TextInputEditText)findViewById(R.id.register_email);
        register_password = (TextInputEditText)findViewById(R.id.register_password);
        register_phone_no = (TextInputEditText)findViewById(R.id.register_number);
        register_submit = (Button)findViewById(R.id.register_submit);
        register_progress = (ProgressBar)findViewById(R.id.register_progress);
        register_getlocation = (Button)findViewById(R.id.register_location);
        latitude = 12.99 + 0.000000000001 * Math.random();
        longitude = 76.11 + 0.00000000001 * Math.random();

        // Creates Spinner selector for Blood Selection
        materialBetterSpinner = (MaterialBetterSpinner)findViewById(R.id.register_select);
        ArrayAdapter<String> select_adapter = new ArrayAdapter<>(this,  android.R.layout.simple_dropdown_item_1line, BLOOD_GROUPS);
        materialBetterSpinner.setAdapter(select_adapter);
        materialBetterSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing to do
            }

            @Override
            public void afterTextChanged(Editable editable) {
                user_blood_group = materialBetterSpinner.getText().toString();
            }
        });



        register_getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create class object
                gps = new GPSTracker(RegisterActivity.this);

                // Check if GPS enabled
                if(gps.canGetLocation()) {

                    Double temp1 = gps.getLatitude();
                    Double temp2 = gps.getLongitude();

                    if(temp1 != 0) latitude = temp1;
                    if(temp2 != 0) longitude = temp2;

                    // \n is for new line

                } else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.
                    gps.showSettingsAlert();
                }
            }
        });


        // Submit button clicked

        register_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get entered values
                final String name = register_name.getText().toString().trim();
                String email = register_email.getText().toString().trim();
                String password = register_password.getText().toString().trim();
                final String phone_number = register_phone_no.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Enter valid Email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(checkPhoneNumber(phone_number)) {
                    Toast.makeText(RegisterActivity.this, "Enter valid Phone number", Toast.LENGTH_SHORT).show();
                    return;
                }


                if(TextUtils.isEmpty(name)) {
                    Toast.makeText(RegisterActivity.this, "Enter name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Enter valid password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must be of atleast 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                register_progress.setVisibility(View.VISIBLE);

                firebase_auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(RegisterActivity.this, "Created User: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                register_progress.setVisibility(View.GONE);

                                if(!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                                } else {
                                    DatabaseReference myRef = database.getReference();
                                    FirebaseUser currentUser = firebase_auth.getCurrentUser();
                                    User user = new User(name, currentUser.getEmail(), phone_number, currentUser.getUid(),user_blood_group, latitude, longitude);
                                    myRef.child("users").child(currentUser.getUid()).setValue(user);
                                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });

    }

//    private void getLocationDetails() {
//        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//    }

    private static boolean checkPhoneNumber(String phone_no) {
       if(phone_no == "") {
           return false;
       }
       if(phone_no.length() != 10) {
           return false;
       }
       for(int i = 0; i < phone_no.length(); i++) {
           char c = phone_no.charAt(i);
           if((c > '0' && c <= '9')) {
               return false;
           }
       }
       return true;
    }
}
