# IosLikeToggleButton 仿IOS的开关按钮

#### 效果如下

<img src="https://github.com/jiaowenzheng/IosLikeToggleButton/raw/master/screen.png" width="500" height="300"/>  

<br/>


#### 用法

         xmlns:app="http://schemas.android.com/apk/res-auto"
        
        
            <com.ios.button.IosLikeToggleButton
                android:id="@+id/toggle_btn"
                android:layout_width="50dp"
                android:layout_height="wrap_content"
                app:off_color="#FFFFFF"
                app:on_color="#00FF00"
                app:toggle="true"
                />
                
          必须设置android:layout_width="具体的数值"，不可以设置 wrap_content or match_parent      
        
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

