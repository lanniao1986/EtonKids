package com.enix.hoken.custom.receiver;

import com.enix.hoken.activity.*;
import android.content.BroadcastReceiver;  
import android.content.Context;  
import android.content.Intent;  
 
public class AlarmReceiver extends BroadcastReceiver{  
 
    @Override  
    public void onReceive(Context context, Intent intent) {  
        Intent i=new Intent(context, AlarmActivity.class);  
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        context.startActivity(i);  
    }  
}  
