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
import com.frosquivel.magicalcamera.Functionallities.PermissionGranted;
import com.frosquivel.magicalcamera.MagicalCamera;

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

    private PermissionGranted permissionGranted = new PermissionGranted(this);
    private MagicalCamera magicalCamera;
    Bitmap result;

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_SELECT_IMAGE_IN_ALBUM = 1;
    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 80;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testcamerakit);
        ButterKnife.bind(this);

        permissionGranted.checkAllMagicalCameraPermission();
        magicalCamera = new MagicalCamera(this, RESIZE_PHOTO_PIXELS_PERCENTAGE, permissionGranted);
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
                result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
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
    public void selectImageInAlbum(View view) {
        magicalCamera.selectedPicture("my_header_name");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //this is for rotate picture in this method
        //magicalCamera.resultPhoto(requestCode, resultCode, data, MagicalCamera.ORIENTATION_ROTATE_180);

        //you should to call the method ever, for obtain the bitmap photo (= magicalCamera.getPhoto())
        magicalCamera.resultPhoto(requestCode, resultCode, data);

        if (magicalCamera.getPhoto() != null) {
            //another form to rotate image
            magicalCamera.setPhoto(magicalCamera.rotatePicture(magicalCamera.getPhoto(), MagicalCamera.ORIENTATION_ROTATE_NORMAL));

            //set the photo in image view
            imageView.setImageBitmap(magicalCamera.getPhoto());
        }
    }
}
