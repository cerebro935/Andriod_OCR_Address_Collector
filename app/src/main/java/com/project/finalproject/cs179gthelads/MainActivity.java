package com.project.finalproject.cs179gthelads;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    protected String photopath;
    private Uri pfile;
    private TessOcr myTess;
    Dialog myDialog;
    protected String myString = "no result";



    //@SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myDialog = new Dialog(this);
        myTess = new TessOcr(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new ButtonClickHandler());
        // Create an instance of Camera
        Log.d("Hello", "HelLOO");
        mCamera = getCameraInstance();
        Log.d("Hello", "HelLOO");


        // Create our Preview view and set it as a the content
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

    }

    public void ShowPopup(View v){
        TextView confirmTxt;
        TextView close;
        Button  subBtn;
        EditText myInput;

        myDialog.setContentView(R.layout.popup);
        close = myDialog.findViewById(R.id.closetxt);
        confirmTxt = myDialog.findViewById((R.id.confirm));
        myInput = myDialog.findViewById(R.id.userInput);
        myInput.setText(myString);
        subBtn = myDialog.findViewById(R.id.submit);

        subBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                myString = myInput.getText().toString(); //myString <--- edited text
            }
        });

        close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                myDialog.dismiss();
            }
        });
        //myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public class ButtonClickHandler implements View.OnClickListener
    {
        public void onClick( View view ){
            mCamera.takePicture(null, null, mPicture);
        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            Camera.Parameters params = c.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            c.setParameters(params);

            Log.d("Here", "Camera Made");
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.d("Error:", e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }

    // Create file
    private File createImageFile(){
        File folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(!folder.exists()) {
            folder.mkdir();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image_file = null;
        try{
            image_file = File.createTempFile(imageFileName,".jpg",folder);
        }catch(IOException e){
            e.printStackTrace();
        }
        photopath = image_file.getAbsolutePath();
        return image_file;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback(){
        @Override
        public void onPictureTaken(byte[] data, Camera camera){
            File pictureFile = createImageFile();
            if(pictureFile == null){
                Log.d("Media:", "Error creating media");
                return;
            }
            Log.d("File", "Created");
            try{
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            }catch(FileNotFoundException e){
                Log.d("Error-", "File not found: " + e.getMessage());
            } catch(IOException e){
                Log.d("Error-", "Error Accessing File: " + e.getMessage());
            }
            // Seprate this into a function to reduce coding space
            BitmapFactory.Options options = new BitmapFactory.Options();
            pfile = Uri.fromFile(pictureFile);
            Bitmap bitmap = BitmapFactory.decodeFile(new File(pfile.getPath()).getAbsolutePath(), options);
            Log.d("Height", Integer.toString(options.outHeight));
            Log.d("Width", Integer.toString(options.outWidth));
            pictureFile.delete();
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            myTess.TessInit();
            myString = myTess.readImage(bitmap); //myString <-- extracted info

            //begin popup
            //ShowPopup(View v );

            mCamera.stopPreview();
            mCamera.startPreview();

        }

    };

    // Releasing Camera
    private void releaseCamera(){
        if(mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }



}