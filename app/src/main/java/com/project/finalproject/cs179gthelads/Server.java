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
        //text submitted from OCR
        String details = params[0];
        String supporter = "";
        String street = "";
        String city = "";
        String state = "";
        String url = "";
        int postal = 0;
        boolean supporterFlag = true;
        boolean streetFlag = false;
        int length = 0;

        /*start of the parsing of the return address information.
         this parsing does not yet take into account addresses that contain a P.O box, addresses
         that contain a suite number, or addresses whose city name is more than two words.
         */

        //remove some symbols from the submitted text
        details = details.replace("\n", " ");
        details = details.replace(",", " ");

        //split text by spaces
        String[] splitDetails = details.split("\\s+");
        length = splitDetails.length;

        //postal code is the last object of a return address
        postal = Integer.parseInt(splitDetails[length - 1]);
        //state is right before postal code. Some states are two words, but normal people use abbreviations anyways.
        state = splitDetails[length - 2];
        //this is only for cities that are one word.
        // Need to figure out a method for cities whose names consist of more than one word.
        city = splitDetails[length - 3];

        //this loop is for the supporters name and street address
        for(int i = 0; i < (length - 3); ++i){
            //until we find a string that contains digits, add to supporter
            if((!splitDetails[i].matches(".*\\d.*")) && supporterFlag){
                if(supporter.isEmpty()){
                    supporter = supporter.concat(splitDetails[i]);
                }
                else{
                    supporter = supporter.concat(" ");
                    supporter = supporter.concat(splitDetails[i]);
                }
            }
            else if(supporterFlag){//digits are found and are assumed to be the start of a street address.
                street = street.concat(splitDetails[i]);
                supporterFlag = false;
                streetFlag = true;
            }
            else if((!splitDetails[i].matches(".*\\d.*")) && streetFlag){
                //the rest of the objects are part of street address
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

        //url for inserting into the supporter table
        url = "http://172.119.206.111/insertSupporter.php?var="+supporter;
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
                //response from the insert is the newly created ID
                String idString = response.toString();

                //url for inserting address data
                url = "http://172.119.206.111/insertAddress.php?var="+street+"&var2="+city+"&var3="+state+"&var4="+postal+"&var5="+idString;
                url = url.replaceAll("\\s+", "%20");
                current = new DefaultHttpClient();
                destination = new HttpGet();
                destination.setURI(new URI(url));
                status = current.execute(destination);

                //url for inserting timestamp
                url = "http://172.119.206.111/insertLetterReceived.php?var="+idString;
                url = url.replaceAll("\\s+", "%20");
                current = new DefaultHttpClient();
                destination = new HttpGet();
                destination.setURI(new URI(url));
                status = current.execute(destination);


                Log.d("Response", response.toString());
                return response.toString();
            } catch(Exception e){
                Log.e("ERROR PHP-URL-Exception", e.getMessage());
                return "ERROR connecting to Apache Server";
            }
    }

    protected void onPostExecute(String message){

    }
}
