package project.msd.teenviolence;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private PendingIntent pendingIntent;
    TextView textView, selectedTime;
    Button setTime;
    Button buttonCancelAlarm;
    int gSelectedHour, gSelectedMinute;
    Switch switchbutton;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        textView = (TextView) findViewById(R.id.usernameValue);
        setTime = (Button) findViewById(R.id.setTime);
        selectedTime = (TextView) findViewById(R.id.selectedTime);

        switchbutton = (Switch) findViewById(R.id.notificationSwitch);
        switchbutton.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton cb, boolean bchecked){

                if(bchecked){
                    setTime.setEnabled(true);
                    selectedTime.setText("");
                }

                else{
                   setTime.setEnabled(false);
                    cancelAlarm();
                    selectedTime.setText(
                            "\n\n***\n"
                                    + "Alarm Cancelled! \n"
                                    + "***\n");
                }
            }
        });

        if(switchbutton.isChecked()){
            setTime.setEnabled(true);
            selectedTime.setText("");
        }
        else{
            setTime.setEnabled(false);
            selectedTime.setText(
                    "\n\n***\n"
                            + "Alarm Cancelled! \n"
                            + "***\n");
        }

        Intent intent = getIntent();
        String text = intent.getStringExtra("text");
        textView.setText(text);

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        selectedTime.setText(selectedHour + ":" + selectedMinute);
                        gSelectedHour = selectedHour;
                        gSelectedMinute = selectedMinute;
                        System.out.println("selected time: "+gSelectedHour+gSelectedMinute);
                        setAlarm(gSelectedHour, gSelectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
//        setAlarm(gSelectedHour, gSelectedMinute);

    }

    public void setAlarm(int hour, int minute){

        Calendar calendar = Calendar.getInstance();
// we can set time by open date and time picker dialog
        //hour = calendar.get(Calendar.HOUR_OF_DAY);
        //minute = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        System.out.println("blablablablabla");
        System.out.println(calendar.getTimeInMillis());
        System.out.println("Hour: "+calendar.get(Calendar.HOUR_OF_DAY));
        System.out.println("Minute: "+calendar.get(Calendar.MINUTE));

        Intent intent1 = new Intent(MainActivity.this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        System.out.println("inside main activity, after intent1");
//      AlarmManager am = (AlarmManager) MainActivity.this
//              .getSystemService(MainActivity.this.ALARM_SERVICE);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
        System.out.println("in main activity, after calling set repeating");

    } // end onCreate

    private void cancelAlarm(){

        Intent intent1 = new Intent(MainActivity.this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }


}