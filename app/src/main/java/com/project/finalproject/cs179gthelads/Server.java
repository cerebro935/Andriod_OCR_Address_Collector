package com.project.finalproject.cs179gthelads;

import android.os.AsyncTask;
import android.util.Log;

import net.sourceforge.jgeocoder.AddressComponent;
import net.sourceforge.jgeocoder.us.AddressParser;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.lang.Integer;
import java.util.Map;

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
        String direction = "";
        String url = "";
        int postal = 0;

        /*start of the parsing of the return address information.
         this parsing does not yet take into account addresses that contain a P.O box, addresses
         that contain a suite number, or addresses whose city name is more than two words.
         */

        //remove some symbols from the submitted text
        details = details.replace("\n", ", ");
        Log.d("Details", details);

        //use Jgeocode API to parse the address
        Map<AddressComponent, String> parsedAddr = AddressParser.parseAddress(details);

        supporter = parsedAddr.get(AddressComponent.NAME);
        if(supporter == null){
            supporter = "N/A";
        }
        city = parsedAddr.get(AddressComponent.CITY);
        state = parsedAddr.get(AddressComponent.STATE);
        postal = Integer.parseInt(parsedAddr.get(AddressComponent.ZIP));

        //Check for street direction
        direction = parsedAddr.get(AddressComponent.PREDIR);
        if(direction != null){
            street = direction+" ";
        }

        street = street+parsedAddr.get(AddressComponent.NUMBER)+" "+
                parsedAddr.get(AddressComponent.STREET)+" "+
                parsedAddr.get(AddressComponent.TYPE);


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
