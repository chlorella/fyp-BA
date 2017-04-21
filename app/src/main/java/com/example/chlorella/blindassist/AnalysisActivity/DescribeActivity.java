package com.example.chlorella.blindassist.AnalysisActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chlorella.blindassist.Classes.ActionClass;
import com.example.chlorella.blindassist.Helper.ClipboardHelper;
import com.example.chlorella.blindassist.Helper.ImageHelper;
import com.example.chlorella.blindassist.Helper.ShareHelper;
import com.example.chlorella.blindassist.Helper.TranslateHelper;
import com.example.chlorella.blindassist.R;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.contract.Face;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.chlorella.blindassist.MainActivity.magicalCamera;
import static com.example.chlorella.blindassist.R.string.analyzing;
import static com.example.chlorella.blindassist.R.string.no_g_tran;
import static com.example.chlorella.blindassist.R.string.saved;


public class DescribeActivity extends Activity {
    @BindView(R.id.selectedImage)
    ImageView selectedImage;
    @BindView(R.id.textView)
    TextView textview;

    // The image selected to detect.
    private Bitmap rBitmap;
    private Bitmap sBitmap;

    private VisionServiceClient client;
    private CharSequence textResult = null;

    TextToSpeech t1;

    List<Face> faces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_describe);
        ButterKnife.bind(this);

        if (client == null) {
            client = new VisionServiceRestClient(getString(R.string.subscription_key));
        }

        rBitmap = ImageHelper.getImage();
        sBitmap = ImageHelper.getScaledImage();
        if (rBitmap == null || sBitmap == null) {
            finish();
            return;
        } else {
            // Show the image on screen.
            selectedImage.setImageBitmap(rBitmap);

            // Add detection log.
            Log.d("DescribeActivity", "Image: " + rBitmap.getWidth()
                    + "x" + rBitmap.getHeight());

            doAnalyze();
        }

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }

    public void doAnalyze() {
        Toast.makeText(DescribeActivity.this, analyzing, Toast.LENGTH_SHORT).show();

        try {
            new doRequest().execute();
        } catch (Exception e) {
            textview.setText("Error encountered. Exception is: " + e.toString());
        }
    }

    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();
        String[] features = {"Description", "Faces"};
        String[] details = {};

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        sBitmap.compress(Bitmap.CompressFormat.JPEG, ImageHelper.getScale(), output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        AnalysisResult v = this.client.analyzeImage(inputStream, features, details);


        String result = gson.toJson(v);
        Log.d("result", result);

        return result;
    }


    @OnClick(R.id.selectedImage)
    public void onViewClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.addition_array_d, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                action(which);
            }
        });
        builder.create().show();
    }

    private void action(int i) {
        if (i == ActionClass.REPEAT) {
            if (textResult != null) {
                if (!Locale.getDefault().toString().contentEquals("en")) {
                    t1.speak(textResult.toString(), TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    Toast.makeText(getApplicationContext(), textResult, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.wait, Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (i == ActionClass.COPYTOCLIPBOARD) {
            if (textResult != null) {
                ClipboardHelper.setClipboard(getApplicationContext(), textResult.toString());
                Toast toast = Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.wait, Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (i == ActionClass.SHAREMESSAGE) {
            if (textResult != null) {
                ShareHelper.share(rBitmap, textResult.toString(), DescribeActivity.this);

            } else {

                Toast toast = Toast.makeText(getApplicationContext(), R.string.wait, Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (i == ActionClass.SAVEIMAGE) {
            magicalCamera.savePhotoInMemoryDevice(rBitmap, "photo", "rHelper", MagicalCamera.JPEG, true);
            Toast toast = Toast.makeText(getApplicationContext(), saved, Toast.LENGTH_SHORT);
            toast.show();
        } else if (i == ActionClass.FACERANGONIZATION) {
            FaceDetector();
        } else if (i == ActionClass.TRANSLATION) {
            if (Locale.getDefault().toString().contentEquals("en") && textResult != null) {
                try {
                    Intent intent = TranslateHelper.callGoogleTranslateApps(textResult.toString(), Locale.getDefault().toString());
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplication(), no_g_tran,
                            Toast.LENGTH_SHORT).show();
                }
            } else if (textResult == null) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.wait, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void FaceDetector() {
        int faceCount = 0;

        for (Face face : faces) {
            faceCount++;
        }
        magicalCamera.setPhoto(rBitmap);
        selectedImage.setImageBitmap(magicalCamera.faceDetector(5, Color.BLUE));
        if (faceCount != 0) {
            CharSequence text = getResources().getQuantityString(R.plurals.number_of_face, faces.size(),faces.size());

            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();
            int fc2 = 0;
            for (Face face : faces) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.face) + ++fc2 + getResources().getString(R.string.f_gender) + genderCS((CharSequence) face.gender.toString()) + getResources().getString(R.string.f_age) + face.age, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.no_face, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private CharSequence genderCS(CharSequence cs) {
        if (cs == "Male") {
            return getResources().getString(R.string.s_m);
        } else {
            return getResources().getString(R.string.s_f);
        }
    }


    private class doRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence

            textview.setText("");
            if (e != null) {
                textview.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);
                faces = result.faces;

                for (Caption c : result.description.captions) {
                    textResult = c.text;
                    textview.append(c.text);
                }
                if (!Locale.getDefault().toString().contentEquals("en")){
                    t1.speak(textResult.toString(), TextToSpeech.QUEUE_FLUSH, null);
                }else{
                    Toast.makeText(getApplicationContext(), textResult, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
