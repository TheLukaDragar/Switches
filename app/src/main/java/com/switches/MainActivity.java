package com.switches;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private TextView text1;
    private TextView text2;
    private TextView text3;
    ;

    private FirebaseAuth mAuth;
    // [END declare_auth]

    //firststrtcode
    SharedPreferences prefs = null;

    private GoogleSignInClient mGoogleSignInClient;
    private String TAG;
    Switch aSwitch = null;
    private boolean islogedin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String check = pref.getString("checkusername","");
        SharedPreferences.Editor editor = pref.edit();

        // Toast.makeText(this, check, Toast.LENGTH_SHORT).show();
        if (check.equalsIgnoreCase("1")) {
            editor.putString("checkusername", "0");
            editor.apply();
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
            myIntent.putExtra("makelogin", "0"); //Optional parame


            MainActivity.this.startActivity(myIntent);
            finish();

            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();
        }

        run();
        texts();


















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
                        editor.apply();
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
            Toast.makeText(this, "logged in", Toast.LENGTH_SHORT).show();
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
                        test();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });










    }

    private void test() {
        prefs = getSharedPreferences("com.switches", MODE_PRIVATE);

        String test = prefs.getString("testemail", "?");

       // Toast.makeText(this, test, Toast.LENGTH_SHORT).show();

    }

    private void pleaseregister() {
        Intent myIntent = new Intent(MainActivity.this, GoogleSignInActivity.class);
        myIntent.putExtra("makelogin", "0"); //Optional parame


        MainActivity.this.startActivity(myIntent);
        finish();

    }

    private void welcome() {

        Toast.makeText(this, "welcome", Toast.LENGTH_SHORT).show();
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
}



