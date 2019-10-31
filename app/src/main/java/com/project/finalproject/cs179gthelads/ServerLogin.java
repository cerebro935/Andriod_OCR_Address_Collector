package com.project.finalproject.cs179gthelads;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class ServerLogin{

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
}
