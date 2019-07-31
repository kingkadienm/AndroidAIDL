package com.wangzs.oneapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import com.wangzs.aidl_library.IUserCallBack;
import com.wangzs.aidl_library.User;


/**
 * @Description:
 * @Author:  wangzs
 * @Date:    2019-07-31 10:52
 * @Version:
 * @Email    wangzs@yuntongxun.com
 */

public class MainOneActivity extends AppCompatActivity {

    private String TAG = "wangzs";
    IUserCallBack callBack = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isApkInstalled("com.wangzs.twoapp")) {
                    return;
                }
                // 这里以包名跳转
                Intent intent = new Intent(Intent.ACTION_MAIN);
                // 参数1:目标包名 参数2:目标activity的具体路径
                ComponentName componentName = new ComponentName("com.wangzs.twoapp", "com.wangzs.twoapp.MainTwoActivity");
                intent.setComponent(componentName);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnGet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("aidl_user_service");
                intent.setPackage("com.wangzs.twoapp"); //发送端pkg
                boolean isbind = bindService(intent, conn, BIND_AUTO_CREATE);
                Log.e(TAG, "接收数据 isbind == " + isbind);
            }
        });
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            callBack = IUserCallBack.Stub.asInterface(service);
            try {
                User user = callBack.getUser();
                if (user != null) {
                    Log.e(TAG, "接收数据  == " + user.toString());
                    Toast.makeText(MainOneActivity.this, "接收到数据, 数据看Log日志", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            callBack = null;
        }
    };


    public boolean isApkInstalled(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn!=null) {
            unbindService(conn);
        }
    }
}
