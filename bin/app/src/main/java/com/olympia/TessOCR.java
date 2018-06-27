package com.olympia;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TessOCR {
    private TessBaseAPI mTess;

    public TessOCR(Context context, String language) {
        mTess = new TessBaseAPI();

//        File dst = new File(context.getFilesDir() + "/tessdata/");
//        File myDir = context.getExternalFilesDir(Environment.MEDIA_MOUNTED +  "/tesseract/");
        String datapath = Environment.getExternalStorageDirectory() + "/tesseract/";
//        boolean success = true;
//        if (!dst.exists()) {
//            success = dst.mkdirs();
//        }

//        if (success) {
//            InputStream ins = context.getResources().openRawResource(R.raw.eng_traineddata);
//            try {
//                OutputStream outs = new FileOutputStream(dst);
//                copyFile(ins, outs);
//                String datapath = context.getFilesDir() + "/tesseract/";
                mTess.init(datapath, language);
//                b = true;
//            }
//            catch(IOException e) {
//
//            }
//        }
    }

//    private void copyFile(InputStream in, OutputStream out) throws IOException {
//        byte[] buffer = new byte[1024];
//        int read;
//        while((read = in.read(buffer)) != -1){
//            out.write(buffer, 0, read);
//        }
//    }

    public String getOCRResult(Bitmap bitmap) {
        mTess.setImage(bitmap);
        return mTess.getUTF8Text();
    }

    public void onDestroy() {
        if (mTess != null) mTess.end();
    }
}