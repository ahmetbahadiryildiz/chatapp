package com.greemlock.edutherapist;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.greemlock.edutherapist.Objects.User;
import com.sendbird.calls.AcceptParams;
import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.RoomInvitation;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.SendBirdException;
import com.sendbird.calls.handler.AuthenticateHandler;
import com.sendbird.calls.handler.SendBirdCallListener;

public class CallService extends Service {
    private static final String APP_ID = "B43D7E3E-5930-40F3-951A-D32347F7A168";
    String ACCESS_TOKEN = null;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public void onCreate() {
        super.onCreate();
        SendBirdCall.init(getApplicationContext(), APP_ID);
        SendBirdCall.Options.setDirectCallDialingSoundOnWhenSilentOrVibrateMode(true);

        AuthenticateParams params = new AuthenticateParams(user.getUid())
                .setAccessToken(ACCESS_TOKEN);

        SendBirdCall.authenticate(params, new AuthenticateHandler() {
            @Override
            public void onResult(@Nullable com.sendbird.calls.User user, @Nullable SendBirdException e) {
                if (e == null) {
                    waitForCalls();
                } else {
                    Toast.makeText(CallService.this.getApplicationContext(), e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /* access modifiers changed from: private */
    public void waitForCalls() {
        SendBirdCall.addListener("1234567", new SendBirdCallListener() {
            public void onInvitationReceived(RoomInvitation roomInvitation) {
            }

            public void onRinging(DirectCall call) {
                FirebaseDatabase.getInstance().getReference("users").orderByChild("userUID").equalTo(call.getCaller().getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            User user1 = dataSnapshot.getValue(User.class);
                            sendNotification(user1.getUserDisplayName(), call.getCallId());
                            /*call.accept(new AcceptParams());
                            Intent intent = new Intent(getApplicationContext(), VideoCallActivity.class);
                            intent.putExtra("callID", call.getCallId());
                            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);*/
                        }
                    }

                    public void onCancelled(DatabaseError error) {
                        Log.e("error",error.getMessage());
                    }
                });
            }
        });
        firebaseToken();
    }

    private void firebaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            public void onSuccess(String token) {
                SendBirdCall.registerPushToken(token, false, e ->{
                    if (e != null) {
                        Log.i("TAG", "[PushUtils] registerPushToken() => e: " + e.getMessage());
                        return;
                    }
                    Log.i("FCM Token", token);
                });
            }
        });
    }

    /* access modifiers changed from: private */
    public void sendNotification(String callerName, String callID) {
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(NotificationCompat.CATEGORY_STATUS, true);
        intent.putExtra("callID", callID);
        PendingIntent gidilecekIntent = PendingIntent.getActivity(this,1 , intent, PendingIntent.FLAG_MUTABLE| PendingIntent.FLAG_UPDATE_CURRENT );

        Log.e("INCOMING CALL",callID);

        if (Build.VERSION.SDK_INT >= 26) {
            if (notificationManager.getNotificationChannel("channelCall") == null) {
                NotificationChannel kanal = new NotificationChannel("channelCall", "Call Channel", NotificationManager.IMPORTANCE_HIGH);
                kanal.setDescription("the_description");
                notificationManager.createNotificationChannel(kanal);
            }
            builder = new NotificationCompat.Builder(this, "channelCall");
            builder.setContentTitle(String.format("There is a call from %s",callerName))
                    .setContentText("Click to answer")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setAutoCancel(true).
                    setContentIntent(gidilecekIntent);
        } else {
            builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("Başlık")
                    .setContentText("İçerik").
                    setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(gidilecekIntent)
                    .setAutoCancel(true)
                    .setPriority(1);
        }
        notificationManager.notify(1, builder.build());
    }
}
