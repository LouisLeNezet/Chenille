package com.example.bureau.testhorlogesimple;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationView extends Activity {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        TextView notif = (TextView) findViewById(R.id.notification);
        Bundle data = getIntent().getExtras();
        notif.setText(data.getString("content"));
    }
}