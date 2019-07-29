package com.switches;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashSet;
import java.util.Set;

import static android.app.Service.START_NOT_STICKY;
import static android.content.ContentValues.TAG;
import static com.switches.App.CHANNEL_ID;

public class NotifService extends Service {
    private boolean skip;
    @Override
    public void onCreate() {
         skip=true;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Shared Switches")
                .setContentText("I notify you when a connected switch is turned ON.")
                .setSmallIcon(R.drawable.service1)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);


        Listen();







        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    private void Listen() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        Set<String> sharedarray = pref.getStringSet("sharedswitchesarray", new HashSet<String>());
        String[] sw = sharedarray.toArray(new String[sharedarray.size()]);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (int i=0; i<sw.length; i++) {

            String txt=sw[i];
            final String shared_switch_id = txt.substring(txt.length() - 1);
            String shared_switch_user_id= txt.replaceFirst(".$","");


            final DocumentReference docRef = db.collection("users").document(shared_switch_user_id);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                      String switchchanged=  shared_switch_id;
                      String username=  snapshot.getString("username");
                      String id = snapshot.getString("id");

                        Syncer(switchchanged,username,id);
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void Syncer(final String switchchanged, String username, String id){






            // Toast.makeText(getApplicationContext() , shared_switch_user_id, Toast.LENGTH_SHORT).show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(id);
            //final int finalI = i;

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String TAG="syncer";
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {


                            String switchstate = document.getString("switch"+switchchanged);
                            String username = document.getString("username");
                            // Toast.makeText(getApplicationContext() ,"switch state of 1 ="+switchstate, Toast.LENGTH_SHORT).show();
                          //  com.suke.widget.SwitchButton btn1 = findViewById(finalI);
                           // TextView textView = findViewById(100+finalI);

                            if (username==null){
                               /// textView.setText("User deleted this switch");

                            }
                            else {
                               // textView.setText(username+"'s switch "+shared_switch_id);

                            }


                            String ena = "1";
                            String nula = "0";
                            if(switchstate == null){
                               // Toast.makeText(getApplicationContext() ,"Switch isnt there", Toast.LENGTH_SHORT).show();


                            }
                            if (switchstate != null && switchstate.equals(ena)&&!skip) {
                                //btn1.setChecked(true);
                                Toast.makeText(getApplicationContext() , username+"switch"+switchchanged+" is ON", Toast.LENGTH_LONG).show();
                                //Toast.makeText(this,"d",Toast.LENGTH_LONG).show();


                            }
                            if (switchstate != null && switchstate.equals(nula)) {
                               // Toast.makeText(getApplicationContext() , username+"switch"+shared_switch_id+" is OFF", Toast.LENGTH_LONG).show();
                                //btn1.setChecked(false);
                                //Toast.makeText(this,"zero",Toast.LENGTH_LONG).show();
                            }




                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                    skip=false;
                }
            });










}


}
