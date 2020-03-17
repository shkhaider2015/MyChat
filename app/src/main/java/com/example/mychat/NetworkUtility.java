package com.example.mychat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class NetworkUtility {
    private static final String TAG = "NetworkUtility";

    public static int getConnectionType(Context context)
    {
        int result = 0;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        {
            if (cm != null)
            {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null)
                {

                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    {
                        result = 2;
                        Log.d(TAG, "getConnectionType: WIFI");
                    }
                    else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    {
                        result = 1;
                        Log.d(TAG, "getConnectionType: MOBILE DATA");
                    }
                    else
                    {
                        result = 0;
                        Log.d(TAG, "getConnectionType: NOT MOBILE AND WIFI ");
                    }
                    
                }
                else
                {
                    result = 0;
                    Log.d(TAG, "getConnectionType: capabilities is null");
                }
            }
            else
            {
                result = 0;
                Log.d(TAG, "getConnectionType: CM IS NULL NO NETWORK");
            }

        }
        else
        {
            if (cm != null)
            {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null)
                {
                    //connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    {
                        result = 2;
                        Log.d(TAG, "getConnectionType: WIFI");
                    }
                    else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    {
                        result = 1;
                        Log.d(TAG, "getConnectionType: MOBILE DATA");
                    }
                    else 
                    {
                        result = 0;
                        Log.d(TAG, "getConnectionType: NOT MOBILE AND WIFI ");
                    }
                }
                else 
                {
                    result = 0;
                    Log.d(TAG, "getConnectionType: ActiveNetwork is null");
                }
            }
            else 
            {
                result = 0;
                Log.d(TAG, "getConnectionType: cm is null");
            }

        }


        return result;
    }

    public static boolean isInternetAvailable() {
        vv vv = new vv();
        boolean result = false;
        try {
            result = vv.execute().get();
            Log.d(TAG, "isInternetAvailable: result = " + result);

        }catch (ExecutionException e)
        {
            Log.e(TAG, "isInternetAvailable: ", e);
        }catch (InterruptedException e)
        {
            Log.e(TAG, "isInternetAvailable: ", e);
        }

        return result;
    }

    public static void strictModeOn()
    {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
    }

    static class vv extends AsyncTask<Boolean, Boolean, Boolean>
    {

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            try {
                InetAddress address = InetAddress.getByName("www.google.com");
                Log.d(TAG, "doInBackground: address : " + address);
                return !address.equals("");
            } catch (UnknownHostException e) {
                // Log error
                Log.e(TAG, "isInternetAvailable: ", e);
            }
            return false;
        }
    }


}
