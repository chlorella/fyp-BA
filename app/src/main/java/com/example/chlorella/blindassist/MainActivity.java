package com.example.chlorella.blindassist;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.chlorella.blindassist.AnalysisActivity.RecognizeActivity;
import com.example.chlorella.blindassist.Setting.SettingsActivity;
import com.example.chlorella.blindassist.helper.ImageHelper;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.MagicalPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by chlorella on 23/3/2017.
 */

public class MainActivity extends AppCompatActivity implements View.OnLayoutChangeListener {
    @BindView(R.id.camera)
    CameraView camera;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.album)
    Button album;

    private MagicalPermissions magicalPermissions;
    private MagicalCamera magicalCamera;

    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 50;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testcamerakit);
        ButterKnife.bind(this);

        String[] permissions = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        magicalPermissions = new MagicalPermissions(this, permissions);
        magicalCamera = new MagicalCamera(this,RESIZE_PHOTO_PIXELS_PERCENTAGE, magicalPermissions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recognize, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent nextActivity = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(nextActivity);
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.capture)
    void capturePhoto() {
        camera.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                // Create a bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);

                imageView.setImageBitmap(bitmap);
                switchIntent(bitmap);
            }
        });
        camera.captureImage();
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

    public void switchIntent(Bitmap bmap){
        ImageHelper.setImage(bmap);
        Intent intent = new Intent(MainActivity.this, RecognizeActivity.class);
        startActivity(intent);
    }



    @OnClick(R.id.album)
    public void selectImageInAlbum(View view) {
        magicalCamera.selectedPicture("img");
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
            Bitmap bitmap = magicalCamera.getPhoto();
            switchIntent(bitmap);
        }
    }
}
