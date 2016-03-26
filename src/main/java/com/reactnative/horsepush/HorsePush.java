package com.reactnative.horsepush;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Created by techbin on 2016/3/18 0018.
 */
public class HorsePush {
    private static String HORSE_PUSH_WORK_PATH = "/mnt/sdcard/";//js文件夹路径,这个路径运行的时候会改变
    private static String HORSE_PUSH_JS_FILE_NAME = "";//js名字
    private static String HORSE_PUSH_ASSET_JS_NAME = "horse.push.js";//asset js名字
    private static String HORSE_PUSH_JS1_FILE_NAME = "horse.push.0.js";//js名字
    private static String HORSE_PUSH_JS2_FILE_NAME = "horse.push.1.js";//js名字

    private static final int DOWN_JAVA = 0;//下载apk标记
    private static final int DOWN_JAVA_PATCH = 1;//下载apk补丁标记
    private static final int DOWN_JS = 2;//下载js标记
    private static final int DOWN_JS_PATCH = 3;//下载js补丁标记
    private static Activity mActivity = null;
    private Context mContext;//上下文
    private String updateServer;//更新服务器
    private String channel;//渠道号
    private int appVersionCode = 0;//我的app版本
    private String jsFileMd5 = "";//我的appmd5


    //服务器返回的数据
    private int javaVersionCode = 0;  //得到最新的java版本
    private String javaVersionInfo = ""; //最新java版本提示信息
    private String javaPatchDownlink = "";  //最新java版本补丁包下载地址
    private String javaDownlink = "";   //最新java版本下载地址
    private String javaDownlinkMd5 = "";  //最新java版本下载md5
    private boolean javaForceUpdate = false; //最新js版本md5

    private String jsVersionInfo = ""; //最新js版本提示信息
    private String jsPatchDownlink = "";  //最新js补丁下载地址
    private String jsDownlink = ""; //最新js下载地址
    private String jsDownlinkMd5 = ""; //最新js版本md5
    private boolean jsForceUpdate = false; //最新js版本md5


    //构造
    public HorsePush() {
        super();
    }

    // 这个方法必须先执行到
    public HorsePush(Context context, String updateServer, String channel) {
        super();
        this.mContext = context;//上下文
        this.updateServer = updateServer;//更新服务器
        this.channel = channel;//渠道
        this.HORSE_PUSH_WORK_PATH = getHorsePushWorkPatch(context) + "/";//程序运行工作目录
        checkJS();
        //TODO 这里需要加入多次打开报错的优化
    }

    //检查js
    private void checkJS() {
        String jsRecord = HorsePushModule.getSharedPreferences(mContext, "js_name");//设置js默认值
        String jsDelFileName = jsRecord.equals(HORSE_PUSH_JS1_FILE_NAME) ? HORSE_PUSH_JS2_FILE_NAME : HORSE_PUSH_JS1_FILE_NAME;
        HORSE_PUSH_JS_FILE_NAME = jsRecord.equals(HORSE_PUSH_JS1_FILE_NAME) ? HORSE_PUSH_JS1_FILE_NAME : HORSE_PUSH_JS2_FILE_NAME;
        File tFileDel = new File(HORSE_PUSH_WORK_PATH + jsDelFileName);
        if (tFileDel.exists())
            tFileDel.delete();


        try {
            File f = new File(HORSE_PUSH_WORK_PATH + HORSE_PUSH_JS_FILE_NAME);
            if (!f.exists()) {
                copyAssetFileToFiles(HORSE_PUSH_ASSET_JS_NAME, HORSE_PUSH_WORK_PATH + HORSE_PUSH_JS_FILE_NAME + ".t");
                File t = new File(HORSE_PUSH_WORK_PATH + HORSE_PUSH_JS_FILE_NAME + ".t");
                t.renameTo(f);//安全复制
            }
        } catch (Exception e) {
        }
        checkUpdate();//更新
    }

    //得到bundle目录
    public static String getJSBundleFile() {

        return HORSE_PUSH_WORK_PATH + HORSE_PUSH_JS_FILE_NAME;
    }

    public static String getJSBundleFile(Activity activity) {
        mActivity = activity;

        return getJSBundleFile();
    }

    //得到工作目录
    public String getHorsePushWorkPatch(Context context) {
        try {
            File f = new File(context.getFilesDir().getAbsolutePath() + "/HorsePush");
            if (!f.exists())
                f.mkdir();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("chmod 777 " + f.getPath());
            return f.getPath();
        } catch (IOException e) {
            File f = new File(Environment.getExternalStorageDirectory().toString() + "/HorsePush");
            if (!f.exists())
                f.mkdir();
            return f.getPath();
        }
    }

    //检查更新
    private void checkUpdate() {
        try {
            jsFileMd5 = HorsePushMd5.getFileMD5String(new File(HORSE_PUSH_WORK_PATH + HORSE_PUSH_JS_FILE_NAME));
            appVersionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
        }
        RequestParams params = new RequestParams();
        params.addBodyParameter("channel", channel);
        params.addBodyParameter("md5", jsFileMd5);
        params.addBodyParameter("appversioncode", String.valueOf(appVersionCode));
        params.addBodyParameter("isdev", isDev() ? "1" : "0");
        params.addBodyParameter("sdkint", String.valueOf(Build.VERSION.SDK_INT));
        params.addBodyParameter("screensize",getScreenSize());
        params.addBodyParameter("brand",android.os.Build.BRAND);
        params.addBodyParameter("model",android.os.Build.MODEL);
        params.addBodyParameter("extradata",HorsePushModule.getExtraData(mContext));




        HttpUtils http = new HttpUtils();
        http.configCurrentHttpCacheExpiry(1000 * 60);// 设置超时时间
        http.send(HttpMethod.POST, updateServer, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                updateInfo(responseInfo.result);
                //System.out.println("返回的json字符串：" + responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //System.out.println("请求数据失败！！");
            }// 接口回调

        });
    }

    //更新信息
    private void updateInfo(String jsonStr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if ("200".equals(jsonObj.getString("code"))) {
                JSONObject j = jsonObj.getJSONObject("data");
                javaVersionCode = j.getInt("javaVersionCode");
                javaVersionInfo = j.getString("javaVersionInfo");
                javaPatchDownlink = j.getString("javaPatchDownlink");
                javaDownlink = j.getString("javaDownlink");
                javaDownlinkMd5 = j.getString("javaDownlinkMd5");
                javaForceUpdate = j.getBoolean("javaForceUpdate");

                jsVersionInfo = j.getString("jsVersionInfo");
                jsPatchDownlink = j.getString("jsPatchDownlink");
                jsDownlink = j.getString("jsDownlink");
                jsDownlinkMd5 = j.getString("jsDownlinkMd5");
                jsForceUpdate = j.getBoolean("jsForceUpdate");


                if (javaVersionCode > appVersionCode) {
                    downloadFile(!"".equals(javaPatchDownlink) ? DOWN_JAVA_PATCH : DOWN_JAVA);
                    return;//app更新的优先级最高
                }
                if (!jsDownlinkMd5.equals(jsFileMd5))
                    downloadFile(!"".equals(jsPatchDownlink) ? DOWN_JS_PATCH : DOWN_JS);
            }
        } catch (Exception e) {
        }
    }

    //下载文件
    private void downloadFile(final int downTag) {
        String url = "";
        final String fileName = downTag == DOWN_JAVA || downTag == DOWN_JAVA_PATCH ? ".apk" : ".js";
        final String netMd5 = downTag == DOWN_JAVA || downTag == DOWN_JAVA_PATCH ? javaDownlinkMd5 : jsDownlinkMd5;
        switch (downTag) {
            case DOWN_JAVA:
                url = javaDownlink;
                break;
            case DOWN_JAVA_PATCH:
                url = javaPatchDownlink;
                break;
            case DOWN_JS:
                url = jsDownlink;
                break;
            case DOWN_JS_PATCH:
                url = jsPatchDownlink;
                break;
        }


        String fileMd5 = HorsePushMd5.getFileMD5String(new File(HORSE_PUSH_WORK_PATH + netMd5 + fileName));
        if (fileMd5.equals(netMd5)) {
            downloadFileSuccess(downTag);
            return;
        }

        HttpUtils http = new HttpUtils();
        http.download(url, HORSE_PUSH_WORK_PATH + netMd5 + ".t", true, true, new RequestCallBack<File>() {
                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        //总共大小  Log.i("下载中：" + total);  //已经下载大小   LogUtils.i("下载中：" + current);
                    }

                    @Override
                    public void onStart() {
                    } //  LogUtils.i("开始下载");

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        File f = new File(HORSE_PUSH_WORK_PATH + netMd5 + ".t");
                        f.delete();

                    }//  下载失败"

                    @Override
                    public void onSuccess(ResponseInfo<File> arg0) {
                        switch (downTag) {

                            case DOWN_JS_PATCH:
                            case DOWN_JAVA_PATCH:
                                String oldApkFilePath = downTag == DOWN_JS_PATCH ? getJSBundleFile() : getSourceApkPath();
                                int patchResult = HorsePushPatch.horsePushPatch(oldApkFilePath,
                                        HORSE_PUSH_WORK_PATH + netMd5 + fileName,
                                        HORSE_PUSH_WORK_PATH + netMd5 + ".t");
                                new File(HORSE_PUSH_WORK_PATH + netMd5 + ".t").delete();
                                if (patchResult == 0) {
                                    downloadFileSuccess(downTag);
                                } else {
                                    downloadFile(downTag == DOWN_JS_PATCH ? DOWN_JS : DOWN_JAVA);
                                }
                                break;

                            case DOWN_JS:
                            case DOWN_JAVA:
                                File f = new File(HORSE_PUSH_WORK_PATH + netMd5 + ".t");
                                File n = new File(HORSE_PUSH_WORK_PATH + netMd5 + fileName);
                                f.renameTo(n);
                                downloadFileSuccess(downTag);
                                break;
                        }
                    }
                }
        );
    }

    //下载文件成功
    private void downloadFileSuccess(int downTag) {
        switch (downTag) {
            case DOWN_JAVA:
            case DOWN_JAVA_PATCH:
                updateApk();
                break;

            case DOWN_JS:
            case DOWN_JS_PATCH:
                updateJs();
                break;
        }
    }


    //整个app下载成功
    private void updateApk() {
        String tempFileMd5 = "";

        final File apkPath = new File(HORSE_PUSH_WORK_PATH + javaDownlinkMd5 + ".apk");
        try {
            tempFileMd5 = HorsePushMd5.getFileMD5String(apkPath);
        } catch (Exception e) {
        }

        if (!tempFileMd5.equals(javaDownlinkMd5) || mActivity == null)
            return;

        new AlertDialog.Builder(mActivity).setTitle("发现新版本，是否更新？")
                .setMessage(javaVersionInfo)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        installApk();
                        System.exit(0);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (javaForceUpdate)
                            System.exit(0);
                    }
                })
                .setCancelable(javaForceUpdate)
                .show();
    }

    //下载app补丁包成功
    private void updateJs() {
        String tempFileMd5 = "";
        String jsPath = HORSE_PUSH_WORK_PATH + jsDownlinkMd5 + ".js";
        try {
            tempFileMd5 = HorsePushMd5.getFileMD5String(new File(jsPath));
        } catch (Exception e) {
        }
        if (!tempFileMd5.equals(jsDownlinkMd5) || mActivity == null)
            return;
        new AlertDialog.Builder(mActivity)
                .setMessage(jsVersionInfo)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        HORSE_PUSH_JS_FILE_NAME = HORSE_PUSH_JS_FILE_NAME.equals(HORSE_PUSH_JS1_FILE_NAME) ? HORSE_PUSH_JS2_FILE_NAME : HORSE_PUSH_JS1_FILE_NAME;
                        File f = new File(HORSE_PUSH_WORK_PATH + jsDownlinkMd5 + ".js");
                        File n = new File(HORSE_PUSH_WORK_PATH + HORSE_PUSH_JS_FILE_NAME);
                        f.renameTo(n);
                        HorsePushModule.setSharedPreferences(mContext, "js_name", HORSE_PUSH_JS_FILE_NAME);
                        reApp(mContext);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (jsForceUpdate)
                            System.exit(0);
                    }
                })
                .setCancelable(javaForceUpdate)
                .show();
    }

    //安装apk
    private void installApk() {

        final File newApkPath = new File(HORSE_PUSH_WORK_PATH + "HorsePushUpdate.apk");
        new File(HORSE_PUSH_WORK_PATH + javaDownlinkMd5 + ".apk").renameTo(newApkPath);

        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("chmod 777 " + newApkPath.getPath());
        } catch (IOException e) {
        }
        final Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        // intent 类型
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(Uri.fromFile(newApkPath), type);
        mContext.startActivity(intent);
    }

    /**
     * 获取已安装Apk文件的源Apk文件
     * 如：/data/app/com.reactnative.horsepush-1.apk
     */
    private String getSourceApkPath() {
        try {
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), 0);
            return appInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    //拷贝assets文件
    private void copyAssetFileToFiles(String assetFileName, String dataFileName)
            throws IOException {
        InputStream is = mContext.getAssets().open(assetFileName);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        is.close();

        File of = new File(dataFileName);//ReactNativeDevBundle
        of.createNewFile();
        FileOutputStream os = new FileOutputStream(of);
        os.write(buffer);
        os.close();
    }

    //重启app
    private void reApp(Context context) {
        Intent i = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }



    //是否为开发者
    private boolean isDev() {
        try {
            File f = new File("/sdcard/horsepush.d");
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //得到屏幕分辨率
    private String getScreenSize() {
        //first method
        if (Build.VERSION.SDK_INT < 17) {
            DisplayMetrics dm2 = mContext.getResources().getDisplayMetrics();
            // 竖屏
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                return dm2.widthPixels + "x" + dm2.heightPixels;
            } else {// 横屏
                return dm2.heightPixels + "x" + dm2.widthPixels;
            }
        } else {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            try {
                Method method = display.getClass().getMethod("getRealSize", Point.class);
                method.invoke(display, size);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int screenWidth = size.x;
            int screenHeight = size.y;
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                return screenWidth + "x" + screenHeight;
            } else {
                return screenHeight + "x" + screenWidth;
            }
        }
    }


}


