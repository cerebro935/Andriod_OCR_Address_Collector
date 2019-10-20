package com.project.cs180.cs179gthelads;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    ServerConnection Connection;
    ProgressDialog progressDialog;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login);

        Connection = new ServerConnection();
        progressDialog = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v){
               ServerLogin serverlogin = new ServerLogin();
               serverlogin.execute();
           }
        });
    }

    private class ServerLogin extends AsyncTask<String,String,String>{
        String message = "";

        protected void onPreExecute(){
            progressDialog.setMessage("Loading");
            progressDialog.show();
            super.onPreExecute();
        }

        protected String doInBackground(String... params){

            Connection ping = Connection.PING();
            if(ping == null){
                message = "Failed to contact server";
            }
            else{
                message = "Server contacted successfully";
                try {
                    ping.close();
                } catch(SQLException SQLe){
                    Log.e("ERROR", SQLe.getMessage());
                }
            }

            return message;
        }

        protected void onPostExecute(String message){
            Toast.makeText(getBaseContext(),""+message, Toast.LENGTH_LONG).show();
            progressDialog.hide();
        }
    }
}
