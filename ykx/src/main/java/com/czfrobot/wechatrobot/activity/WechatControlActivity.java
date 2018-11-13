package com.czfrobot.wechatrobot.activity;

import android.content.Intent;
import android.os.Bundle;

import com.czfrobot.wechatrobot.constant.Constants;
import com.czfrobot.wechatrobot.R;
import com.czfrobot.wechatrobot.utils.DeviceUtils;
import com.czfrobot.wechatrobot.utils.LocationUtil;
import com.czfrobot.wechatrobot.utils.Preferences;
import com.czfrobot.wechatrobot.utils.Utils;
import com.czfrobot.wechatrobot.utils.WechatControl;

import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class WechatControlActivity extends Activity {
    private boolean isSimulateGps = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.LOG_TAG, "WechatControlActivity, onCreate");


        setContentView(R.layout.activity_wechatcontrol);
        TextView uuid = (TextView)findViewById(R.id.uuid);

        String deviceId = Preferences.getJPushAlias(this);
        uuid.setText(deviceId);
        Button auto_send_sns = (Button) findViewById(R.id.auto_send_sns);
        auto_send_sns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WechatControl.publishSns(WechatControlActivity.this);
            }

        });


        Button auto_add_contact_friends = (Button) findViewById(R.id.auto_add_contact_friends);
        auto_add_contact_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WechatControl.addFriendContacts(WechatControlActivity.this);
            }

        });

        Button auto_search_friends = (Button) findViewById(R.id.auto_search_friends);
        auto_search_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WechatControl.searchFriends(WechatControlActivity.this);
            }

        });

        Button auto_send_friends_message = (Button) findViewById(R.id.auto_send_friends_message);
        auto_send_friends_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WechatControl.sendFriendsMessage(WechatControlActivity.this);
            }

        });

        Button auto_like_friends = (Button) findViewById(R.id.auto_like_friends);
        auto_like_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WechatControl.autolike(WechatControlActivity.this);
            }

        });


        Button auto_lock_screen = (Button) findViewById(R.id.auto_lock_screen);
        auto_lock_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceUtils.lockScreen(WechatControlActivity.this);
            }

        });
        Button auto_poweroff = (Button) findViewById(R.id.auto_poweroff);
        auto_poweroff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.execShellCmd("reboot -p");
            }

        });
        Button auto_reboot = (Button) findViewById(R.id.auto_reboot);
        auto_reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.execShellCmd("reboot");
            }

        });

        final Button simulate_gps = (Button) findViewById(R.id.simulate_gps);
        simulate_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSimulateGps) {
                    LocationUtil.stopMockLocation();
                    Toast.makeText(WechatControlActivity.this, "停止定位", Toast.LENGTH_SHORT).show();
                    simulate_gps.setText("启动虚拟定位");
                    isSimulateGps = true;
                }else {
                    LocationUtil.startLocaton();
                    Toast.makeText(WechatControlActivity.this, "启动定位", Toast.LENGTH_SHORT).show();
                    simulate_gps.setText("停止虚拟定位");
                    isSimulateGps = false;
                }
            }
        });

        Button find_nearby_friends = (Button) findViewById(R.id.find_nearby_friends);
        find_nearby_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WechatControl.searchNearbyFriends(WechatControlActivity.this);
            }
        });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }



    @Override
    protected void onResume() {
        Log.i(Constants.LOG_TAG, "WechatControlActivity, onResume");
        super.onResume();

    }


    @Override
    protected void onPause() {
        Log.i(Constants.LOG_TAG, "WechatControlActivity, onPause");
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        Log.i(Constants.LOG_TAG, "WechatControlActivity, onDestroy");
        LocationUtil.stopMockLocation();
        super.onDestroy();
    }




}
