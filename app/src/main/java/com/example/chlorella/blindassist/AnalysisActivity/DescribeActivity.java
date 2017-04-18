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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chlorella.blindassist.Classes.ActionClass;
import com.example.chlorella.blindassist.MainActivity;
import com.example.chlorella.blindassist.R;
import com.example.chlorella.blindassist.helper.ClipboardHelper;
import com.example.chlorella.blindassist.helper.ImageHelper;
import com.example.chlorella.blindassist.helper.ShareHelper;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_describe, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        builder.setItems(R.array.addition_array, new DialogInterface.OnClickListener() {
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
            if(textResult != null) {
                Intent share = ShareHelper.share(rBitmap, textResult.toString());
                startActivity(share);
            }else{
                //Todo: String rHK
                Toast toast = Toast.makeText(getApplicationContext(), "please wait for the result", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else if(i == ActionClass.SAVEIMAGE){
            Toast toast = Toast.makeText(getApplicationContext(), MainActivity.magicalCamera.savePhotoInMemoryDevice(rBitmap,"test", MagicalCamera.JPEG, true),Toast.LENGTH_SHORT);
            toast.show();
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
