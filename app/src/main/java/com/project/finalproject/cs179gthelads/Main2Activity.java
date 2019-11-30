package com.project.finalproject.cs179gthelads;

import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main2Activity extends AppCompatActivity {
    protected Button _submitButton;
    protected Button _connectButton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        progressDialog = new ProgressDialog(this);
        _submitButton = findViewById( R.id.button2 );
        _connectButton = findViewById(R.id.button4);

        _connectButton.setOnClickListener(new connectClick() );
        _submitButton.setOnClickListener( new submitClick() );
    }

    public class connectClick implements View.OnClickListener{
        public void onClick( View view ){
            startConnectActivity();
        }
    }

    public class submitClick implements View.OnClickListener{
        public void onClick( View view ){
            startSubmitActivity();
        }
    }

    private void startConnectActivity(){
        Server server = new Server();
        server.decideButton(this,0);
        server.execute();
    }

    private void startSubmitActivity(){
        Server server = new Server();
        server.decideButton(this,1);
        server.execute();

    }
}
