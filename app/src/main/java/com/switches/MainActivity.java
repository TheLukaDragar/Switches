package com.switches;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.protobuf.StringValue;
import com.suke.widget.SwitchButton;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private TextView text1;
    private TextView text2;
    private TextView text3;
    private FloatingActionMenu Fabmenu;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    private FloatingActionButton fab5;


    ;

    private FirebaseAuth mAuth;
    // [END declare_auth]

    //firststrtcode
    SharedPreferences prefs = null;

    private GoogleSignInClient mGoogleSignInClient;
    private String TAG;
    Switch aSwitch = null;
    private boolean islogedin;
    private boolean lockswitches;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //com.suke.widget.SwitchButton switchButton = (com.suke.widget.SwitchButton)
                //findViewById(R.id.switch_button);
        Fabmenu=findViewById(R.id.fabmenu);
        fab1=findViewById(R.id.menu_item);
        fab1.setOnClickListener(clickListener);
        fab2=findViewById(R.id.menu_item2);
        fab2.setOnClickListener(clickListener);
        fab3=findViewById(R.id.menu_item3);
        fab3.setOnClickListener(clickListener);
        fab4=findViewById(R.id.menu_item4);
        fab4.setOnClickListener(clickListener);
        fab5=findViewById(R.id.menu_item5);
        fab5.setOnClickListener(clickListener);


        Fabmenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    //String text = "Menu opened";
                    //Toast.makeText(getApplicationContext() , text, Toast.LENGTH_LONG).show();
                } else {
                    if(lockswitches){
                        lockswitches=false;
                        fab1.setVisibility(View.VISIBLE);
                        fab3.setVisibility(View.VISIBLE);
                        fab4.setVisibility(View.GONE);
                        fab5.setVisibility(View.GONE);
                        Fabmenu.setMenuButtonLabelText("Switch !");
                        Fabmenu.open(true);
                        ClearSwitchesFromLay();
                        SwitchesDoAll();


                    }
                  // String text = "Menu closed";
                   // Toast.makeText(getApplicationContext() , text, Toast.LENGTH_LONG).show();
                }


            }
        });

        SwitchesDoAll();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String check = pref.getString("checkusername","");
        Boolean DoAll = pref.getBoolean("SwitchesDoAll",false);

        SharedPreferences.Editor editor = pref.edit();

        if(DoAll){
            DoAll();
        }

        // Toast.makeText(this, check, Toast.LENGTH_SHORT).show();
        if (check.equalsIgnoreCase("1")) {
            editor.putString("checkusername", "0");
            editor.commit();
            checkusername();
            Toast.makeText(this, "checking username..", Toast.LENGTH_SHORT).show();
        }




        checkuser();




        aSwitch = (Switch) findViewById(R.id.MySwitch);
        aSwitch.setOnCheckedChangeListener(this);

       // DocumentReference ref = db.collection("users").document();
       // String myId = ref.getId();
        //text4.setText(myId);

        if (prefs.getBoolean("firstrun", true)) {

            Intent myIntent = new Intent(MainActivity.this, GoogleSignInActivity.class);
            myIntent.putExtra("makelogin", "0");
            myIntent.putExtra("norestart", true)
            ; //Optional parame



            MainActivity.this.startActivity(myIntent);
            finish();


            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();
        }

        run();
        texts();

    }

    private void DoAll() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();


        Toast.makeText(getApplicationContext() , "Please Restart ;)", Toast.LENGTH_LONG).show();

        //startActivity(getIntent());
        SwitchesDoAll();
        editor.putBoolean("SwitchesDoAll",false);
        editor.commit();
        finish();
    }

    private void SwitchesDoAll() {
        getNumOfSwitches();
        // ReadForSwitches();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences("com.switches", MODE_PRIVATE);

        //String user_id = prefs.getString("user_id", "1");

        mAuth = FirebaseAuth.getInstance();
        String user_id=  mAuth.getUid();

        if (user_id != null && !user_id.equals("")) {

            //do something


            DocumentReference docRef = db.collection("users").document(
                    user_id);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                            int iswitch = pref.getInt("numberofswitches", 0);

                            for (int i = 1; i <= iswitch; i++) {
                                String switchstate = document.getString("switch"+i);


                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("switch"+i,switchstate);
                               // Toast.makeText(getApplicationContext() , "Reading", Toast.LENGTH_SHORT).show();
                                editor.commit();

                            }
                            // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            //String test = document.getString("sw");

                            //SharedPreferences.Editor editor = prefs.edit();
                            // editor.putString("testemail", test);
                            //editor.apply();

                            //
                            // text3.setText(test);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }
        MakeSwitches();
        SyncSwitches();
    }

    private void getNumOfSwitches() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences("com.switches", MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        String user_id=  mAuth.getUid();

        if (user_id != null && !user_id.equals("")) {

            DocumentReference docRef = db.collection("users").document(
                    user_id);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            String num = document.getString("numberofswitches");
                            if(num != null && !num.trim().isEmpty()){
                                int  num2 = Integer.parseInt(num);

                                SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);


                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("numberofswitches",num2);
                                //Toast.makeText(getApplicationContext() , "reading data", Toast.LENGTH_LONG).show();
                                editor.commit();
                            }




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

    private void ReadForSwitches() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences("com.switches", MODE_PRIVATE);

        //String user_id = prefs.getString("user_id", "1");

        mAuth = FirebaseAuth.getInstance();
        String user_id=  mAuth.getUid();

        if (user_id != null && !user_id.equals("")) {

            //do something


            DocumentReference docRef = db.collection("users").document(
                    user_id);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                            int iswitch = pref.getInt("numberofswitches", 0);

                            for (int i = 1; i <= iswitch; i++) {
                                String switchstate = document.getString("switch"+i);


                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("switch"+i,switchstate);
                                //Toast.makeText(getApplicationContext() , "Reading", Toast.LENGTH_SHORT).show();
                                editor.commit();

                            }
                            // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            //String test = document.getString("sw");

                            //SharedPreferences.Editor editor = prefs.edit();
                            // editor.putString("testemail", test);
                            //editor.apply();

                            //
                            // text3.setText(test);
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

    private void ClearSwitchesandData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        int iswitch= pref.getInt("numberofswitches",0);

        LinearLayout linear = findViewById(R.id.lay);
        mAuth = FirebaseAuth.getInstance();
        String user_id=  mAuth.getUid();




        for (int i = 1; i <= iswitch; i++) {

            pref.edit().remove("switch"+i).commit();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> user = new HashMap<>();
            user.put("switch"+i,"cleared");
            user.put("numberofswitches","0");

            if (user_id != null) {
                db.collection("users").document(user_id)
                        .set(user, SetOptions.merge());
            }
            else{

                Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();
            }


            com.suke.widget.SwitchButton btn1 = findViewById(i);
            linear.removeView(btn1);
        }

        pref.edit().remove("numberofswitches").commit();


    }

    private void ClearSwitches() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        int iswitch= pref.getInt("numberofswitches",0);
        LinearLayout linear = findViewById(R.id.lay);


        for (int i = 1; i <= iswitch; i++) {

            pref.edit().remove("switch"+i).commit();
            com.suke.widget.SwitchButton btn1 = findViewById(i);
            linear.removeView(btn1);


        }

        pref.edit().remove("numberofswitches").commit();


    }
    private void MakeSwitches() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        int iswitch= pref.getInt("numberofswitches",0);

        float width = getResources().getDimension(R.dimen.btn_width);
        float height = getResources().getDimension(R.dimen.btn_height);
        LinearLayout linear = findViewById(R.id.lay);
        for (int i = 1; i <= iswitch; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(

                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width= (int) width;
            params.height= (int) height;
            params.setMargins(20, 40, 5, 40);

           // TextView textView = new TextView(this);
           // final String text= Integer.toString(i);
           // textView.setText("Switch: " +text);

           // LinearLayout.LayoutParams paramstxt = new LinearLayout.LayoutParams(

            //        LinearLayout.LayoutParams.MATCH_PARENT,
             //       LinearLayout.LayoutParams.WRAP_CONTENT);
            //paramstxt.addR
           // paramstxt.gravity= Gravity.END;
            //paramstxt.width= (int) width;
            //paramstxt.height= (int) height;
            // paramstxt.s
          //  paramstxt.setMargins(125, 10, 5, 0);
           // linear.addView(textView,paramstxt);

           if(!lockswitches){
               com.suke.widget.SwitchButton btn = new SwitchButton(this);
               btn.setId(i);
               final int id_ = btn.getId();
               //btn.setLayoutParams(new LinearLayout.LayoutParams(165, 60));
               //btn.setText("button " + id_);
               // btn.setBackgroundColor(Color.rgb(70, 80, 90));
               linear.addView(btn, params);
               com.suke.widget.SwitchButton btn1 = (SwitchButton) findViewById(id_);
               Animation switchanim= AnimationUtils.loadAnimation(this, R.anim.switchanim);
               btn1.setAnimation(switchanim);
               btn1.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                       if (isChecked){

                           Toast.makeText(view.getContext(),
                                   "Switch on = " + id_, Toast.LENGTH_SHORT)
                                   .show();
                           SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                           String myId=pref.getString("user_id", null);
                           int iswitch = pref.getInt("numberofswitches", 0);
                           for (int i = 1; i <= iswitch; i++) {
                               SharedPreferences.Editor editor = prefs.edit();
                               editor.putString("switch"+id_,"1");
                               // Toast.makeText(getApplicationContext() , "", Toast.LENGTH_SHORT).show();
                               editor.commit();
                           }
                           FirebaseFirestore db = FirebaseFirestore.getInstance();
                           Map<String, Object> user = new HashMap<>();
                           user.put("switch"+id_, "1");
                           if (myId != null) {
                               db.collection("users").document(myId)
                                       .set(user, SetOptions.merge());
                           }
                           else{

                               Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();
                           }

                       }
                       if (!isChecked){
                           SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                           int iswitch = pref.getInt("numberofswitches", 0);

                           for (int i = 1; i <= iswitch; i++) {
                               SharedPreferences.Editor editor = prefs.edit();
                               editor.putString("switch"+id_,"0");
                               //  Toast.makeText(getApplicationContext() , "Reading", Toast.LENGTH_SHORT).show();
                               editor.commit();
                           }
                           String myId=pref.getString("user_id", null);
                           FirebaseFirestore db = FirebaseFirestore.getInstance();
                           Map<String, Object> user = new HashMap<>();
                           user.put("switch"+id_, "0");

                           if (myId != null) {
                               db.collection("users").document(myId)
                                       .set(user, SetOptions.merge());
                           }
                           else{
                               Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();
                           }
                       }





                       //TODO do your job
                   }
               });
           }
            if(lockswitches){
                com.suke.widget.SwitchButton btn = new SwitchButton(this);
                //TODO MAKE THEMC HANGE COLORS !!!
                btn.setId(i);
                final int id_ = btn.getId();
                linear.addView(btn, params);

                com.suke.widget.SwitchButton btn1 = (SwitchButton) findViewById(id_);

                Animation switchanim= AnimationUtils.loadAnimation(this, R.anim.switchanim);
                btn1.setAnimation(switchanim);


                btn1.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                        if (isChecked){
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                            int iswitch = pref.getInt("numberofswitches", 0);

                            for (int i = 1; i <= iswitch; i++) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("ConnectSwitch", String.valueOf(id_));
                                //  Toast.makeText(getApplicationContext() , "Reading", Toast.LENGTH_SHORT).show();
                                editor.commit();
                                CreateConnection();
                            }



                        }
                        if (!isChecked){
                            Toast.makeText(getApplicationContext() , "Bug", Toast.LENGTH_SHORT).show();


                        }





                        //TODO do your job
                    }
                });
            }
        }
    }


    private void SyncSwitches() {

      //  Toast.makeText(this,"SYNCING",Toast.LENGTH_LONG).show();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        int iswitch=pref.getInt("numberofswitches",0);


        for (int i = 1; i <= iswitch; i++) {

            //com.suke.widget.SwitchButton btn = new SwitchButton(this);
            ///btn.setId(i);
            //final int id_ = btn.getId();

            String Switchids = pref.getString("switch"+i,"hmm");
            String ena="1";
            String nula="0";

            com.suke.widget.SwitchButton btn1 = findViewById(i);
            if (Switchids.equals(ena)){
                btn1.setChecked(true);
                //Toast.makeText(this,Switchids,Toast.LENGTH_LONG).show();



            }
            if (Switchids.equals(nula)){
                btn1.setChecked(false);
                //Toast.makeText(this,"zero",Toast.LENGTH_LONG).show();
            }

           // Toast.makeText(this,Switchids,Toast.LENGTH_LONG).show();








        }



    }

    private void AddSwitch() {



        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        int iswitch= pref.getInt("numberofswitches",0);



        float width = getResources().getDimension(R.dimen.btn_width);
        float height = getResources().getDimension(R.dimen.btn_height);
        LinearLayout linear = findViewById(R.id.lay);





            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(

                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width= (int) width;
            params.height= (int) height;

            params.setMargins(20, 40, 5, 40);


            com.suke.widget.SwitchButton btn = new SwitchButton(this);
            btn.setId(iswitch);
            final int id_ = btn.getId();
            //btn.setLayoutParams(new LinearLayout.LayoutParams(165, 60));
            // btn.setText("button " + id_);
            // btn.setBackgroundColor(Color.rgb(70, 80, 90));
            linear.addView(btn, params);
            com.suke.widget.SwitchButton btn1 = (SwitchButton) findViewById(id_);
        Animation switchanim= AnimationUtils.loadAnimation(this, R.anim.switchanim);
        btn1.setAnimation(switchanim);
        btn1.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(view.getContext(),
                            "Switch on = " + id_, Toast.LENGTH_SHORT)
                            .show();

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

                    int iswitch = pref.getInt("numberofswitches", 0);

                    for (int i = 1; i <= iswitch; i++) {

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("switch"+id_,"1");
                       // Toast.makeText(getApplicationContext() , "Reading", Toast.LENGTH_SHORT).show();
                        editor.commit();

                    }



                    String myId=pref.getString("user_id", null);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> user = new HashMap<>();
                    user.put("switch"+id_, "1");

                    if (myId != null) {
                        db.collection("users").document(myId)
                                .set(user, SetOptions.merge());


                    }
                    else{

                        Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();
                    }



                }
                if (!isChecked){
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    int iswitch = pref.getInt("numberofswitches", 0);

                    for (int i = 1; i <= iswitch; i++) {

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("switch"+id_,"0");
                       // Toast.makeText(getApplicationContext() , "Reading", Toast.LENGTH_SHORT).show();
                        editor.commit();

                    }

                    String myId=pref.getString("user_id", null);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> user = new HashMap<>();
                    user.put("switch"+id_, "0");

                    if (myId != null) {
                        db.collection("users").document(myId)
                                .set(user, SetOptions.merge());

                    }
                    else{
                        Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();


                    }



                }



                //TODO do your job
            }
        });

        }


    private void getusername() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String user_id=  mAuth.getUid();


        DocumentReference docRef = db.collection("users").document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String test = document.getString("username");
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("username", test);
                        editor.commit();
                        // test();

                    } else {
                        Log.d(TAG, "No such document");
                        test();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    private void checkuser() {
        prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        //fitsrtart
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            islogedin = true;
            Snackbar.make(findViewById(R.id.main_layout), "Logged In", Snackbar.LENGTH_SHORT).show();
            getusername();

            // User is signed in
        } else {
            islogedin = false;
            Toast.makeText(this, "not logged in", Toast.LENGTH_SHORT).show();
            // No user is signed in
        }

    }

    private void checkusername() {
       // Toast.makeText(this, "checking 2", Toast.LENGTH_SHORT).show();
        prefs = getSharedPreferences("com.switches", MODE_PRIVATE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String user_id=  mAuth.getUid();


        DocumentReference docRef = db.collection("users").document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String test = document.getString("username");
                       // SharedPreferences.Editor editor = prefs.edit();
                        //editor.putString("test", test);
                       // test();

                        if (test != null) {
                            if (test.equals("")) {

                                pleaseregister();

                            }
                            else
                            welcome();
                        }

                    } else {
                        Log.d(TAG, "No such document");
                        pleaseregister();

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });










    }

    private void test() {
        prefs = getSharedPreferences("MyPref", MODE_PRIVATE);

        //String test = prefs.getString("switch1", "hmm");

        Toast.makeText(this, "No Data yet", Toast.LENGTH_SHORT).show();

        //String test2 = prefs.getString("switch2", "hmm2");

       // Toast.makeText(this, test2+"dve", Toast.LENGTH_LONG).show();

    }

    private void pleaseregister() {
        Intent myIntent = new Intent(MainActivity.this, GoogleSignInActivity.class);
        myIntent.putExtra("makelogin", "0"); //Optional parame


        MainActivity.this.startActivity(myIntent);
        finish();

    }

    private void welcome() {

        Toast.makeText(this, "welcome", Toast.LENGTH_SHORT).show();
        SwitchesDoAll();

    }


    private void run() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        String user_id=pref.getString("user_id", null);
        String user_email=pref.getString("user_email", null);
        String username=pref.getString("username", null);






        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Access a Cloud Firestore instance from your Activity

    }

    private void texts() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        String user_id=pref.getString("user_id", null);
        String user_email=pref.getString("user_email", null);
        String username=pref.getString("username", null);

        text1 = findViewById(R.id.textView);
        text2 = findViewById(R.id.textView2);
        text3 = findViewById(R.id.textView3);


        String user_id2=("Firebase ID: "+user_id);
        String username2=("Username: "+username);
        String user_email2=("User email: "+user_email);

        text1.setText(user_id2);
        text2.setText(user_email2);
        text3.setText(username2);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu); //your file name
        MenuItem log_out = menu.findItem(R.id.log_out);
        MenuItem log_in = menu.findItem(R.id.log_in);

        if (islogedin) {
            log_in.setVisible(false);

            // valid!
        }
        if (!islogedin) {
            // valid!
            log_out.setVisible(false);
        }







        return super.onCreateOptionsMenu(menu);
    }

    public void onStart() {
        checkuser();

        super.onStart();
    }

    private View.OnClickListener clickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.menu_item:
                    if(!islogedin){
                        Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();
                        break;
                    }





                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                   int iswitch1= pref.getInt("numberofswitches",0);
                   if (iswitch1 >= 8){
                       Toast.makeText(getApplicationContext() , "Sorry that is the maximum", Toast.LENGTH_LONG).show();
                       break;

                   }
                   else
                    iswitch1 = iswitch1+1;




                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("numberofswitches", iswitch1);
                    editor.commit();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    mAuth = FirebaseAuth.getInstance();
                    String user_id=  mAuth.getUid();

                   String  numberString= Integer.toString(iswitch1);


                    Map<String, Object> user = new HashMap<>();
                    user.put("numberofswitches",numberString);
                    user.put("switch"+iswitch1,"created");

                    if (user_id != null) {
                        db.collection("users").document(user_id)
                                .set(user, SetOptions.merge());
                    }
                    else{

                        Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();
                    }
                    AddSwitch();
                    //ClearSwitches();
                   // MakeSwitches();

                    break;
                case R.id.menu_item2:
                    if(!islogedin){

                        Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();
                        break;
                    }
                    WhoWasClicked();
                   // Toast.makeText(getApplicationContext() , "2", Toast.LENGTH_LONG).show();
                    //ReadForSwitches();

                  //  SyncSwitches();



                   // ClearSwitches();

                   //SharedPreferences pref2 = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    //pref2.edit().remove("numberofswitches").commit();


                    break;

                case R.id.menu_item3:

                    if(!islogedin){
                        Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();
                        break;
                    }
                   // Toast.makeText(getApplicationContext() , "", Toast.LENGTH_LONG).show();
                    //ReadForSwitches();
                   // SyncSwitches();

                    //getNumOfSwitches();
                     ClearSwitchesandData();
                    break;


                case R.id.menu_item4:

                    if(!islogedin){
                        Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();
                        break;
                    }
                    // Toast.makeText(getApplicationContext() , "", Toast.LENGTH_LONG).show();
                    //ReadForSwitches();
                    // SyncSwitches();

                    //getNumOfSwitches();
                    AddManually();

                    break;


                case R.id.menu_item5:
                    if(!islogedin){
                        Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();
                        break;
                    }

                    Intent myIntent = new Intent(MainActivity.this, SharedSwitchesActivity.class);
                    //myIntent.putExtra("addmanually", true); //Optional parame

                    MainActivity.this.startActivity(myIntent);
                    break;



            }




        }
    };



    private void WhoWasClicked() {
        lockswitches=true;
        fab1.setVisibility(View.GONE);
        fab3.setVisibility(View.GONE);
        fab4.setVisibility(View.VISIBLE);
        fab5.setVisibility(View.VISIBLE);
        Fabmenu.setMenuButtonLabelText("Cancel selection");
        ClearSwitchesFromLay();
        MakeSwitches();


        //fab2.
    }

    private void ClearSwitchesFromLay() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        int iswitch= pref.getInt("numberofswitches",0);
        LinearLayout linear = findViewById(R.id.lay);


        for (int i = 1; i <= iswitch; i++) {

           // pref.edit().remove("switch"+i).commit();
            com.suke.widget.SwitchButton btn1 = findViewById(i);
            linear.removeView(btn1);




        }
    }

    private void CreateConnection() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String Switchtoconnect = pref.getString("ConnectSwitch",null);
        mAuth = FirebaseAuth.getInstance();
        String user_id=  mAuth.getUid();


       // String link = user_id+Switchtoconnect;
       // String full = "https://com.switches.page.link/?link=your_deep_link&apn=com.switches[&amv=minimum_version][&afl=fallback_link]";
       // DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
        //        .setLink(Uri.parse(link))
         //       .setDomainUriPrefix("https://switches.page.link")
                // Open links with this app on Android
          //      .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.switches").build())
                // Open links with com.example.ios on iOS
           //     .buildDynamicLink();

       // Uri dynamicLinkUri = dynamicLink.getUri();
      //  String ok = dynamicLinkUri.toString();

       // Toast.makeText(this,ok, Toast.LENGTH_LONG).show();


       // ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
       // ClipData clip = ClipData.newPlainText("test", ok);
       // clipboard.setPrimaryClip(clip);

      //  Intent sendIntent = new Intent();
       // sendIntent.setAction(Intent.ACTION_SEND);
       // sendIntent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri);
       // sendIntent.setType("text/plain");
       // startActivity(sendIntent);


    }
    private void AddManually() {
        Intent myIntent = new Intent(MainActivity.this, SharedSwitchesActivity.class);
        //myIntent.putExtra("addmanually", true); //Optional parame

        SharedPreferences pref = getSharedPreferences("MyPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("manually", true);
        editor.commit();
        MainActivity.this.startActivity(myIntent);



    }







































    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                       // Intent myIntent = new Intent(MainActivity.this, GoogleSignInActivity.class);
                        Snackbar.make(findViewById(R.id.main), "Logged out", Snackbar.LENGTH_SHORT).show();
                       // MainActivity.this.startActivity(myIntent);

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                        ClearSwitches();

                        pref.edit().remove("user_email").commit();
                        pref.edit().remove("user_id").commit();
                        pref.edit().remove("username").commit();
                       texts();
                       checkuser();
                        invalidateOptionsMenu();
                       // islogedin=false;


                    }
                });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

         // Clear the menu first

        /* Add the menu items */

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        if (item.getItemId() == R.id.log_out){//your code
            // EX : call intent if you want to swich to other activity
            signOut();

            return true;
            // case R.id.help:
            //your code
            //   return true;
        }
        if (item.getItemId() == R.id.log_in){//your code
            // EX : call intent if you want to swich to other activity
            Intent myIntent = new Intent(MainActivity.this, GoogleSignInActivity.class);
            myIntent.putExtra("makelogin", "1"); //Optional parame

            MainActivity.this.startActivity(myIntent);
            finish();


            return true;
            // case R.id.help:
            //your code
            //   return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void read(View  view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
       // db.collection("users")
          //      .get()
             //   .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              //      @Override
               //     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //        if (task.isSuccessful()) {
                  //          for (QueryDocumentSnapshot document : task.getResult()) {
                  ///              Log.d(TAG, document.getId() + " => " + document.getData());
                    //            text3.setText(getString(db.get));
                    //        }
                   //     } else {
                   //         Log.w(TAG, "Error getting documents.", task.getException());
                   //     }
                   // }
              //  });
        // Read from the database
        prefs = getSharedPreferences("com.switches", MODE_PRIVATE);

        //String user_id = prefs.getString("user_id", "1");

        mAuth = FirebaseAuth.getInstance();
        String user_id=  mAuth.getUid();
        text3.setText(user_id);

        DocumentReference docRef = db.collection("users").document(
                user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                       // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String test = document.getString("email");

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("testemail", test);
                        editor.apply();
                        test();
                        //
                        // text3.setText(test);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    public void send(View view) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        String myId=pref.getString("ID", null);
       // Toast.makeText(this,myId, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

            String myId=pref.getString("user_id", null);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> user = new HashMap<>();
            user.put("switch", "1");

            if (myId != null) {
                db.collection("users").document(myId)
                        .set(user, SetOptions.merge());
            }
            else{
                Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();


            }


            // do something when check is selected
        } else {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

            String myId=pref.getString("user_id", null);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> user = new HashMap<>();
            user.put("switch", "0");

            if (myId != null) {
                db.collection("users").document(myId)
                        .set(user, SetOptions.merge());
            }
            else{
                Snackbar.make(findViewById(R.id.main), "You need to log in", Snackbar.LENGTH_SHORT).show();


            }
        }



    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                       // ReadForSwitches();

                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }


}



