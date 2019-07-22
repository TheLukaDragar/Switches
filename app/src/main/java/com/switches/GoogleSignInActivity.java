package com.switches;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */
public class GoogleSignInActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private String username="";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private TextView signintext;
    private EditText nameed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        // Views
      //  mStatusTextView = findViewById(R.id.status);
      //  mDetailTextView = findViewById(R.id.detail);
        signintext=findViewById(R.id.signin);
        nameed=findViewById(R.id.editText);


        // Button listeners
        findViewById(R.id.signInButton).setOnClickListener(this);
       // findViewById(R.id.signOutButton).setOnClickListener(this);
       // findViewById(R.id.disconnectButton).setOnClickListener(this);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        ObjectAnimator animY1 = ObjectAnimator.ofFloat(signintext, "translationY", -300f, 0f);
        animY1.setDuration(1500);//1sec
        animY1.setInterpolator(new BounceInterpolator());
        animY1.setRepeatCount(0);
        animY1.start();

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);
        findViewById(R.id.signInButton).startAnimation(fadeIn);




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Snackbar.make(findViewById(R.id.main_layout), "Signed In!", Snackbar.LENGTH_SHORT).show();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Snackbar.make(findViewById(R.id.main_layout), "Sign in failed.!", Snackbar.LENGTH_SHORT).show();

                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

        }

    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information



                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            Intent myIntent = new Intent(GoogleSignInActivity.this, MainActivity.class);
                            if (user != null) {
                                myIntent.putExtra("user_email", getString(R.string.google_status_fmt, user.getEmail()));
                            }
                            assert user != null;
                            myIntent.putExtra("user_id",(getString(R.string.firebase_status_fmt, user.getUid())));//Optional parameters

                            myIntent.putExtra("username",(username));//Optional parameters

                            GoogleSignInActivity.this.startActivity(myIntent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed. Check connection", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {

        String namecheck = nameed.getText().toString();
        if (namecheck.matches("")) {
            Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
            return;

        }

        username= nameed.getText().toString();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
           // mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
          //  mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
          //  signintext.setText("Logged in as"+getString(R.string.google_status_fmt, user.getEmail()));
          //  TranslateAnimation anim = new TranslateAnimation(0, 0, 0, 100); //first 0 is start point, 150 is end point horizontal
           // anim.setDuration(1000); // 1000 ms = 1second




           // findViewById(R.id.signInButton).setVisibility(View.GONE);
           // ObjectAnimator animY = ObjectAnimator.ofFloat(findViewById(R.id.signInButton), "translationY", -100f, 0f);
            //animY.setDuration(1000);//1sec
            //animY.setInterpolator(new BounceInterpolator());
           // animY.setRepeatCount(1);
           // animY.start();

            //findViewById(R.id.signOutAndDisconnect).setVisibility(View.VISIBLE);
        } else {
            //mStatusTextView.setText(R.string.signed_out);
           // mDetailTextView.setText(null);

            //findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
          //  findViewById(R.id.signOutAndDisconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signInButton) {
            signIn();
        } //else if (i == R.id.signOutButton) {
          //  signOut();
         //else if (i == R.id.disconnectButton) {
          //  revokeAccess();
       // }
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
        return super.onOptionsItemSelected(item);
    }

}
