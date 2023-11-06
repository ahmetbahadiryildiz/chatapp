package com.greemlock.edutherapist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.SendBirdVideoView;
import com.sendbird.calls.VideoDevice;
import com.sendbird.calls.handler.DirectCallListener;

import org.webrtc.RendererCommon;

import java.io.File;
import java.util.List;

public class VideoCallActivity extends AppCompatActivity {
    ImageView imageViewMicOff;
    ImageView imageViewVideoOff;
    boolean isMicOpen = true;
    boolean isVideoOpen = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        getSupportActionBar().hide();
        imageViewMicOff = findViewById(R.id.imageViewMicOff);
        imageViewVideoOff = findViewById(R.id.imageViewVideoOff);

        imageViewVideoOff.setVisibility(View.INVISIBLE);
        imageViewMicOff.setVisibility(View.INVISIBLE);


        String callID = getIntent().getStringExtra("callID");
        String calleeName = getIntent().getStringExtra("calleeName");

        DirectCall call = SendBirdCall.getCall(callID);
        SendBirdVideoView sendBirdVideoView = findViewById(R.id.video_view_fullscreen);
        sendBirdVideoView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        call.setLocalVideoView(sendBirdVideoView);

        SendBirdVideoView sendBirdVideoView1 = findViewById(R.id.video_view_small);
        sendBirdVideoView1.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        sendBirdVideoView1.setVisibility(View.INVISIBLE);

        ImageView imageViewCallee = findViewById(R.id.imageView3);
        TextView textViewCallee = findViewById(R.id.textView2);
        textViewCallee.setText(calleeName);

        StorageReference reference = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference = reference.child("profilePhotos/" + call.getCallee().getUserId());
        try {
            final File file = File.createTempFile("images", "jpg");
            storageReference.getFile(file).addOnSuccessListener((OnSuccessListener) new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    imageViewCallee.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception e) {
                    imageViewCallee.setBackgroundColor(Color.parseColor("#D5D5D5"));
                    imageViewCallee.setImageResource(R.drawable.ic_baseline_person_24);
                }
            });
        } catch (Exception e) {
            Log.e("hata", e.getMessage());
        }
        ImageButton button3 = findViewById(R.id.imageButtonEnd);
        button3.setOnClickListener(view ->{
            call.end();
        });

       List<VideoDevice> availableVideoDevices = call.getAvailableVideoDevices();
        VideoDevice currentVideoDevice = call.getCurrentVideoDevice();

       for (VideoDevice availableVideoDevice : availableVideoDevices) {
            if (!availableVideoDevice.getCameraCharacteristics().get(CameraCharacteristics.LENS_FACING)
                    .equals(currentVideoDevice.getCameraCharacteristics().get(CameraCharacteristics.LENS_FACING))) {
                call.selectVideoDevice(availableVideoDevice, e -> {
                    if (e != null) {
                    }
                });
            }
        }

        call.setListener(new DirectCallListener() {
            @Override
            public void onConnected(@NonNull DirectCall directCall) {
                Toast.makeText(VideoCallActivity.this, "CONNECTED", Toast.LENGTH_SHORT).show();
                ConstraintLayout layoutCall = findViewById(R.id.layoutCall);
                layoutCall.setVisibility(View.GONE);

                call.startVideo();

                SendBirdVideoView sendBirdVideoView = findViewById(R.id.video_view_small);
                sendBirdVideoView.setVisibility(View.VISIBLE);
                call.setLocalVideoView(sendBirdVideoView);

                call.setRemoteVideoView(findViewById(R.id.video_view_fullscreen));


                ImageButton imageButtonVideo = findViewById(R.id.imageButtonVideo);
                imageButtonVideo.setOnClickListener(view -> {
                    if (isVideoOpen) {
                        call.stopVideo();
                        imageButtonVideo.setImageResource(R.drawable.baseline_videocam_off_24);
                    } else {
                        call.startVideo();
                        imageButtonVideo.setImageResource(R.drawable.baseline_videocam_24);
                    }
                    isVideoOpen = !isVideoOpen;
                });

                ImageButton imageButtonMic = findViewById(R.id.imageButtonMic);
                imageButtonMic.setOnClickListener(view -> {
                    if (VideoCallActivity.this.isMicOpen) {
                        call.muteMicrophone();
                        imageButtonMic.setImageResource(R.drawable.baseline_mic_off_24);
                    } else {
                        call.unmuteMicrophone();
                        imageButtonMic.setImageResource(R.drawable.baseline_mic_24);
                    }
                    isMicOpen = !isMicOpen;
                });

                ImageButton imageButtonRotate = findViewById(R.id.imageButtonRotate);
                imageButtonRotate.setOnClickListener(view ->{
                    directCall.switchCamera(e -> {
                        if (e != null) {
                        }
                    });
                });

                ImageButton imageButtonEnd = findViewById(R.id.imageButtonEnd);
                imageButtonEnd.setOnClickListener(view -> {
                    call.end();
                });



            }

            @Override
            public void onEnded(@NonNull DirectCall directCall) {

                Toast.makeText(VideoCallActivity.this, "ENDED", Toast.LENGTH_SHORT).show();
                finish();

            }


            public void onRemoteVideoSettingsChanged(DirectCall call) {
                super.onRemoteVideoSettingsChanged(call);
                if (call.isRemoteVideoEnabled()) {
                    imageViewVideoOff.setVisibility(View.INVISIBLE);
                } else {
                    imageViewVideoOff.setVisibility(View.VISIBLE);
                }
            }

            public void onRemoteAudioSettingsChanged(DirectCall call) {
                super.onRemoteAudioSettingsChanged(call);
                if (call.isRemoteAudioEnabled()) {
                    imageViewMicOff.setVisibility(View.INVISIBLE);
                } else {
                    imageViewMicOff.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}