package com.example.ioboardcontroller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class RemoteControlActivity extends AppCompatActivity
{
    private NewController newController;
    private Intent myIntent;
    private RecyclerView recyclerView;
    private DeviceRecyclerViewAdapter recyclerAdapter;

    private class myCheckedChangedListener implements CompoundButton.OnCheckedChangeListener
    {
        NewDevice d;
        View view;

        public void setDevice(NewDevice d, View view)
        {
            this.d = d;
            this.view = view;
        }

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1)
        {
            int value = (arg1?1:0); // ischecked
            d.setValue(value);
            d.setDeviceValue(view);
            //Toast.makeText(getApplicationContext(), Integer.toString(d.getDevid()), Toast.LENGTH_LONG).show();
        }
    }

    public class myItemSelectedListener implements Spinner.OnItemSelectedListener
    {
        NewDevice d;
        View view;

        public void setDevice(NewDevice d, View view)
        {
            this.d = d;
            this.view = view;
        }

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id)
        {
            //String sel = arg0.getItemAtPosition(pos).toString();
            int value = pos;
            d.setValue(value);
            d.setDeviceValue(view);
            //Toast.makeText(getApplicationContext(), new Integer(value).toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
            // TODO Auto-generated method stub
        }
    }

    public class myClickListener implements View.OnClickListener
    {
        NewDevice d;
        View view;

        public void setDevice(NewDevice d, View view)
        {
            this.d = d;
            this.view = view;
        }

        @Override
        public void onClick(View arg0)
        {
            int value = d.getInitval();
            d.setValue(value);
            d.setDeviceValue(view);
            //Toast.makeText(getApplicationContext(), new Integer(value).toString(), Toast.LENGTH_LONG).show();
        }
    }

    private class recyclerViewItemClickListener implements View.OnClickListener
    {
        NewDevice d;
        View view;

        recyclerViewItemClickListener(NewDevice d, View view)
        {
            this.d = d;
            this.view = view;
        }

        @Override
        public void onClick(View arg0)
        {
            ExtendedNewDevice xnd = new ExtendedNewDevice(d);
            xnd.getDeviceValue(view); // async
            //Toast.makeText(getApplicationContext(), d.getDtext(), Toast.LENGTH_SHORT).show();
        }
    }

    // stores and recycles views as they are scrolled off screen
    private class rViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewDtext, textViewCatxt;
        ImageView imageViewDevice;
        LinearLayout layoutControl;

        public rViewHolder(View itemView)
        {
            super(itemView);
            textViewDtext = itemView.findViewById(R.id.textViewDtext);
            textViewCatxt = itemView.findViewById(R.id.textViewCatxt);
            imageViewDevice = itemView.findViewById(R.id.imageViewDevice);
            layoutControl = itemView.findViewById(R.id.layoutControl);
        }
    }

    private class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<rViewHolder>
    {
        private List<NewDevice> mData;
        private LayoutInflater mInflater;

        // data is passed into the constructor
        DeviceRecyclerViewAdapter(Context context, List<NewDevice> data)
        {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public rViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            //rViewHolder holder;

            View view = mInflater.inflate(R.layout.device_row, parent, false);
            return(new rViewHolder(view));
        }

        // binds the data to Views in each row
        @Override
        public void onBindViewHolder(rViewHolder holder, int position)
        {
            recyclerViewItemClickListener listener;
            Switch onOffSwitch;
            Spinner spinner;
            Button button;

            NewDevice d = this.mData.get(position);
            ExtendedNewDevice xnd = new ExtendedNewDevice(d);

            holder.textViewDtext.setText(d.getDtext());
            String catxt = d.getCatxt() + ", " + d.getDttext();
            holder.textViewCatxt.setText(catxt);
            d.loadImage(holder.imageViewDevice, d.getDicon());

            int childPos = holder.layoutControl.getChildCount();
            while(childPos>0)
            {
                childPos--;
                View view = holder.layoutControl.getChildAt(childPos);
                holder.layoutControl.removeView(view);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            switch (d.getDtype()) {
                case 1:
                case 5:
                    onOffSwitch = new Switch(RemoteControlActivity.this);
                    //onOffSwitch.setId(); //onOffSwitch.generateViewId();
                    onOffSwitch.setTag("device" + d.getDevid());
                    onOffSwitch.setText("");
                    onOffSwitch.setTextOn("ON");
                    onOffSwitch.setTextOff("OFF");
                    onOffSwitch.setEnabled(d.getCateg().equals("A") && d.getAuthorizationLevel().equals("W"));
                    onOffSwitch.setLayoutParams(params);

                    myCheckedChangedListener ccl = new myCheckedChangedListener();
                    ccl.setDevice(d, onOffSwitch);
                    onOffSwitch.setOnCheckedChangeListener(ccl);

                    holder.layoutControl.addView(onOffSwitch);
                    xnd.getDeviceValue((View)onOffSwitch); // async

                    listener = new recyclerViewItemClickListener(d, (View)onOffSwitch);
                    holder.itemView.setOnClickListener(listener);
                    break;
                case 2:
                case 6:
                    spinner = new Spinner(RemoteControlActivity.this);
                    //spinner.setId(); //spinner.generateViewId();
                    spinner.setTag("device" + d.getDevid());
                    String[] items = new String[d.getNumstates()];
                    for(int j=0;j<d.getNumstates();j++)
                        items[j] = Integer.toString(j);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RemoteControlActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                    spinner.setAdapter(adapter);
                    spinner.setEnabled(d.getCateg().equals("A") && d.getAuthorizationLevel().equals("W"));
                    spinner.setLayoutParams(params);

                    myItemSelectedListener isl = new myItemSelectedListener();
                    isl.setDevice(d, (View)spinner);
                    spinner.setOnItemSelectedListener(isl);

                    holder.layoutControl.addView(spinner);
                    xnd.getDeviceValue((View)spinner); // async

                    listener = new recyclerViewItemClickListener(d, (View)spinner);
                    holder.itemView.setOnClickListener(listener);
                    break;
                case 3:
                    button = new Button(RemoteControlActivity.this);
                    //button.setId(); //button.generateViewId();
                    button.setTag("device" + d.getDevid());
                    button.setText("Press");
                    button.setEnabled(d.getAuthorizationLevel().equals("W"));
                    button.setLayoutParams(params);

                    myClickListener cl = new myClickListener();
                    cl.setDevice(d, (View)button);
                    button.setOnClickListener(cl);

                    holder.layoutControl.addView(button);
                    xnd.getDeviceValue((View)button); // async

                    listener = new recyclerViewItemClickListener(d, (View)button);
                    holder.itemView.setOnClickListener(listener);
                    break;
            }
        }

        // total number of rows
        @Override
        public int getItemCount()
        {
            return mData.size();
        }
    }

    private class ExtendedDeviceHelper extends DeviceHelper
    {
        public ExtendedDeviceHelper(String hostName)
        {
            super(hostName);
        }

        @Override
        void onDevicesReady(List<NewDevice> devices)
        {
            recyclerView = findViewById(R.id.recyclerViewDevices);
            recyclerView.setLayoutManager(new GridLayoutManager(RemoteControlActivity.this, 1));
            recyclerAdapter = new DeviceRecyclerViewAdapter(RemoteControlActivity.this, devices);
            recyclerView.setAdapter(recyclerAdapter);
            //Toast.makeText(getApplicationContext(), new Integer(devices.size()).toString(), Toast.LENGTH_LONG).show();
        }
    }

    private class ExtendedNewDevice extends NewDevice
    {
        ExtendedNewDevice(NewDevice d)
        {
            super(d.getHostname(), d.getService(), d.getDevid(), d.getChnnl(), d.getDtype(), d.getNumstates(), d.getInitval(), d.getAuthorizationLevel(), d.getDtext(), d.getDttext(), d.getCateg(), d.getCatxt(), d.getDicon(), d.getDticon(), d.getValue());
        }

        @Override
        void onDeviceValueReady(NewDevice dev, View view)
        {
            if (view instanceof Switch)
            {
                ((Switch)view).setChecked(dev.getValue()==1);
            }
            else if (view instanceof Spinner)
            {
                ((Spinner)view).setSelection(dev.getValue(), true);
            }
            else if (view instanceof Button)
            {
            }
        }
    }

    public class ImgButtonClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View arg0)
        {
            for(int i=0;i<recyclerAdapter.getItemCount();i++) {
                NewDevice d = recyclerAdapter.mData.get(i);
                ExtendedNewDevice xnd = new ExtendedNewDevice(d);
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
                if (holder!=null) {
                    View view = holder.itemView.findViewWithTag("device" + d.getDevid());
                    xnd.getDeviceValue(view);
                }
                //Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startChangePasswordActivity(NewController c)
    {
        Intent ncaIntent = new Intent(RemoteControlActivity.this, ChangePasswordActivity.class);
        ncaIntent.putExtra("newControllerDefinition", c); // Optional parameters
        //RemoteControlActivity.this.startActivity(myIntent);
        RemoteControlActivity.this.startActivityForResult(ncaIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Handle the logic for the requestCode, resultCode and data returned...
        try
        {
            super.onActivityResult(requestCode, resultCode, data);

            NewController newController = (NewController)data.getSerializableExtra("newControllerDefinition");
            switch (requestCode)
            {
                case 0: // Change Password
                    switch (resultCode)
                    {
                        case RESULT_OK:
                        case RESULT_CANCELED:
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(RemoteControlActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);

        myIntent = getIntent();
        newController = (NewController)myIntent.getSerializableExtra("newControllerDefinition");

        ExtendedDeviceHelper edh = new ExtendedDeviceHelper(newController.getHostname());
        edh.getDevices(); // async

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.refresh_devices);

        ImageButton imagebutton = findViewById(R.id.imageButton);
        ImgButtonClickListener ibcl = new ImgButtonClickListener();
        imagebutton.setOnClickListener(ibcl);
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        //Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_LONG).show();

        setResult(NewControllerActivity.RESULT_CANCELED, myIntent);
        RemoteControlActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_remote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.change_password)
        {
            startChangePasswordActivity(newController);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
