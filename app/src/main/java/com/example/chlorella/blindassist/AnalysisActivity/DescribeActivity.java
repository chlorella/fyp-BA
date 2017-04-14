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

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chlorella.blindassist.R;
import com.example.chlorella.blindassist.helper.ImageHelper;
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

public class DescribeActivity extends ActionBarActivity {
    @BindView(R.id.selectedImage)
    ImageView selectedImage;
    @BindView(R.id.editTextResult)
    EditText editText;
    // The image selected to detect.
    private Bitmap mBitmap;
    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;


    private VisionServiceClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_describe);
        ButterKnife.bind(this);

        if (client == null) {
            client = new VisionServiceRestClient(getString(R.string.subscription_key));
        }

        mBitmap = ImageHelper.getImage();
        if (mBitmap == null) {
            finish();
            return;
        }else{
            // Show the image on screen.
            ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
            imageView.setImageBitmap(mBitmap);

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
        editText.setText("Describing...");

        try {
            new doRequest().execute();
        } catch (Exception e) {
            editText.setText("Error encountered. Exception is: " + e.toString());
        }
    }

    //process
    //different
    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        AnalysisResult v = this.client.describe(inputStream, 1);

        String result = gson.toJson(v);
        Log.d("result", result);

        return result;
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

            editText.setText("");
            if (e != null) {
                editText.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                //gson
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);

                editText.append("Image format: " + result.metadata.format + "\n");
                editText.append("Image width: " + result.metadata.width + ", height:" + result.metadata.height + "\n");
                editText.append("\n");
                CharSequence text = "";

                //result.description.captions = .description text
                for (Caption caption : result.description.captions) {
                    editText.append("Caption: " + caption.text + ", confidence: " + caption.confidence + "\n");
                    text = text + caption.text + "\n";
                }
                editText.append("\n");

                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                for (String tag : result.description.tags) {
                   editText.append("Tag: " + tag + "\n");
                }
                editText.append("\n");

//                editText.append("\n--- Raw Data ---\n\n");
//                editText.append(data);
//                editText.setSelection(0);
            }
        }
    }
}
