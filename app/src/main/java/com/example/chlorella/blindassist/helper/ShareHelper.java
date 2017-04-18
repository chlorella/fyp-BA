package com.example.chlorella.blindassist.helper;

import android.content.Intent;
import android.graphics.Bitmap;

/**
 * Created by chlorella on 18/4/2017.
 */

public class ShareHelper {
    //todo:test Share intent
    public static Intent share(Bitmap bmap, String text) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("Text/plain");
//        share.setType("image/jpeg");
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        File f = new File(Environment.getExternalStorageDirectory()
//                + File.separator + "temporary_file.jpg");
//        try {
//            f.createNewFile();
//            FileOutputStream fo = new FileOutputStream(f);
//            fo.write(bytes.toByteArray());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        share.putExtra(Intent.EXTRA_TEXT, text);
//        share.putExtra(Intent.EXTRA_STREAM,
//                Uri.parse("file:///sdcard/temporary_file.jpg"));
        return share;
    }
}
