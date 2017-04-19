package com.example.chlorella.blindassist.Helper;

import android.content.Context;

/**
 * Created by chlorella on 18/4/2017.
 */

public class ClipboardHelper{
        public static void setClipboard(Context context, String text) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
}
