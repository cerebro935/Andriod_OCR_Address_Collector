package com.project.finalproject.cs179gthelads;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import androidx.annotation.NonNull;

public class FirebaseOCR {
    public FirebaseOCR(Context context){
        // Do nothing
    }
    public void runTextRecognition(Bitmap ocrImage){
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(ocrImage);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>(){
                            @Override
                            public void onSuccess(FirebaseVisionText texts){
                                processTextRecognitionResult(texts);
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
    private void processTextRecognitionResult(FirebaseVisionText texts){
        Log.d("Firebase OCR", texts.getText());
    }
}
