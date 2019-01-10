package com.example.jon.politiswap.DataUtils.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionAsyncTask extends AsyncTask<Void,Void,Boolean> {
    private InternetCheckListener listener;

    public ConnectionAsyncTask(InternetCheckListener listener) {
        this.listener = listener;
    }

    public interface InternetCheckListener {
        void onInternetConnect(boolean isConnected);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try{
            HttpURLConnection connection = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
            connection.setRequestProperty("User-Agent", "Test");
            connection.setRequestProperty("Connection", "close");
            connection.setConnectTimeout(2000); //choose your own timeframe
            connection.setReadTimeout(2000); //choose your own timeframe
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) { //Connection OK
                return true;
            } else {
                return  false;
            }
        }catch (Exception e){
            return  false; //connectivity exists, but no internet.
        }
    }

    @Override
    protected void onPostExecute(Boolean isConnected) {
        super.onPostExecute(isConnected);
        listener.onInternetConnect(isConnected);
    }

}
