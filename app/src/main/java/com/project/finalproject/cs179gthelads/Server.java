package com.project.finalproject.cs179gthelads;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class Server extends AsyncTask<String, String, String>{
    private static int task = 0;
    private Context context;

    public void decideButton(Context con, int flag){
        context = con;
        task = flag;
    }

    protected void onPreExecute(){
    }

    protected String doInBackground(String... params){
        if(task == 0){
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
        else if(task == 1){
            String url = "";
            String supporter = "";
            String street = "";
            String city = "";
            String state = "";
            int postal = 0;

            url = "http://172.119.206.111/testinsert.php";



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
        else{
            return "ERROR button press cannot be resolved";
        }
    }

    protected void onPostExecute(String message){
        Toast.makeText(context,""+message, Toast.LENGTH_LONG).show();
    }
}
