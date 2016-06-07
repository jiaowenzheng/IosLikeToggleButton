package com.ios.toggle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ios.button.IosLikeToggleButton;

public class MainActivity extends AppCompatActivity {

    private IosLikeToggleButton mToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToggleButton = (IosLikeToggleButton) findViewById(R.id.toggle_btn);

        mToggleButton.setChecked(true); //设置开关状态

        mToggleButton.isChecked(); //获取开关状态

        //设置开关监听事件
        mToggleButton.setOnCheckedChangeListener(new IosLikeToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IosLikeToggleButton buttonView, boolean isChecked) {
                Log.i("toggle"," isChecked "+isChecked);
            }
        });

    }


}
