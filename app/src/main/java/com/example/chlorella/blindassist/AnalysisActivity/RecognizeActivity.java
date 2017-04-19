package com.example.chlorella.blindassist.AnalysisActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chlorella.blindassist.Classes.ActionClass;
import com.example.chlorella.blindassist.MainActivity;
import com.example.chlorella.blindassist.R;
import com.example.chlorella.blindassist.Helper.ClipboardHelper;
import com.example.chlorella.blindassist.Helper.ImageHelper;
import com.example.chlorella.blindassist.Helper.ShareHelper;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.chlorella.blindassist.Helper.ImageHelper.scaleBitmapDown;

public class RecognizeActivity extends Activity {

    @BindView(R.id.selectedImage)
    ImageView selectedImage;
    @BindView(R.id.editTextResult)
    TextView editText;

    // The image selected to detect(for display).
    private Bitmap rBitmap;
    // The image selected to detect.
    private Bitmap sBitmap;

    //Vision Service Client provided form MS
    private VisionServiceClient client;


    private CharSequence textResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);
        ButterKnife.bind(this);

        if (client == null) {
            client = new VisionServiceRestClient(getString(R.string.subscription_key));
        }

        rBitmap = ImageHelper.getImage();
        sBitmap = ImageHelper.getScaledImage();
        textResult = null;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if (rBitmap == null || sBitmap == null) {
            finish();
            return;
        } else {
            // Show the image on screen.
            rBitmap = scaleBitmapDown(rBitmap, metrics.heightPixels);
            selectedImage.setImageBitmap(rBitmap);

            // Add detection log.
            Log.d("AnalyzeActivity", "recognizing");

            doRecognize();
        }
    }

    public void doRecognize() {
        editText.setText("Analyzing...");

        try {
            new doRequest().execute();
        } catch (Exception e) {
            editText.setText("Error encountered. Exception is: " + e.toString());
        }
    }

    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        sBitmap.compress(Bitmap.CompressFormat.JPEG, ImageHelper.getScale(), output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        OCR ocr;
        ocr = this.client.recognizeText(inputStream, LanguageCodes.AutoDetect, true);

        //result = recoginize text
        String result = gson.toJson(ocr);
        Log.d("result", result);

        return result;
    }

    @OnClick(R.id.selectedImage)
    public void onViewClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.addition_array_r, new DialogInterface.OnClickListener() {
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
        }else if(i == ActionClass.COPYTOCLIPBOARD){
            if(textResult != null){
                ClipboardHelper.setClipboard(getApplicationContext(),textResult.toString());
                Toast toast = Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT);
                toast.show();
            }else{
                //Todo: String rHK
                Toast toast = Toast.makeText(getApplicationContext(), "please wait for the result", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else if(i == ActionClass.SHAREMESSAGE){
            ShareHelper.share(rBitmap,textResult.toString());
        }else if(i == ActionClass.SAVEIMAGE){
            Toast toast = Toast.makeText(getApplicationContext(),MainActivity.magicalCamera.savePhotoInMemoryDevice(rBitmap,"rHelper",MagicalCamera.JPEG,true),Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    //do request
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

            if (e != null) {
                editText.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                OCR r = gson.fromJson(data, OCR.class);

                String result = "";
                for (Region reg : r.regions) {
                    for (Line line : reg.lines) {
                        for (Word word : line.words) {
                            result += word.text + " ";
                        }
                        result += "\n";
                    }
                    result += "\n\n";
                }

                textResult = result;
                Toast toast = Toast.makeText(getApplicationContext(), textResult, Toast.LENGTH_SHORT);
                toast.show();

                editText.setText(result);
            }
        }
    }
}
