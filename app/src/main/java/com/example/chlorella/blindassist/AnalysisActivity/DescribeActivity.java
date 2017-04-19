//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Vision-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
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
import com.google.android.gms.vision.face.Landmark;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
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

public class DescribeActivity extends Activity {
    @BindView(R.id.selectedImage)
    ImageView selectedImage;
    @BindView(R.id.textView)
    TextView textView;

    // The image selected to detect.
    private Bitmap rBitmap;
    private Bitmap sBitmap;

    private VisionServiceClient client;

    private CharSequence textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textResult = null;
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
            Log.d("DescribeActivity", "get Bitmap");

            doDescribe();
        }
    }

    public void doDescribe() {
        //todo: toast
        textView.setText("Describing...");

        try {
            new doRequest().execute();
        } catch (Exception e) {
            //todo: toast
            textView.setText("Error encountered. Exception is: " + e.toString());
        }
    }

    //process
    //different
    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        sBitmap.compress(Bitmap.CompressFormat.JPEG, ImageHelper.getScale(), output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        AnalysisResult v = this.client.describe(inputStream, 1);

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
                //Todo: String rHK
                Toast toast = Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT);
                toast.show();
            }else{
                //Todo: String rHK
                Toast toast = Toast.makeText(getApplicationContext(), "please wait for the result", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else if(i == ActionClass.SHAREMESSAGE){
            if(textResult != null) {
                Intent share = ShareHelper.share(rBitmap, textResult.toString());
                startActivity(share);
            }else{
                //Todo: String rHK
                Toast toast = Toast.makeText(getApplicationContext(), "please wait for the result", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else if(i == ActionClass.SAVEIMAGE){
            //Todo: Dialog change name
            Toast toast = Toast.makeText(getApplicationContext(), magicalCamera.savePhotoInMemoryDevice(rBitmap,"test","rHelper",MagicalCamera.JPEG,true),Toast.LENGTH_SHORT);
            toast.show();
        }else if(i == ActionClass.FACERANGONIZATION){
            //Todo: Dialog change name
            FaceDetector();
        }else if(i == ActionClass.TRANSLATION){
            //Todo: Dialog change name
            if( Locale.getDefault().toString() != "en" && textResult != null) {
                try {
                    Intent intent = TranslateHelper.callGoogleTranslateApps(textResult.toString(), Locale.getDefault().toString());
                    startActivity(intent);
                }catch (ActivityNotFoundException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplication(), "Sorry, No Google Translation Installed",
                            Toast.LENGTH_SHORT).show();
                }
            }else if(textResult != null){
                Toast toast = Toast.makeText(getApplicationContext(), "Translation is for English to Chinese", Toast.LENGTH_SHORT);
                toast.show();
            }else if(textResult == null){
                Toast toast = Toast.makeText(getApplicationContext(), "please wait for the result", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void FaceDetector() {
        if (magicalCamera != null) {
            magicalCamera.setPhoto(rBitmap);
            if (magicalCamera.getPhoto() != null) {
                //this comment line is the strok 5 and color red for default
                //imageView.setImageBitmap(magicalCamera.faceDetector());
                //you can the posibility of send the square color and the respective stroke
                selectedImage.setImageBitmap(magicalCamera.faceDetector(10, Color.BLUE));
                //todo: landmark
                List<Landmark> listMark = magicalCamera.getFaceRecognitionInformation().listLandMarkPhoto;

                int count = 0;
                if(listMark != null) {
                    for (Landmark landmark : listMark) {
                        count++;
                    }
                }
                if(count != 0){
                    CharSequence text = "There are " + count + " face";
                    Toast toast = Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"There are no human face",Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                //Todo: String
                Toast.makeText(DescribeActivity.this,
                        "Your image is null, please select or take one",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            //Todo: String
            Toast.makeText(DescribeActivity.this,
                    "Please initialized magical camera, maybe in static context for use in all activity",
                    Toast.LENGTH_SHORT).show();
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

            textView.setText("");
            if (e != null) {
                textView.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                //gson
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);

                textView.append("Image format: " + result.metadata.format + "\n");
                textView.append("Image width: " + result.metadata.width + ", height:" + result.metadata.height + "\n");
                textView.append("\n");
                textResult = "";

                //result.description.captions = .description text
                for (Caption caption : result.description.captions) {
                    textView.append("Caption: " + caption.text + ", confidence: " + caption.confidence + "\n");
                    textResult = textResult + caption.text + "\n";
                }
                textView.append("\n");

                //Context
                Toast toast = Toast.makeText(getApplicationContext(), textResult, Toast.LENGTH_SHORT);
                toast.show();

                for (String tag : result.description.tags) {
                    textView.append("Tag: " + tag + "\n");
                }
                textView.append("\n");

//                textView.append("\n--- Raw Data ---\n\n");
//                textView.append(data);
//                textView.setSelection(0);
            }
        }
    }
}
