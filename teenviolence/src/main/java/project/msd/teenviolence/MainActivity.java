package project.msd.teenviolence;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button alarmbutton, cancelButton;
    EditText text;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmbutton = (Button) findViewById(R.id.button);
        cancelButton = (Button) findViewById(R.id.button2);
        text = (EditText) findViewById(R.id.editText);
        alarmbutton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        /* use this method if you want to start Alarm at particular time*/

        // startAlertAtParticularTime();

    }

    // This method to be called at Start button click and set repeating at every 10 seconds interval

    public void startAlert(View view) {
        //if (!text.getText().toString().equals("")) {
        int i = Integer.parseInt(text.getText().toString());
        intent = new Intent(this, MyBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 2000, pendingIntent);

        Toast.makeText(this, "Alarm will set in " + " seconds",
                Toast.LENGTH_LONG).show();
        //}else {
          //  Toast.makeText(this,"Please Provide time ",Toast.LENGTH_SHORT).show();
        //}

    }

    public void startAlertAtParticularTime() {

        // alarm first vibrate at 14 hrs and 40 min and repeat itself at ONE_HOUR interval

        intent = new Intent(this, MyBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 4);
//        System.out.println("Hour set: "+calendar.get(Calendar.HOUR_OF_DAY));
//        System.out.println("Minutes set: "+calendar.get(Calendar.MINUTE));
        System.out.println("blabalakksdfjksj");
        System.out.println("Time in millis: "+calendar.getTimeInMillis());

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR, pendingIntent);

        Toast.makeText(this, "Alarm will vibrate at time specified"+calendar.getTimeInMillis(),
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            startAlert(v);
        } else {
            if (alarmManager != null) {

                alarmManager.cancel(pendingIntent);
                Toast.makeText(this, "Alarm Disabled !!",Toast.LENGTH_LONG).show();

            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }


    }
}