package com.project.finalproject.cs179gthelads;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Popup {

    TextView confirmTxt;
    TextView close;
    Button subBtn;
    EditText myInput;
    Dialog myDialog;

    public void ShowPopup(String test){
        myDialog.setContentView(R.layout.popup);

        close = myDialog.findViewById(R.id.closetxt);
        confirmTxt = myDialog.findViewById((R.id.confirm));
        myInput = myDialog.findViewById(R.id.userInput);
        //myInput.setText(myString);
        subBtn = myDialog.findViewById(R.id.submit);
        myInput.setText(test);


        subBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //myString = myInput.getText().toString(); //myString <--- edited text
                Server server = new Server();
                server.execute((myInput.getText().toString()));
                myDialog.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }
}
