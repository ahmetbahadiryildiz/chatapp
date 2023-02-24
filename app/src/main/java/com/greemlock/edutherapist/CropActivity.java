package com.greemlock.edutherapist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.canhub.cropper.CropImageView;

public class CropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Uri profilePhoto = getIntent().getData();
        CropImageView cropImageView = findViewById(R.id.cropImageView);
        cropImageView.setImageUriAsync(profilePhoto);
        cropImageView.setAspectRatio(1,1);
        getSupportActionBar().hide();

        Button b_cropImage = findViewById(R.id.b_cropImage);
        b_cropImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    Bitmap croppedImage = cropImageView.getCroppedImage();
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedImage, 256, 256, false);
                    Intent intent = new Intent();
                    intent.putExtra("data",scaledBitmap);
                    setResult(RESULT_OK, intent);
                    finish();

                }catch (Exception ex){
                    Log.e("exception",ex.getLocalizedMessage());
                }
            }
        });
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();


        float bitmapRatio = (float)width / (float) height;

        height = maxSize;
        width = (int) (height * bitmapRatio);

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}