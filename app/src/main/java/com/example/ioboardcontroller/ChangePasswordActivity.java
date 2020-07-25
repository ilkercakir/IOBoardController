package com.example.ioboardcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity
{
    private NewController newController;
    private Intent myIntent;

    private class ExtendedNewController extends NewController
    {
        DBHelper controllerDB;

        public ExtendedNewController()
        {
            super();
        }

        public ExtendedNewController(Integer id, String name, String hostname, String username, String password, boolean isrunning)
        {
            super(id, name, hostname, username, password, isrunning);
        }

        @Override
        public void onPasswordSet()
        {
            // local DB
            controllerDB = new DBHelper(ChangePasswordActivity.this);
            controllerDB.setPassword(newController);

            myIntent.putExtra("newControllerDefinition", newController);
            setResult(ChangePasswordActivity.RESULT_OK, myIntent);
            ChangePasswordActivity.this.finish();
        }
    }

    public class changePasswordClickListener implements View.OnClickListener
    {
        ExtendedNewController enc;

        @Override
        public void onClick(View arg0)
        {
            EditText editTextPassword1 = findViewById(R.id.editTextPassword1);
            EditText editTextPassword2 = findViewById(R.id.editTextPassword2);

            if (editTextPassword1.getText().toString().length()<8)
            {
                Toast.makeText(getApplicationContext(), "Password shorter than 8 characters", Toast.LENGTH_LONG).show();
                return;
            }
            if (!editTextPassword1.getText().toString().equals(editTextPassword2.getText().toString()))
            {
                Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
                return;
            }

            // local object
            newController.setPassword(editTextPassword1.getText().toString());

            // DB on server
            ExtendedNewController enc = new ExtendedNewController(newController.getID(), newController.getName(), newController.getHostname(), newController.getUsername(), newController.getPassword(), false);
            enc.setNewPassword();
            //Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        myIntent = getIntent();
        newController = (NewController)myIntent.getSerializableExtra("newControllerDefinition");

        changePasswordClickListener cl = new changePasswordClickListener();
        Button button = findViewById(R.id.button_changepassword);
        button.setOnClickListener(cl);
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        //Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_LONG).show();

        setResult(ChangePasswordActivity.RESULT_CANCELED, myIntent);
        ChangePasswordActivity.this.finish();
    }
}
