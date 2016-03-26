package com.reactnative.horsepush;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;

import java.util.List;

/**
 * Created by techbin on 2016/3/22 0022.
 */
public class HorsePushModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext mReactApplicationContext;
    private static String HORSEPUSH = "HorsePush";

    public HorsePushModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactApplicationContext = reactContext;
    }

    @Override
    public String getName() {
        return HORSEPUSH;
    }

    //得到用户app包名列表
    @ReactMethod
    public static void getUserAppPackageNameArray(Callback callback) {

        if (mReactApplicationContext == null)
            return;
        PackageManager packageManager = mReactApplicationContext.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        WritableArray map = Arguments.createArray();
        for (int i = 0; i < packageInfos.size(); i++) {
            PackageInfo packageInfo = packageInfos.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //非系统应用
                map.pushString(packageInfo.packageName);
            } else {
                //系统应用　　　　　　　　
            }
        }
        callback.invoke(map);
    }


    //小型存储用的 set
    @ReactMethod
    public static void setExtraData(String value) {
        if (mReactApplicationContext == null)
            return;
        setSharedPreferences(mReactApplicationContext, HORSEPUSH +"extradata", value);
    }

    @ReactMethod
    public static void getExtraData(Callback callback) {
        if (mReactApplicationContext == null)
            return;
        callback.invoke(getSharedPreferences(mReactApplicationContext, HORSEPUSH + "extradata"));
    }


    public static String getExtraData(Context context) {
        return getSharedPreferences(context, HORSEPUSH + "extradata");
    }

    public static String getSharedPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }

    public static void setSharedPreferences(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


}
