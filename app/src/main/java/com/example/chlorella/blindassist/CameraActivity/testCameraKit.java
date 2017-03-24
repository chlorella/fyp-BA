package com.example.chlorella.blindassist.CameraActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.chlorella.blindassist.R;
import com.flurgle.camerakit.CameraView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chlorella on 23/3/2017.
 */

public class testCameraKit extends AppCompatActivity implements View.OnLayoutChangeListener {

    @BindView(R.id.camera)
    CameraView camera;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testcamerakit);
        ButterKnife.bind(this);

        camera.addOnLayoutChangeListener(this);
    }

    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        mCameraWidth = right - left;
        mCameraHeight = bottom - top;

        width.setText(String.valueOf(mCameraWidth));
        height.setText(String.valueOf(mCameraHeight));

        camera.removeOnLayoutChangeListener(this);
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
}
