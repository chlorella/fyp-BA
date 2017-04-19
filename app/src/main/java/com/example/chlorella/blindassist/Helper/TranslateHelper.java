package com.example.chlorella.blindassist.Helper;

import android.content.ComponentName;
import android.content.Intent;

/**
 * Created by chlorella on 19/4/2017.
 */

public class TranslateHelper {
    public static Intent callGoogleTranslateApps(String word, String toLang) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, word);
        i.putExtra("key_text_input", word);
        i.putExtra("key_text_output", "");
        i.putExtra("key_language_from", "en");
        i.putExtra("key_language_to", toLang);
        i.putExtra("key_suggest_translation", "");
        i.putExtra("key_from_floating_window", true);
        i.setComponent(new ComponentName("com.google.android.apps.translate", "com.google.android.apps.translate.TranslateActivity"));
        return i;
    }
}
