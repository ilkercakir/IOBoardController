package com.example.ioboardcontroller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    static Context mainContext;
    ControllerRecyclerViewAdapter recyclerAdapter;

    public static Context getMainContext()
    {
        return(mainContext);
    }

    private class recyclerViewItemClickListener implements View.OnClickListener
    {
        ExtendedNewController enc;

        recyclerViewItemClickListener(ExtendedNewController enc)
        {
            this.enc = enc;
        }

        @Override
        public void onClick(View arg0)
        {
            // TODO Auto-generated method stub
            if (enc.isRunning())
            {
                enc.rLogin(); // async
            }
        }
    }

    // stores and recycles views as they are scrolled off screen
    private class rViewHolder extends RecyclerView.ViewHolder
    {
        TextView textviewname, textviewhost;
        ImageView imageviewstatus;

        public rViewHolder(View itemView)
        {
            super(itemView);
            textviewname = itemView.findViewById(R.id.textviewname);
            textviewhost = itemView.findViewById(R.id.textviewhost);
            imageviewstatus = itemView.findViewById(R.id.imageviewstatus);
        }
    }

    public class ExtendedNewController extends NewController
    {
        rViewHolder holder;

        public ExtendedNewController()
        {
            super();
        }

        public ExtendedNewController(Integer id, String name, String hostname, String username, String password, boolean isrunning)
        {
            super(id, name, hostname, username, password, isrunning);
        }

        public void setHolder(rViewHolder holder)
        {
            this.holder = holder;
        }

        @Override
        public void onStatusReady()
        {
            if (isRunning())
            {
                holder.imageviewstatus.setImageResource(R.drawable.online);
            }
            else
            {
                holder.imageviewstatus.setImageResource(R.drawable.offline);
            }
        }

        @Override
        public void onLoginReady()
        {
            if (isLoggedIn())
            {
                NewController c = new NewController(getID(), getName(), getHostname(), getUsername(), getPassword(), isRunning());
                Intent ncaIntent = new Intent(MainActivity.this, RemoteControlActivity.class);
                ncaIntent.putExtra("newControllerDefinition", c); // Optional parameters
                //MainActivity.this.startActivity(myIntent);
                MainActivity.this.startActivityForResult(ncaIntent, 1);
                //Toast.makeText(MainActivity.this, this.getName(), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(MainActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class ControllerRecyclerViewAdapter extends RecyclerView.Adapter<rViewHolder>
    {
        private List<ExtendedNewController> mData;
        private LayoutInflater mInflater;

        // data is passed into the constructor
        ControllerRecyclerViewAdapter(Context context, List<NewController> data)
        {
            mData = new ArrayList<ExtendedNewController>();
            for(int i=0;i<data.size();i++)
            {
                NewController nc = data.get(i);
                ExtendedNewController e = new ExtendedNewController(nc.getID(), nc.getName(), nc.getHostname(), nc.getUsername(), nc.getPassword(), false);
                mData.add(e);
            }
            this.mInflater = LayoutInflater.from(context);
        }

        // inflates the row layout from xml when needed
        @Override
        public rViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = mInflater.inflate(R.layout.recycler_row, parent, false);
            return new rViewHolder(view);
        }

        // binds the data to Views in each row
        @Override
        public void onBindViewHolder(rViewHolder holder, int position)
        {
            ExtendedNewController c = this.mData.get(position);
            holder.textviewname.setText(c.getName());
            holder.textviewhost.setText(c.getHostname());
            holder.imageviewstatus.setImageResource(R.drawable.unknown);
            recyclerViewItemClickListener listener = new recyclerViewItemClickListener(c);
            holder.itemView.setOnClickListener(listener);
            c.setHolder(holder);
            c.getControllerStatus(); // async
        }

        // total number of rows
        @Override
        public int getItemCount()
        {
            return mData.size();
        }
    }

    private void startNewControllerActivity(ExtendedNewController enc, int index, int mode)
    {
        NewController c = new NewController(enc.getID(), enc.getName(), enc.getHostname(), enc.getUsername(), enc.getPassword(), enc.isRunning());
        Intent ncaIntent = new Intent(MainActivity.this, NewControllerActivity.class);
        ncaIntent.putExtra("newControllerDefinition", c); // Optional parameters
        ncaIntent.putExtra("index", index);
        ncaIntent.putExtra("mode", mode);
        //MainActivity.this.startActivity(myIntent);
        MainActivity.this.startActivityForResult(ncaIntent, 0);
    }

    public class newButtonClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View arg0)
        {
            // TODO Auto-generated method stub

            ExtendedNewController extendedNewController = new ExtendedNewController();
            startNewControllerActivity(extendedNewController, 0, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Handle the logic for the requestCode, resultCode and data returned...
        try
        {
            super.onActivityResult(requestCode, resultCode, data);

            NewController newController = (NewController)data.getSerializableExtra("newControllerDefinition");
            ExtendedNewController extendedNewController = new ExtendedNewController(newController.getID(), newController.getName(), newController.getHostname(), newController.getUsername(), newController.getPassword(), newController.isRunning());
            switch (requestCode)
            {
                case 0: // New Controller, Edit Controller
                    int mode = data.getIntExtra("mode", 0);
                    int index = data.getIntExtra("index", 0);
                    switch (resultCode)
                    {
                        case RESULT_OK:
                            if (mode == 0)
                                recyclerAdapter.mData.add(extendedNewController);
                            else
                                recyclerAdapter.mData.set(index, extendedNewController);
                            recyclerAdapter.notifyDataSetChanged();
                            // Toast.makeText(MainActivity.this, extendedNewController.getName(), Toast.LENGTH_SHORT).show();
                            break;
                        case RESULT_CANCELED:
                        default:
                            if (mode == 1)
                                recyclerAdapter.notifyDataSetChanged();
                            break;
                    }

                    break;
                case 1: // Remote Control
                    break;
                default:
                    break;
            }

        }
        catch (Exception ex)
        {
            Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private class ItemTouchHelperSimpleCallback extends ItemTouchHelper.SimpleCallback
    {
        Paint p;
        DBHelper controllerDB;
        ControllerRecyclerViewAdapter rAdapter;

        public ItemTouchHelperSimpleCallback(int dragDirs, int swipeDirs, DBHelper cDB, ControllerRecyclerViewAdapter adapter)
        {
            super(dragDirs, swipeDirs);

            this.p = new Paint();
            this.controllerDB = cDB;
            this.rAdapter = adapter;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
        {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
        {
            int position = viewHolder.getAdapterPosition();
            ExtendedNewController c = this.rAdapter.mData.get(position);

            if (direction == ItemTouchHelper.LEFT)
            {
                this.controllerDB.deleteController(c);
                this.rAdapter.mData.remove(position);
                this.rAdapter.notifyDataSetChanged();
                //Toast.makeText(MainActivity.this, c.getName(), Toast.LENGTH_SHORT).show();
            }
            else
            {
                startNewControllerActivity(c, position, 1);
                //Toast.makeText(MainActivity.this, c.getHostname(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
        {
            Bitmap icon;
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
            {
                View itemView = viewHolder.itemView;
                float side = (float) itemView.getBottom() - (float) itemView.getTop();
                float margin = side / 4;

                if(dX > 0)
                {
                    p.setColor(Color.parseColor("#388E3C"));
                    RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                    c.drawRect(background,p);

                    float left = (float) itemView.getLeft() + margin;
                    float top = (float) itemView.getTop() + margin;
                    float right = (float) itemView.getLeft() + 3*margin;
                    float bottom = (float)itemView.getBottom() - margin;

                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.edit);
                    //icon = getBitmapFromVectorDrawable(MainActivity.this, R.drawable.ic_launcher_background);
                    RectF iconDest = new RectF(left, top, right, bottom);

                    c.drawBitmap(icon,null,iconDest,p);
                }
                else if (dX < 0)
                {
                    p.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background,p);

                    float left = (float) itemView.getRight() - 3*margin;
                    float top = (float) itemView.getTop() + margin;
                    float right = (float) itemView.getRight() - margin;
                    float bottom = (float)itemView.getBottom() - margin;

                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.trash);
                    //icon = getBitmapFromVectorDrawable(MainActivity.this, R.drawable.ic_launcher_background);
                    RectF iconDest = new RectF(left, top, right,bottom);

                    c.drawBitmap(icon,null,iconDest,p);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        newButtonClickListener clfab = new newButtonClickListener();
        fab.setOnClickListener(clfab);

        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL );
        CookieHandler.setDefault(cookieManager);

        DBHelper controllerDB = new DBHelper(this);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerAdapter = new ControllerRecyclerViewAdapter(this, controllerDB.getControllers());
        recyclerView.setAdapter(recyclerAdapter);
/*
        ItemTouchHelper.SimpleCallback scb = initSwipe(controllerDB);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(scb);
        itemTouchHelper.attachToRecyclerView(recyclerView);
*/
        ItemTouchHelperSimpleCallback ithsc = new ItemTouchHelperSimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, controllerDB, recyclerAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(ithsc);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        mainContext = this.getApplicationContext();
    }

/*
public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId)
{
    Drawable drawable = ContextCompat.getDrawable(context, drawableId);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
    {
        drawable = (DrawableCompat.wrap(drawable)).mutate();
    }

    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
            drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);

    return bitmap;
}

    private ItemTouchHelper.SimpleCallback initSwipe(DBHelper cDB)
    {
        final Paint p = new Paint();
        final DBHelper controllerDB = cDB;

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                int position = viewHolder.getAdapterPosition();
                NewController c = recyclerAdapter.mData.get(position);

                if (direction == ItemTouchHelper.LEFT)
                {
                    controllerDB.deleteController(c);
                    recyclerAdapter.mData.remove(position);
                    recyclerAdapter.notifyDataSetChanged();
                    //Toast.makeText(MainActivity.this, c.getName(), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    startNewControllerActivity(c, position, 1);
                    //Toast.makeText(MainActivity.this, c.getHostname(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
                {
                    View itemView = viewHolder.itemView;
                    float side = (float) itemView.getBottom() - (float) itemView.getTop();
                    float margin = side / 4;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);

                        float left = (float) itemView.getLeft() + margin;
                        float top = (float) itemView.getTop() + margin;
                        float right = (float) itemView.getLeft() + 3*margin;
                        float bottom = (float)itemView.getBottom() - margin;

                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.edit);
                        //icon = getBitmapFromVectorDrawable(MainActivity.this, R.drawable.ic_launcher_background);
                        RectF iconDest = new RectF(left, top, right, bottom);

                        c.drawBitmap(icon,null,iconDest,p);
                    } else if (dX < 0) {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);

                        float left = (float) itemView.getRight() - 3*margin;
                        float top = (float) itemView.getTop() + margin;
                        float right = (float) itemView.getRight() - margin;
                        float bottom = (float)itemView.getBottom() - margin;

                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.trash);
                        //icon = getBitmapFromVectorDrawable(MainActivity.this, R.drawable.ic_launcher_background);
                        RectF iconDest = new RectF(left, top, right,bottom);

                        c.drawBitmap(icon,null,iconDest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        return(simpleItemTouchCallback);
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
