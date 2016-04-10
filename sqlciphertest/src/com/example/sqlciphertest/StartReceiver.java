package com.example.sqlciphertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StartReceiver extends BroadcastReceiver    
{   
    /*要接收的intent源*/  
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";   
           
    public void onReceive(Context context, Intent intent)    
    {   
        if (intent.getAction().equals(ACTION))    
        {   
                  context.startService(new Intent(context,    
                       MainService.class));
             Toast.makeText(context, "Mainservice has started!", Toast.LENGTH_LONG).show();   
        }   
    }   
}  