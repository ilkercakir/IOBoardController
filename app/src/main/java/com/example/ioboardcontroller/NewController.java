package com.example.ioboardcontroller;

import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewController implements Serializable
{
    Integer id ;
    String name, hostname, username, password;
    boolean running, loggedIn;

    public NewController()
    {
        this.id = 0;
        this.name = "";
        this.hostname = "";
        this.username = "";
        this.password = "";
        this.running = false;
        this.loggedIn = false;
    }

    public NewController(Integer id, String name, String hostname, String username, String password, boolean isrunning)
    {
        this.id = id;
        this.name = name;
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.running = isrunning;
        this.loggedIn = false;
    }

    public Integer getID()
    {
        return (this.id);
    }
    public void setID(Integer id)
    {
        this.id = id;
    }
    public String getName()
    {
        return (this.name);
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getHostname()
    {
        return (this.hostname);
    }
    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }
    public String getUsername()
    {
        return (this.username);
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getPassword()
    {
        return (this.password);
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public boolean isRunning()
    {
        return (this.running);
    }
    public boolean isLoggedIn()
    {
        return (this.loggedIn);
    }

    private class NameValuePair
    {
        private String paramName;
        private String paramValue;

        public NameValuePair(String name, String value)
        {
            paramName = name;
            paramValue = value;
        }

        public String getPair()
        {
            return (paramName + "=" + paramValue);
        }
    };

    private String makeURL(String service, String parameters)
    {
        String url = "http://" + this.hostname + ":8080/IOBoardV0/ControllerConsole?" + service;
        if (!parameters.equals(""))
        {
            url += parameters;
        }
        return(url);
    }

    public void onStatusReady()
    {

    }

    public void onLoginReady()
    {

    }

    public void onPasswordSet()
    {

    }

    private class RequestTask_getControllerStatus extends AsyncTask<String, String, String>
    {
        String responseString = "", stacktraceString = "";
        boolean success = true;

        private String getStackTrace(final Throwable throwable)
        {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw, true);
            throwable.printStackTrace(pw);
            return sw.getBuffer().toString();
        }

        @Override
        protected String doInBackground(String... uri)
        {
            URL url = null;
            try {
                url = new URL(uri[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);

                // Response
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader inputStream = new BufferedReader(isr);

                StringBuffer sb = new StringBuffer();
                String str;
                while ((str = inputStream.readLine()) != null) {
                    sb.append(str);
                }
                inputStream.close();
                isr.close();
                is.close();

                responseString = sb.toString();

                urlConnection.disconnect();
            }
            catch (IOException e)
            {
                //e.printStackTrace();
                stacktraceString = getStackTrace(e);
                success = false;
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            //Do anything with response..

            running = false;
            // Parse
            try
            {
                JSONObject json = new JSONObject(result);
                String controllerStatus = json.getString("status");
                if (controllerStatus.equals("running"))
                {
                    running = true;
                }
                else {
                }
            }
            catch (JSONException j)
            {
                stacktraceString = getStackTrace(j);
                success = false;
            }

            onStatusReady();
        }
    }

    public void getControllerStatus()
    {
        String url =  makeURL("status", "");

        RequestTask_getControllerStatus asyncTask = new RequestTask_getControllerStatus();
        asyncTask.execute(url);
    }

    private class RequestTask_RLogin extends AsyncTask<String, String, String>
    {
        String responseString = "", stacktraceString = "";
        boolean success = true;

        private String getStackTrace(final Throwable throwable)
        {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw, true);
            throwable.printStackTrace(pw);
            return sw.getBuffer().toString();
        }

        @Override
        protected String doInBackground(String... uri)
        {
            URL url = null;
            try
            {
                url = new URL(uri[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                // Post request
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new NameValuePair("username", uri[1]));
                params.add(new NameValuePair("password", uri[2]));
                String urlEncoded = "";
                for(int i=0;i<params.size();i++)
                    urlEncoded += "&" + params.get(i).getPair();
                urlEncoded = urlEncoded.replaceFirst("&", "");
                byte[] postData = urlEncoded.getBytes();
                int postDataLength = postData.length;

                urlConnection.setRequestMethod( "POST" );
                urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty( "charset", "utf-8");
                urlConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));

                OutputStream os = urlConnection.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.write(postData);
                dos.flush();
                dos.close();
                os.close();

                // Response
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                InputStreamReader isr = new InputStreamReader(is,"UTF-8");
                BufferedReader inputStream = new BufferedReader(isr);

                StringBuffer sb = new StringBuffer();
                String str;
                while((str = inputStream.readLine())!= null)
                {
                    sb.append(str);
                }
                inputStream.close();
                isr.close();
                is.close();

                responseString = sb.toString();

                urlConnection.disconnect();
            }
            catch (IOException e)
            {
                //e.printStackTrace();
                stacktraceString = getStackTrace(e);
                success = false;
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            //Do anything with response..

            try
            {
                loggedIn = false;
                // Parse
                JSONObject json = new JSONObject(result);
                Integer i = json.getInt("login");
                if (i==1)
                {
                    loggedIn = true;
                }
                else{
                }
            }
            catch(JSONException j)
            {
                String s = getStackTrace(j);
                success = false;
            }

            onLoginReady();
        }
    }

    public void rLogin()
    {
        String url =  makeURL("rlogin", "");

        RequestTask_RLogin asyncTask = new RequestTask_RLogin();
        asyncTask.execute(url, this.username, this.password);
    }

    private class RequestTask_SetPassword extends AsyncTask<String, String, String>
    {
        String responseString = "", stacktraceString = "";
        boolean success = true;

        private String getStackTrace(final Throwable throwable)
        {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw, true);
            throwable.printStackTrace(pw);
            return sw.getBuffer().toString();
        }

        @Override
        protected String doInBackground(String... uri)
        {
            URL url = null;
            try
            {
                url = new URL(uri[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                // Post request
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new NameValuePair("password", uri[1]));
                String urlEncoded = "";
                for(int i=0;i<params.size();i++)
                    urlEncoded += "&" + params.get(i).getPair();
                urlEncoded = urlEncoded.replaceFirst("&", "");
                byte[] postData = urlEncoded.getBytes();
                int postDataLength = postData.length;

                urlConnection.setRequestMethod( "POST" );
                urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty( "charset", "utf-8");
                urlConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));

                OutputStream os = urlConnection.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.write(postData);
                dos.flush();
                dos.close();
                os.close();

                // Response
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                InputStreamReader isr = new InputStreamReader(is,"UTF-8");
                BufferedReader inputStream = new BufferedReader(isr);

                StringBuffer sb = new StringBuffer();
                String str;
                while((str = inputStream.readLine())!= null)
                {
                    sb.append(str);
                }
                inputStream.close();
                isr.close();
                is.close();

                responseString = sb.toString();

                urlConnection.disconnect();
            }
            catch (IOException e)
            {
                //e.printStackTrace();
                stacktraceString = getStackTrace(e);
                success = false;
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            //Do anything with response..

            try
            {
                // Parse
                JSONObject json = new JSONObject(result);
                Integer i = json.getInt("setpassword");
                if (i==1)
                {
                    onPasswordSet();
                }
                else{
                }
            }
            catch(JSONException j)
            {
                String s = getStackTrace(j);
                success = false;
            }
        }
    }

    public void setNewPassword()
    {
        String url =  makeURL("setpassword", "");

        RequestTask_SetPassword asyncTask = new RequestTask_SetPassword();
        asyncTask.execute(url, this.password);
    }

}
