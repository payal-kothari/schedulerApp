package listviewfromsqlitedb.example.com.scheduler;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by payalkothari on 1/15/17.
 */
public class AlarmReceiver extends BroadcastReceiver {
    MediaPlayer mp;
    Random rand = new Random();
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        int randomNum = rand.nextInt((10 - 0) + 1) + 0;
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(randomNum > 5){
            mp= MediaPlayer.create(context, R.raw.thuglife);
            mp.start();
            Notification notification = intent.getParcelableExtra(NOTIFICATION);
            int id = intent.getIntExtra(NOTIFICATION_ID, 0);
            notificationManager.notify(id, notification);
            Toast.makeText(context, "time : " , Toast.LENGTH_LONG).show();
        }else {
            mp= MediaPlayer.create(context, R.raw.thuglife2);
            mp.start();
            Toast.makeText(context, "time : " , Toast.LENGTH_LONG).show();
        }

    }
}
