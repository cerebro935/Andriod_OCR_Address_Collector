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
import java.lang.Integer;

public class Server extends AsyncTask<String, String, String>{

    protected void onPreExecute(){
    }

    protected String doInBackground(String... params){
        String details = params[0];
        String supporter = "";
        String street = "";
        String city = "";
        String state = "";
        int postal = 0;
        boolean supporterFlag = true;
        boolean streetFlag = false;
        int length = 0;

        details = details.replace("\n", " ");
        details = details.replace(",", " ");
        String[] splitDetails = details.split("\\s+");
        length = splitDetails.length;

        postal = Integer.parseInt(splitDetails[length - 1]);
        state = splitDetails[length - 2];
        city = splitDetails[length - 3];


        for(int i = 0; i < (length - 3); ++i){
            if((!splitDetails[i].matches(".*\\d.*")) && supporterFlag){
                if(supporter.isEmpty()){
                    supporter = supporter.concat(splitDetails[i]);
                }
                else{
                    supporter = supporter.concat(" ");
                    supporter = supporter.concat(splitDetails[i]);
                }
            }
            else if(supporterFlag){
                street = street.concat(splitDetails[i]);
                supporterFlag = false;
                streetFlag = true;
            }
            else if((!splitDetails[i].matches(".*\\d.*")) && streetFlag){
                street = street.concat(" ");
                street = street.concat(splitDetails[i]);

            }
            else if(streetFlag){
                streetFlag = false;
            }
        }

        Log.d("Supporter: ", supporter);
        Log.d("Street", street);
        Log.d("city", city);
        Log.d("State", state);
        Log.d("zip", String.valueOf(postal));
            /*String url = "http://172.119.206.111/connect.php";
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
            } */
            String url = "";
            String rand = "this random string";

            url = "http://172.119.206.111/testinsert.php?var="+rand;
            url = url.replaceAll("\\s+", "%20");



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

    }
}
