package com.project.finalproject.cs179gthelads;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

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

public class MainActivity extends AppCompatActivity{
    private Camera mCamera;
    private CameraPreview mPreview;
    protected String photopath;
    private Uri pfile;
    protected String myString = "no result";
    private Bitmap mybitmap;
    private Boolean go = false;
    protected Popup pop;


    //@SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OpenCVLoader.initDebug();
        pop = new Popup();
        pop.myDialog = new Dialog(this);

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
        mCamera = getCameraInstance();



        // Create our Preview view and set it as a the content
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        ImageView overlay =(ImageView)findViewById(R.id.croprec);
        ((ViewGroup)overlay.getParent()).removeView(overlay);
        preview.addView(overlay);


    }
    // Handles Capture button
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

    // Create file for image that is to be captured.
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
            // Seprate this into a function to reduce coding space?
            // Create bitmap.
            BitmapFactory.Options options = new BitmapFactory.Options();
            pfile = Uri.fromFile(pictureFile);
            Bitmap bitmap = BitmapFactory.decodeFile(new File(pfile.getPath()).getAbsolutePath(), options);
            // Delete unecessary file
            pictureFile.delete();
            // This needs to be corrected as some bitmaps may not have these properties. works on samsung s9+
            bitmap = Bitmap.createBitmap(bitmap, 1000,950,2280, 1260);
            bitmap = Bitmap.createScaledBitmap(bitmap, 960,720,false);
            // Create Mat for OPENCV
            Mat tmp = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
            Utils.bitmapToMat(bitmap, tmp);
            // Thresh hold for binarization
            Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2GRAY);
            // Processing more?
            Imgproc.threshold(tmp,tmp,110,255,Imgproc.THRESH_BINARY);
            Utils.matToBitmap(tmp, bitmap);
            mybitmap = bitmap;
            runTextRecognition(mybitmap);
        }

    };
    // Function for FirebaseVision
    public void runTextRecognition(Bitmap ocrImage){
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(ocrImage);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>(){
                            @Override
                            public void onSuccess(FirebaseVisionText texts){
                                myString = processTextRecognitionResult(texts);
                                //begin popup
                                pop.ShowPopup(myString);
                                mCamera.stopPreview();
                                mCamera.startPreview();
                            }

                        })
                .addOnFailureListener(
                        new OnFailureListener(){
                            @Override
                            public void onFailure(@NonNull Exception e){
                                e.printStackTrace();
                            }
                        });
    }
    private String processTextRecognitionResult(FirebaseVisionText texts){
        Log.d("Firebase OCR", texts.getText());
        return texts.getText();
    }

    // Releasing Camera
    private void releaseCamera(){
        if(mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }



}