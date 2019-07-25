package com.switches;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.suke.widget.SwitchButton;


public class SharedSwitchesActivity extends AppCompatActivity {

    private boolean manually;
    private EditText userid;
    private EditText switchnumber;
    private SwitchButton switchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_switches);
        com.suke.widget.SwitchButton switchButton = (com.suke.widget.SwitchButton)
                findViewById(R.id.switch_button);

        userid = findViewById(R.id.name);
        switchnumber = findViewById(R.id.editText2);

        Intent intent = getIntent();
        manually = intent.getBooleanExtra("addmanually", false);
        if (manually) {
            userid.setVisibility(View.VISIBLE);
            switchnumber.setVisibility(View.VISIBLE);
            switchButton.setVisibility(View.VISIBLE);
            switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    if(isChecked){
                        test();







                    }
                    //TODO do your job
                }
            });


        }

        if ((!manually)){
            Sync();

        }


        //


    }



    private void test() {

        String id= userid.getText().toString();
        final String snum=switchnumber.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Toast.makeText(getApplicationContext() , id, Toast.LENGTH_SHORT).show();

        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String TAG="SharedSwitches";
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {

                        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);



                        String switchstate = document.getString("switch"+snum);


                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("sharedswitch",switchstate);
                        //
                        editor.commit();

                        userid.setVisibility(View.GONE);
                        switchnumber.setVisibility(View.GONE);
                        //switchButton.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext() , "Found!", Toast.LENGTH_SHORT).show();
                        manually=false;
                        Sync();


                    } else {
                        Log.d(TAG, "No such document");
                        Toast.makeText(getApplicationContext() , "not find", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                //TODO do your job
            }
        });






    }



    private void Sync() {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String ss = prefs.getString("sharedswitch", "0");

        RelativeLayout linear = findViewById(R.id.layShared);


        com.suke.widget.SwitchButton btn = new SwitchButton(this);
       // btn.setId(Integer.parseInt("sharedswitch" + userid));
        linear.addView(btn);
        Animation switchanim = AnimationUtils.loadAnimation(this, R.anim.switchanim);
        btn.setAnimation(switchanim);

        String ena = "1";
        String nula = "0";


        if (ss != null && ss.equals(ena)) {

            btn.setChecked(true);


        }


        if (ss != null && ss.equals(nula)) {
            btn.setChecked(false);
        }

    }


}