package com.example.chlorella.blindassist.CameraActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.chlorella.blindassist.R;
import com.example.chlorella.blindassist.helper.FocusMarkerLayout;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;


/**
 * Created by chlorella on 23/3/2017.
 */

public class testCameraKit extends AppCompatActivity {
    @BindView(R.id.camera)
    CameraView camera;
    @BindView(R.id.focusMarker)
    FocusMarkerLayout focusMarker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testcamerakit);
        ButterKnife.bind(this);
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

    @OnClick(R.id.kit_capture)
    void capturePhoto(View view) {
        Log.d("Capture", "in");
        camera.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);
                Log.d("Capture", "onPictureTaken");
                // Create a bitmap
                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            }
        });
        camera.captureImage();

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence text = "touch";

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @OnTouch(R.id.focusMarker)
    boolean onTouchCamera(View view, MotionEvent motionEvent) {
        focusMarker.focus(motionEvent.getX(), motionEvent.getY());
        return false;
    }
}
