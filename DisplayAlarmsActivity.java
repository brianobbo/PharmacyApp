package edu.usf.cse.labrador.familycare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DisplayAlarmsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_alarms);


        SetUpDisplayAlarms retrieveAlarms = new SetUpDisplayAlarms(this);
        retrieveAlarms.execute();

        /*ArrayList<OneTimeAlarm> oneTimeAlarms = myAlarmDb.getAllOneTimeAlarms();

        final ArrayAdapter<OneTimeAlarm> oneTimeAdapter = new ArrayAdapter<OneTimeAlarm>(this, android.R.layout.simple_list_item_1, oneTimeAlarms);
        alarmList.setAdapter(oneTimeAdapter);*/
    }

    public void addOneTimeReminder(View view){
        Intent intent = new Intent(this, OneTimeReminderActivity.class);
        startActivity(intent);
    }
    public void addFrequentReminder(View view){
        Intent intent = new Intent(this, FrequentReminderActivity.class);
        startActivity(intent);
    }
}
