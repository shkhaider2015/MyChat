package com.example.mychat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class PermissionsUtility {


    public static boolean useRunTimePermission()
    {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.M;
    }

    public static boolean checkReadWritePermission(Context mCTX)
    {
        int RES = ContextCompat.checkSelfPermission(mCTX.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int WES = ContextCompat.checkSelfPermission(mCTX.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return RES == PackageManager.PERMISSION_DENIED && WES == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkCameraPermission(Context mCTX)
    {
        int CAMERA_REQUEST = ContextCompat.checkSelfPermission(mCTX.getApplicationContext(), Manifest.permission.CAMERA);

        return CAMERA_REQUEST == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestRWPermission()
    {

    }


}
