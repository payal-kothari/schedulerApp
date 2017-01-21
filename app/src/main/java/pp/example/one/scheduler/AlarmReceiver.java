package pp.example.one.scheduler;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import java.util.Random;

/**
 * Created by payalkothari on 1/15/17.
 */
public class AlarmReceiver extends BroadcastReceiver {
    MediaPlayer mp;
    Random rand = new Random();
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    MainActivity mainActivity = new MainActivity();
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        String tone = intent.getStringExtra("Tone");

        if(tone.equals("ring")){
//            mainActivity.alarmsMap.remove(id);
            mainActivity.removeAlarmToneForRowFromSharedPref(id);
            mp= MediaPlayer.create(context, R.raw.thuglife);
            mp.start();
            notificationManager.notify(id, notification);
        }else if(tone.equals("beep")){
//            mainActivity.alarmsMap.remove(id);
            mainActivity.removeAlarmToneForRowFromSharedPref(id);
            mp= MediaPlayer.create(context, R.raw.double_beep);
            mp.start();
            notificationManager.notify(id, notification);
        }else if(tone.equals("vibrate")){
//            mainActivity.alarmsMap.remove(id);
            mainActivity.removeAlarmToneForRowFromSharedPref(id);
            notificationManager.notify(id, notification);
        }
    }
}
