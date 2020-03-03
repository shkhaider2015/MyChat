package com.example.mychat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

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
}
