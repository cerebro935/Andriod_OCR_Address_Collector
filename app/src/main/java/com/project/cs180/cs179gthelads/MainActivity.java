package com.project.cs180.cs179gthelads;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
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
import java.net.URI;


public class MainActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    Button login;
    protected Button _button;
    protected ImageView _image;
    protected TextView _field;
    protected String _path;
    protected boolean _taken;
    protected static final String PHOTO_TAKEN = "photo_taken";

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

        _path = Environment.getExternalStorageDirectory() + "/images/make_machine_example.jpg";
        // May need to change the path later on

    }

    public class ButtonClickHandler implements View.OnClickListener
    {
        public void onClick( View view ){
            startCameraActivity();
        }
    }

    protected void startCameraActivity()
    {
        File file = new File( _path );
        Uri outputFileUri = Uri.fromFile( file );

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
        intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );

        startActivityForResult( intent, 0 );
    }

    //Marco ends******
    //Sunny starts****

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i( "MakeMachine", "resultCode: " + resultCode );
        switch( resultCode )
        {
            case 0:
                Log.i( "MakeMachine", "User cancelled" );
                break;

            case -1:
                onPhotoTaken();
                break;
        }
    }


    protected void onPhotoTaken()
    {
        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile( _path, options );
        _image.setImageBitmap(bitmap);

        _field.setVisibility( View.GONE );
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        outState.putBoolean( MainActivity.PHOTO_TAKEN, _taken );
    }

    @Override
    protected void onRestoreInstanceState( Bundle savedInstanceState)
    {
        Log.i( "MakeMachine", "onRestoreInstanceState()");
        if( savedInstanceState.getBoolean( MainActivity.PHOTO_TAKEN ) ) {
            onPhotoTaken();
        }
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
