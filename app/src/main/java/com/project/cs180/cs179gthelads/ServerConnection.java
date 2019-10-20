package com.project.cs180.cs179gthelads;


import android.os.StrictMode;
import android.util.Log;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ServerConnection {

    public Connection PING(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.
                Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection ping = null;

        try{
            try {
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            } catch(Exception ex){
                Log.e("ERROR driver", ex.getMessage());
            }

            ping = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb",
                    "user","password");
        } catch(SQLException SQLe){
            Log.e("ERROR SQLException", SQLe.getMessage());
        }

        return ping;
    }
}
