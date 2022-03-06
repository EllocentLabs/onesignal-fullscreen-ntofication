package com.onesignalfullscreennotification;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONException;
import com.onesignal.OSNotification;
import com.onesignal.OSMutableNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler;

import android.content.Intent;  
import com.facebook.react.bridge.ReactContext;

import com.facebook.react.bridge.ReactContextBaseJavaModule;

import android.view.WindowManager;
import android.os.Bundle;
import com.rnonesignalts.RideActivity;



@SuppressWarnings("unused")
public class NotificationServiceExtension implements OSRemoteNotificationReceivedHandler  {

    String source, rideId, type;
    

    public String getNotificationProperty(JSONObject data, String key) {
        try{
            String value = data.getString(key);
            return value;
        }
        catch(JSONException e){
            return null;
        }
    }

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent notificationReceivedEvent) {
        OSNotification notification = notificationReceivedEvent.getNotification();

        // Example of modifying the notification's accent color
        OSMutableNotification mutableNotification = notification.mutableCopy();

        try{
            
            JSONObject data = notification.getAdditionalData();

            source = getNotificationProperty(data, "source");
            rideId = getNotificationProperty(data, "rideId");
            type = getNotificationProperty(data, "type");



            if (RideActivity.active) {
                return;
            }
            if (context != null) {
                

                if(type.equals("NewRide")){
                    Bundle bundle = new Bundle();
                    bundle.putString("uuid", rideId);
                    bundle.putString("name", "New Ride");
                    bundle.putString("avatar", "https://pbs.twimg.com/profile_images/1483923698138689539/NHoaDPdv_400x400.jpg");
                    bundle.putString("info", source);
                    bundle.putInt("timeout", 20000);
                    Intent i = new Intent(context, RideActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                    
                    i.putExtras(bundle);
                    context.startActivity(i);
                }
                else {
                    Log.e("Another Notification", "Veer Ji");
                }
                
                
            }
            
        }
        catch(Error e){
            Log.d("App Crashed", e.getMessage());
            Log.e("App Crashed", "Veer Ji", e);
        }

        notificationReceivedEvent.complete(mutableNotification);
    }
}