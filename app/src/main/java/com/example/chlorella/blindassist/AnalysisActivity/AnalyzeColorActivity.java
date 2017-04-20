package com.example.chlorella.blindassist.AnalysisActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import com.example.chlorella.blindassist.Helper.ColorHelper;
import com.example.chlorella.blindassist.Helper.ImageHelper;
import com.example.chlorella.blindassist.Helper.ShareHelper;
import com.example.chlorella.blindassist.MainActivity;
import com.example.chlorella.blindassist.R;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnalyzeColorActivity extends Activity {
    @BindView(R.id.selectedImage)
    ImageView selectedImage;
    @BindView(R.id.color_text)
    TextView colorText;

    // The image selected to detect.
    private Bitmap rBitmap;

    private VisionServiceClient client;
    private Bitmap sBitmap;
    private CharSequence textResult = null;


    ColorHelper col = new ColorHelper(getApplication());
    private Integer colorNameID = null;
    private TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
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
            Log.d("AnalyzeActivity", "Image: " + rBitmap.getWidth()
                    + "x" + rBitmap.getHeight());

            colorText.setText("processing");
            doAnalyze();
        }

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }

    public void doAnalyze() {
        colorText.setText("Analyzing...");

        try {
            new doRequest().execute();
        } catch (Exception e) {
            colorText.setText("Error encountered. Exception is: " + e.toString());
        }
    }

    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();
        String[] features = {"Color"};
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
        builder.setItems(R.array.addition_array_c, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                action(which);
            }
        });
        builder.create().show();
    }

    private void action(int i){
        if(i == ActionClass.REPEAT){
            if(textResult != null){
                Toast toast = Toast.makeText(getApplicationContext(), textResult, Toast.LENGTH_SHORT);
                toast.show();
            }else{
                //Todo: String rHK
                Toast toast = Toast.makeText(getApplicationContext(), "please wait for the result", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else if(i == ActionClass.COPYTOCLIPBOARD+1){
            if(textResult != null){
                ClipboardHelper.setClipboard(getApplicationContext(),textResult.toString());
                Toast toast = Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT);
                toast.show();
            }else{
                //Todo: String rHK
                Toast toast = Toast.makeText(getApplicationContext(), "please wait for the result", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else if(i == ActionClass.SHAREMESSAGE+1){
            if(textResult != null) {
                Intent shareIntent = ShareHelper.share( rBitmap, textResult.toString());
                startActivity(shareIntent);
            }else{
                //Todo: String rHK
                Toast toast = Toast.makeText(getApplicationContext(), "please wait for the result", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else if(i == ActionClass.SAVEIMAGE+1){
            Toast toast = Toast.makeText(getApplicationContext(),MainActivity.magicalCamera.savePhotoInMemoryDevice(rBitmap,"rHelper",MagicalCamera.JPEG,true),Toast.LENGTH_SHORT);
            toast.show();
        }else if(i == ActionClass.REPERTINEN){
//            Toast toast = Toast.makeText(getApplicationContext(),col.getEnglishString(getApplicationContext(),colorNameID),Toast.LENGTH_SHORT);
//            toast.show();
            t1.speak(col.getEnglishString(getApplicationContext(),colorNameID),TextToSpeech.QUEUE_FLUSH,null);
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

            colorText.setText("");
            if (e != null) {
                colorText.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);

                colorText.append(result.color.accentColor + "\n");
                colorText.append(getResources().getString(col.matchingColorName(Color.parseColor("#" + result.color.accentColor))));
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                colorNameID = col.matchingColorName(Color.parseColor("#" + result.color.accentColor));
                textResult = getResources().getString(col.matchingColorName(Color.parseColor("#" + result.color.accentColor)));
                Toast toast = Toast.makeText(context, textResult, duration);
                toast.show();
                colorText.append("\nDominant Color Foreground :" + result.color.dominantColorForeground + "\n");
                colorText.append("Dominant Color Background :" + result.color.dominantColorBackground + "\n");

//                mcolorText.append("\n--- Raw Data ---\n\n");
//                mcolorText.append(data);
//                mcolorText.setSelection(0);
            }
        }
    }

}
