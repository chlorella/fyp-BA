package com.example.chlorella.blindassist;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chlorella.blindassist.AnalysisActivity.AnalyzeColorActivity;
import com.example.chlorella.blindassist.AnalysisActivity.DescribeActivity;
import com.example.chlorella.blindassist.AnalysisActivity.RecognizeActivity;
import com.example.chlorella.blindassist.helper.ImageHelper;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.MagicalPermissions;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by chlorella on 23/3/2017.
 */

public class MainActivity extends Activity {
    @BindView(R.id.camera)
    CameraView camera;
    @BindView(R.id.preview)
    ImageView imageView;
    @BindView(R.id.album)
    Button album;
    @BindView(R.id.function)
    TextView fText;
    @BindView(R.id.capture)
    Button capture;

    private MagicalPermissions magicalPermissions;
    public MagicalCamera magicalCamera;
    private int function = 0;
    private String[] fArray;

    private int scale = 50;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        magicalPermissions = new MagicalPermissions(this, permissions);
        magicalCamera = new MagicalCamera(this, scale, magicalPermissions);

        fArray = getResources().getStringArray(R.array.function_array);
        fText.setText(fArray[function]);
    }

    public void setScale(int i) {
        if (i == 0) {
            scale = 50;
        } else if (i == 1) {
            scale = 75;
        } else if (i == 2) {
            scale = 100;
        }
        ImageHelper.setScale(scale);
        magicalCamera.setResizePhoto(scale);
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        String[] st = getResources().getStringArray(R.array.scale_array);
        CharSequence text = st[i];

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void setFunction(int f) {
        function = f;
        fText.setText(fArray[function]);
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence text = fArray[function];

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.function) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.menu_function);
            builder.setItems(R.array.function_array, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // The 'which' argument contains the index position
                    // of the selected item
                    setFunction(which);
                }
            });
            builder.create().show();
        } else if (id == R.id.scale) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.menu_scale);
            builder.setItems(R.array.scale_array, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // The 'which' argument contains the index position
                    // of the selected item
                    setScale(which);
                }
            });
            builder.create().show();
        } else if (id == R.id.language) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.menu_language);
            builder.setItems(R.array.language_array, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // The 'which' argument contains the index position
                    // of the selected item
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    CharSequence text = Locale.getDefault().toString();

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
            builder.create().show();
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
                ImageHelper.setImage(bitmap);
                switchIntent();
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

    public void switchIntent() {
        if(ImageHelper.getImage()!=null) {
            if (function == 0) {
                Intent intent = new Intent(MainActivity.this, DescribeActivity.class);
                startActivity(intent);
            } else if (function == 1) {
                Intent intent = new Intent(MainActivity.this, AnalyzeColorActivity.class);
                startActivity(intent);
            } else if (function == 2) {
                Intent intent = new Intent(MainActivity.this, RecognizeActivity.class);
                startActivity(intent);
            }
        }else{
            Toast.makeText(this,"Please choose a Image",Toast.LENGTH_SHORT);
        }
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
            ImageHelper.setImage(bitmap);
            switchIntent();
        }
    }

    @OnClick(R.id.preview)
    public void onViewClicked() {
        switchIntent();
    }
}
