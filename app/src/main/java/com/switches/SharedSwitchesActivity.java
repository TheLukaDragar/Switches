package com.switches;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.TwoStatePreference;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SharedSwitchesActivity extends AppCompatActivity {


    private EditText userid;
    private EditText switchnumber;
    private SwitchButton switchButton;
    private FloatingActionMenu Fabmenu;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;

    private FirebaseAuth mAuth;
    private boolean created;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Connected Switches");
        setContentView(R.layout.activity_shared_switches);

        Fabmenu=findViewById(R.id.fabmenu2);
        fab1=findViewById(R.id.menu_item21);
        fab1.setOnClickListener(clickListener);
        fab2=findViewById(R.id.menu_item22);
        fab2.setOnClickListener(clickListener);

        userid = findViewById(R.id.name);
        switchnumber = findViewById(R.id.editText2);
       com.suke.widget.SwitchButton switchButton=findViewById(R.id.switch_button);

        Intent intent = getIntent();
        SharedPreferences pref = getSharedPreferences("MyPref",MODE_PRIVATE);
       boolean manually = pref.getBoolean("manually", false);
        if (manually) {
            Toast.makeText(getApplicationContext() , "manually", Toast.LENGTH_SHORT).show();
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
          //  Toast.makeText(getApplicationContext() , "", Toast.LENGTH_SHORT).show();
            GetArrayFromDB();


        }

        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                Syncer();
                //do something
                handler.postDelayed(this, delay);
            }
        }, delay);




    }


    private void GetArrayFromDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser userin = mAuth.getCurrentUser();

        String user_id = userin.getUid();

        DocumentReference docRef = db.collection("users").document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String TAG="getarrayfromdb";
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {

                        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                        List<String> stringList = (List<String>) document.get("SharedSwitchesList");

                        if(stringList==null){
                           // Toast.makeText(getApplicationContext() , "You dont have any yet add one", Toast.LENGTH_SHORT).show();
                            return;


                        }


                        SharedPreferences.Editor editor = prefs.edit();



                        Set<String> Array = new HashSet<String>(stringList);




                        editor.putStringSet("sharedswitchesarray",Array); editor.commit();

                        MakeSwitches();
                        Syncer();

                        //TODO Clear on logout




                    } else {
                        Log.d(TAG, "No such document");
                        Toast.makeText(getApplicationContext() , "No shared switches found yet", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                //TODO do your job
            }
        });


    }


    private void test() {

        final String id= userid.getText().toString();
        if (id.matches("")) {
            Toast.makeText(this, "You did not enter id", Toast.LENGTH_SHORT).show();
            return;
        }

        final String snum=switchnumber.getText().toString();

        if (snum.matches("")) {
            Toast.makeText(this, "enter switch id number", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

       // Toast.makeText(getApplicationContext() , id, Toast.LENGTH_SHORT).show();

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
                        com.suke.widget.SwitchButton switchb=findViewById(R.id.switch_button);
                        switchb.setVisibility(View.GONE);


                        Toast.makeText(getApplicationContext() , "Found!", Toast.LENGTH_SHORT).show();


                        Set<String> oldSet = prefs.getStringSet("sharedswitchesarray", new HashSet<String>());

//make a copy, update it and save it
                        Set<String> newStrSet = new HashSet<String>();
                        newStrSet.add(id+snum);
                        if (oldSet != null) {
                            newStrSet.addAll(oldSet);
                        }

                        editor.putStringSet("sharedswitchesarray",newStrSet); editor.commit();
                        //TODO Clear on logout
                        editor.putBoolean("manually",false);
                        editor.commit();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser userin = mAuth.getCurrentUser();

                            String user_id = userin.getUid();

                        String[] sw = newStrSet.toArray(new String[newStrSet.size()]);

                        Map<String, Object> user = new HashMap<>();



                        user.put("SharedSwitchesList", Arrays.asList(sw));


                            db.collection("users").document(user_id)
                                    .set(user, SetOptions.merge());




                        MakeSwitches();


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




    private void Syncer(){

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        Set<String> sharedarray = pref.getStringSet("sharedswitchesarray", new HashSet<String>());
        String[] sw = sharedarray.toArray(new String[sharedarray.size()]);

        for (int i=0; i<sw.length; i++) {

           String txt=sw[i];
           final String shared_switch_id = txt.substring(txt.length() - 1);
            String shared_switch_user_id= txt.replaceFirst(".$","");
           // Toast.makeText(getApplicationContext() , shared_switch_user_id, Toast.LENGTH_SHORT).show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(shared_switch_user_id);
            //final int finalI = i;
            final int finalI = i;
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String TAG="syncer";
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                            Set<String> sharedarray = pref.getStringSet("sharedswitchesarray", new HashSet<String>());
                            String[] sw = sharedarray.toArray(new String[sharedarray.size()]);
                            String txt=sw[finalI];
                            String shared_switch_user_id= txt.replaceFirst(".$","");



                                String switchstate = document.getString("switch"+shared_switch_id);
                                String username = document.getString("username");
                           // Toast.makeText(getApplicationContext() ,"switch state of 1 ="+switchstate, Toast.LENGTH_SHORT).show();
                                com.suke.widget.SwitchButton btn1 = findViewById(finalI);
                                TextView textView = findViewById(100+finalI);

                                if (username==null){
                                    textView.setText("User deleted this switch");

                                }
                                else {
                                    textView.setText(username+"'s switch "+shared_switch_id);

                                }


                                String ena = "1";
                                String nula = "0";
                                if(switchstate == null){
                                    Toast.makeText(getApplicationContext() ,"Switch isnt there", Toast.LENGTH_SHORT).show();


                                }
                                if (switchstate != null && switchstate.equals(ena)) {
                                    btn1.setChecked(true);

                                    //Toast.makeText(this,Switchids,Toast.LENGTH_LONG).show();


                                }
                                if (switchstate != null && switchstate.equals(nula)) {
                                    btn1.setChecked(false);
                                    //Toast.makeText(this,"zero",Toast.LENGTH_LONG).show();
                                }

                                created=true;







                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });








        }























    }








    private void MakeSwitches() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        Set<String> sharedarray = pref.getStringSet("sharedswitchesarray", new HashSet<String>());
        String[] sw = sharedarray.toArray(new String[sharedarray.size()]);


       // int counts = sharedarray.size();
       // Toast.makeText(getApplicationContext() , String.valueOf(counts), Toast.LENGTH_SHORT).show();
       // Toast.makeText(getApplicationContext() , sharedarray.toString(), Toast.LENGTH_SHORT).show();



        float width = getResources().getDimension(R.dimen.btn_width);
        float height = getResources().getDimension(R.dimen.btn_height);
        LinearLayout linear = findViewById(R.id.RelLay);


        for (int i=0; i<sw.length; i++) {





            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(

                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            //params.width= (int) width;
            params.height= (int) height;
            params.setMargins(20, 5, 5, 5);

            TextView textView = new TextView(this);
            textView.setId(100+i);


             String text="New shared switch added";

             //textView.setText(text);

            LinearLayout.LayoutParams paramstxt = new LinearLayout.LayoutParams(

                           LinearLayout.LayoutParams.MATCH_PARENT,
                           LinearLayout.LayoutParams.WRAP_CONTENT);

                     paramstxt.gravity= Gravity.START;
                    //paramstxt.width= (int) width;
                   // paramstxt.height= (int) height;

                      paramstxt.setMargins(50, 10, 5, 0);
                     linear.addView(textView,paramstxt);


                final com.suke.widget.SwitchButton btn = new SwitchButton(this);
                btn.setId(i);
                final int id_ = btn.getId();


                //btn.setLayoutParams(new LinearLayout.LayoutParams(165, 60));
                //btn.setText("button " + id_);
                // btn.setBackgroundColor(Color.rgb(70, 80, 90));


                linear.addView(btn, params);

                final com.suke.widget.SwitchButton btn1 = (SwitchButton) findViewById(id_);
                Animation switchanim= AnimationUtils.loadAnimation(this, R.anim.switchanim);
                btn1.setAnimation(switchanim);
            btn1.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                    if (isChecked&&created){

                      //  Toast.makeText(view.getContext(),
                           //     "I do nothing", Toast.LENGTH_SHORT)
                        //        .show();

                        //TODO MAKE SWITCH BAXK





                    }


                }
            });






        }




    }

    private View.OnClickListener clickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.menu_item21:

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();

                    mAuth = FirebaseAuth.getInstance();
                    String user_id=  mAuth.getUid();

                    Map<String, Object> user = new HashMap<>();

                    user.put("SharedSwitchesList", FieldValue.delete());

                    pref.edit().remove("sharedswitchesarray")
                            .commit();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    if (user_id != null) {
                        db.collection("users").document(user_id)
                                .set(user, SetOptions.merge());
                    }
                    else{

                        Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();
                    }

                case R.id.menu_item22:

                    finish();

















            }




        }
    };


    @Override
    public void onBackPressed() {

            SharedPreferences pref = getSharedPreferences("MyPref",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("manually", false);
            editor.commit();

       SharedSwitchesActivity.super.onBackPressed();


    }
        }







