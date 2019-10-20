package com.project.cs180.cs179gthelads;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class MainActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login);

        progressDialog = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v){
               ServerLogin serverlogin = new ServerLogin();
               serverlogin.execute();
           }
        });
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
