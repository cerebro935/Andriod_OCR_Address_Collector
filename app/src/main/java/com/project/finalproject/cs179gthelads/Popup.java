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

    /*
    This function is in charge of the popup which displays the extracted text for
    the user to edit, submit, or cancel submission.

    subBtn.setOnClickListener adds functionality to submit button. Creates instance
    of server, passing in the string to execute function to be submitted into
    database.

    close.setOnClickListener adds functionality to x button that dismisses the
    dialogue and allows user to retake picture.

    */
    public void ShowPopup(){
        myDialog.setContentView(R.layout.popup);
        close = myDialog.findViewById(R.id.closetxt);
        confirmTxt = myDialog.findViewById((R.id.confirm));
        myInput = myDialog.findViewById(R.id.userInput);
        subBtn = myDialog.findViewById(R.id.submit);


        subBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
