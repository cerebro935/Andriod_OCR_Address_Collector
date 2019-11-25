package com.project.finalproject.cs179gthelads;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    protected String photopath;
    private Uri pfile;
    private TessOcr myTess;
    Dialog myDialog;
    protected String myString = "no result";
    private Bitmap mybitmap;



    //@SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OpenCVLoader.initDebug();
        myDialog = new Dialog(this);
        myTess = new TessOcr(this);
        super.onCreate(savedInstanceState);
        try{
            this.getSupportActionBar().hide();
        }catch(NullPointerException e){
            Log.d("Error: ", e.getMessage());
        }
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
        ImageView overlay =(ImageView)findViewById(R.id.croprec);
        ((ViewGroup)overlay.getParent()).removeView(overlay);
        preview.addView(overlay);


    }

    public void ShowPopup(){
        TextView confirmTxt;
        TextView close;
        Button  subBtn;
        EditText myInput;
        ImageView myImage;
        myDialog.setContentView(R.layout.popup);
        close = myDialog.findViewById(R.id.closetxt);
        confirmTxt = myDialog.findViewById((R.id.confirm));
        //myInput = myDialog.findViewById(R.id.userInput);
        myImage = myDialog.findViewById(R.id.Image);
        myImage.setImageBitmap(mybitmap);
        subBtn = myDialog.findViewById(R.id.submit);


        subBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //myString = myInput.getText().toString(); //myString <--- edited text
                myDialog.dismiss();
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
            bitmap = Bitmap.createBitmap(bitmap, 1000,950,2280, 1260);
            bitmap = Bitmap.createScaledBitmap(bitmap, 960,720,false);
            Mat tmp = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
            Utils.bitmapToMat(bitmap, tmp);
            Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2GRAY);
            // Processing more?
            Imgproc.threshold(tmp,tmp,90,255,Imgproc.THRESH_BINARY);
            Utils.matToBitmap(tmp, bitmap);
            mybitmap = bitmap;

            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            myTess.TessInit();
            myString = myTess.readImage(bitmap); //myString <-- extracted info
            Log.d("String", myString);
            //begin popup
            ShowPopup();
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