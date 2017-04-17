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
package com.example.chlorella.blindassist.helper;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Defined several functions to load, draw, save, resize, and rotate images.
 */
public class ImageHelper {
    private static int scale = 1200;
    private static WeakReference<Bitmap> image;
    private static WeakReference<Bitmap> sImage;

    public static void setImage(@Nullable Bitmap image) {
        Log.d("ImageHelper", "setImage");
        ImageHelper.image = image != null ? new WeakReference<>(image) : null;
        ImageHelper.sImage = image!= null ? new WeakReference<>(scaleBitmapDown(image,scale)) : null;
    }

    public static void setScale(int scale){
        ImageHelper.scale = scale;
        ImageHelper.sImage = image!= null ? new WeakReference<>(scaleBitmapDown(image.get(),scale)) : null;
    }

    @Nullable
    public static Bitmap getImage() {
        return image != null ? image.get() : null;
    }

    @Nullable
    public static Bitmap getScaledImage() {
        return sImage != null ? sImage.get() : null;
    }

    @Nullable
    public static Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
}
