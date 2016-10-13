package it.polito.mad.noticeboard.advertiser;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import it.polito.mad.noticeboard.HomeActivity;
import it.polito.mad.noticeboard.R;

public class NewReportsBReceiver extends BroadcastReceiver {

    private static final int MY_NOTIFICATION_ID = 1;

    public NewReportsBReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        CharSequence title = "Attention";
        CharSequence message = "Some of your notices were flagged as inadequate";
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = new Notification(R.drawable.ic_stop,"A New Message!",System.currentTimeMillis());

        notification.defaults=Notification.FLAG_ONLY_ALERT_ONCE+Notification.FLAG_AUTO_CANCEL;
        Intent notificationIntent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,notificationIntent, 0);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(context, title,message, null);
        notificationManager.notify(MY_NOTIFICATION_ID, notification);

/*
        NotificationCompat.Builder notify = new NotificationCompat.Builder(context)
                .setContentTitle(" is ready for download")
                .setContentText("Click to view or download recording")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_bookmark)
                .setTicker("Echo: your file is ready for download");


        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
*/
        if(Build.VERSION.SDK_INT<16){
            /*build notification for HoneyComb to ICS*/
            //notificationManager.notify(MY_NOTIFICATION_ID, notify.getNotification());
        }if(Build.VERSION.SDK_INT>15){
            /*Notification for Jellybean and above*/
            //notificationManager.notify(MY_NOTIFICATION_ID, notify.build());
        }


    }
}
