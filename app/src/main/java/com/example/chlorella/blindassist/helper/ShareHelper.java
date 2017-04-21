package com.example.chlorella.blindassist.Helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.example.chlorella.blindassist.R;

/**
 * Created by chlorella on 18/4/2017.
 */

public class ShareHelper {
    //todo:test Share intent
    public static void share(Bitmap bmap, String text, Context current) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("Text/plain");
        share.putExtra(Intent.EXTRA_TEXT, text);
        String title = current.getResources().getString(R.string.choose_share);
        Intent chooser = Intent.createChooser(share, title);

        if (share.resolveActivity(current.getPackageManager()) != null) {
            current.startActivity(chooser);
        }
    }
}
