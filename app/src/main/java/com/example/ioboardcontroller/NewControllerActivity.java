package com.example.ioboardcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewControllerActivity extends AppCompatActivity
{
    private NewController newController;
    private Intent myIntent;
    private int mode, index;

    public class saveButtonClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View arg0)
        {
            // TODO Auto-generated method stub

            EditText name = (EditText) findViewById(R.id.editTextName);
            newController.setName(name.getText().toString());
            EditText hostname = (EditText) findViewById(R.id.editTextHostname);
            newController.setHostname(hostname.getText().toString());
            EditText username = (EditText) findViewById(R.id.editTextUsername);
            newController.setUsername(username.getText().toString());
            EditText password = (EditText) findViewById(R.id.editTextPassword);
            newController.setPassword(password.getText().toString());

            DBHelper controllerDB = new DBHelper(NewControllerActivity.this);
            if (mode == 0) // create
            {
                Integer id = controllerDB.saveNewController(newController);
                newController.setID(id);
            }
            else if (mode == 1) // edit
            {
                controllerDB.updateController(newController);
            }

            myIntent.putExtra("newControllerDefinition", newController);
            myIntent.putExtra("index", index);
            myIntent.putExtra("mode", mode);

            setResult(NewControllerActivity.RESULT_OK, myIntent);
            NewControllerActivity.this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_controller);

        myIntent = getIntent();
        newController = (NewController)myIntent.getSerializableExtra("newControllerDefinition");
        mode = myIntent.getIntExtra("mode", 0);
        index = myIntent.getIntExtra("index", 0);
        Button buttonSave = (Button) findViewById(R.id.buttonSave);
        saveButtonClickListener clsaveButton = new saveButtonClickListener();
        buttonSave.setOnClickListener(clsaveButton);

        if (mode==0) // create
        {
            EditText name = (EditText) findViewById(R.id.editTextName);
            name.setText("");
            EditText hostname = (EditText) findViewById(R.id.editTextHostname);
            hostname.setText("");
            EditText username = (EditText) findViewById(R.id.editTextUsername);
            username.setText("");
            EditText password = (EditText) findViewById(R.id.editTextPassword);
            password.setText("");
        }
        else if (mode==1) // edit
        {
            EditText name = (EditText) findViewById(R.id.editTextName);
            name.setText(newController.getName());
            EditText hostname = (EditText) findViewById(R.id.editTextHostname);
            hostname.setText(newController.getHostname());
            EditText username = (EditText) findViewById(R.id.editTextUsername);
            username.setText(newController.getUsername());
            EditText password = (EditText) findViewById(R.id.editTextPassword);
            password.setText(newController.getPassword());
        }
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        //Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_LONG).show();

        setResult(NewControllerActivity.RESULT_CANCELED, myIntent);
        NewControllerActivity.this.finish();
    }
}
