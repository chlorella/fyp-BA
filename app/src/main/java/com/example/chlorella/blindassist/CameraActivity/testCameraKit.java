package com.example.chlorella.blindassist.CameraActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.example.chlorella.blindassist.R;
import com.example.chlorella.blindassist.helper.FocusMarkerLayout;
import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;


/**
 * Created by chlorella on 23/3/2017.
 */

public class testCameraKit extends AppCompatActivity implements View.OnLayoutChangeListener {
    @BindView(R.id.camera)
    CameraView camera;
    @BindView(R.id.focusMarker)
    FocusMarkerLayout focusMarker;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.toggleFlash)
    ToggleButton toggleFlash;
    @BindView(R.id.album)
    Button album;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testcamerakit);
        ButterKnife.bind(this);
    }

    @OnTouch(R.id.focusMarker)
    boolean onTouchCamera(View view, MotionEvent motionEvent) {
        focusMarker.focus(motionEvent.getX(), motionEvent.getY());
        return false;
    }

    @OnClick(R.id.capture)
    void capturePhoto() {
        camera.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);

                // Create a bitmap
                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                imageView.setImageBitmap(result);
            }
        });
        camera.captureImage();
    }

    @OnClick(R.id.toggleFlash)
    void toggleFlash() {
        if (toggleFlash.isChecked()) {
            camera.setFlash(CameraKit.Constants.FLASH_AUTO);
        } else {
            camera.setFlash(CameraKit.Constants.FLASH_OFF);
        }
    }

    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        camera.stop();
        super.onPause();
    }

    @Override
    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

    }

    @OnClick(R.id.album)
    public void onViewClicked() {
        Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
        getImage.addCategory(Intent.CATEGORY_OPENABLE);
        getImage.setType("image/jpeg");
        testCameraKit.this.startActivity(getImage);
    }
}
