package com.project.finalproject.cs179gthelads;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class TessOcr {
    private TessBaseAPI mTess;
    private String DATA_PATH = Environment.getExternalStorageDirectory().toString()+"/Tess/";
    // Setting up Tess
    public TessOcr(Context context){
        mTess = new TessBaseAPI();
        File dir = new File(DATA_PATH + "/tessdata");
        if(!dir.exists())
            dir.mkdirs();
        if(!(new File(DATA_PATH + "/tessdata/eng.traineddata")).exists()){
            try{
                AssetManager AM = context.getAssets();
                InputStream in = AM.open("eng.traineddata");
                OutputStream out = new FileOutputStream(DATA_PATH+"/tessdata/eng.traineddata");
                byte[] buff = new byte[1024];
                int len;
                while(( len = in.read(buff)) > 0){
                    out.write(buff,0,len);
                }
                in.close();
                out.close();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ1234567890',.?;/ ");
    }
    // Initialize the Tess
    public void TessInit(){
        mTess.init(DATA_PATH, "eng");
    }
    // Read Image
    public String readImage(Bitmap bitmap){
        mTess.setImage(bitmap);
        String recognizedText = mTess.getUTF8Text();
        mTess.end();
        Log.d("Analyzed Text: ", recognizedText);
        return recognizedText;
    }
}
