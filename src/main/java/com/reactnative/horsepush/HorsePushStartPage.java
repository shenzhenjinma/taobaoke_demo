package com.reactnative.horsepush;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by techbin on 2016/4/13 0013.
 */
public class HorsePushStartPage extends Activity {
    public static Activity mActivity=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mActivity = this;
        //全屏居中软件
        setTheme(android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        LinearLayout.LayoutParams layoutParamsRoot = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        //创建视图
        RelativeLayout layoutRoot = new RelativeLayout(this);
        //视图设置居中
        layoutRoot.setGravity(Gravity.CENTER);
        //图片设置
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(layoutParamsRoot);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        ProgressBar pro = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);//小圆
        WindowManager wm = this.getWindowManager();
        int height = wm.getDefaultDisplay().getHeight() / 4;
        LinearLayout tempviewRoot = new LinearLayout(this);
        tempviewRoot.setPadding(0, height, 0, 0);
        tempviewRoot.addView(pro);
        tempviewRoot.setLayoutParams(layoutParams);

        try {
//            AssetManager assetManager = getAssets();
//            InputStream is = assetManager.open("test.png");
//            //以下注释掉的代码不靠谱.若采用,会有异常
//            // Bitmap bitmap= BitmapFactory.decodeStream(is);
//            Drawable dr = Drawable.createFromStream(is, null);
//            imageView.setImageDrawable(dr);
            String img = HorsePush.getStartPageImgPath(this);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeFile(img, options);
            imageView.setImageBitmap(bm);

        } catch (Exception e) {
        }
        layoutRoot.addView(imageView);
        layoutRoot.addView(tempviewRoot);

        setContentView(layoutRoot);


        }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                HorsePush.mActivity.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }


}



