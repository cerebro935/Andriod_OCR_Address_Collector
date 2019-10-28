package com.project.cs180.cs179gthelads;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    protected Button _button;
    protected ImageView _image;
    protected TextView _field;
    protected String photopath;
    protected static final String PHOTO_TAKEN = "photo_taken";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri file;
    Bitmap help1;
    ThumbnailUtils thumbnail;

    //@SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressDialog = new ProgressDialog(this);

        _image = findViewById( R.id.image );
        _field = findViewById( R.id.field );
        _button = findViewById( R.id.button );
        _button.setOnClickListener( new ButtonClickHandler() );
        // May need to change the path later on
    }

    public class ButtonClickHandler implements View.OnClickListener
    {
        public void onClick( View view ){
            startCameraActivity();
        }
    }

    private void startCameraActivity() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Get URI from file
        file = Uri.fromFile(createImageFile());
        // Get URI File
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    //Marco ends******
    //Sunny starts****

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            if(resultCode == Activity.RESULT_OK){
                try{
                    help1 = MediaStore.Images.Media.getBitmap(getContentResolver(), file);
                    Log.i("Height:", Integer.toString(help1.getHeight()));
                    Log.i("Width:", Integer.toString(help1.getWidth()));
                    _image.setImageBitmap(thumbnail.extractThumbnail(help1,help1.getWidth(),help1.getHeight()));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    // Fix this code
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


     private class ServerLogin extends AsyncTask<String,String,String>{

        protected void onPreExecute(){
            super.onPreExecute();
        }

        protected String doInBackground(String... params){
            String url = "http://172.119.206.111/connect.php";
            try{
                HttpClient current = new DefaultHttpClient();
                HttpGet destination = new HttpGet();
                destination.setURI(new URI(url));
                HttpResponse status = current.execute(destination);

                BufferedReader input = new BufferedReader(new InputStreamReader(status.getEntity().getContent()));
                StringBuffer response = new StringBuffer("");
                String line="";

                while((line = input.readLine()) != null) {
                    response.append(line);
                    break;
                }

                input.close();
                return response.toString();
            } catch(Exception e){
                Log.e("ERROR PHP-URL-Exception", e.getMessage());
                return "ERROR connecting to Apache Server";
            }
        }

        protected void onPostExecute(String message){
            Toast.makeText(getBaseContext(),""+message, Toast.LENGTH_LONG).show();
        }
    }

}
