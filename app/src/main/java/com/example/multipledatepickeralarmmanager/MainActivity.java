package com.example.multipledatepickeralarmmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView addTextStartDate;
    private TextView addTextEndDate;

    private Button buttonDateStart;
    private Button buttonDateEnd;
    private Calendar calendarStartDate;
    private Calendar calendarEndDate;

    static final int DATE_DIALOG_ID = 0;

    private TextView textDateActive;
    private Calendar calendarDateActive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTextEndDate = findViewById(R.id.add_text_date_end);
        addTextStartDate = findViewById(R.id.add_text_date_start);

        buttonDateStart =  findViewById(R.id.buttonStartDate);
        buttonDateEnd =  findViewById(R.id.buttonEndDate);

        calendarStartDate = Calendar.getInstance();
        calendarEndDate = Calendar.getInstance();


        buttonDateStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDateDialog(addTextStartDate, calendarStartDate);
            }
        });


        buttonDateEnd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDateDialog(addTextEndDate, calendarEndDate);
            }
        });

        updateDisplay(addTextStartDate, calendarStartDate);
        updateDisplay(addTextEndDate, calendarEndDate);


    }

    /////////////////////////////////////////////////////////////////
    private void updateDisplay(TextView dateDisplay, Calendar date) {
        dateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(date.get(Calendar.MONTH) + 1).append("-")
                        .append(date.get(Calendar.DAY_OF_MONTH)).append("-")
                        .append(date.get(Calendar.YEAR)).append(" "));
    }

    public void showDateDialog(TextView dateDisplay, Calendar date) {
        textDateActive = dateDisplay;
        calendarDateActive = date;
        showDialog(DATE_DIALOG_ID);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            calendarDateActive.set(Calendar.YEAR, year);
            calendarDateActive.set(Calendar.MONTH, monthOfYear);
            calendarDateActive.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDisplay(textDateActive, calendarDateActive);
            unregisterDateDisplay();

            updateDateText(c);
            startAlarm(c);
        }
    };

    private void unregisterDateDisplay() {
        textDateActive = null;
        calendarDateActive = null;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, dateSetListener, calendarDateActive.get(Calendar.YEAR), calendarDateActive.get(Calendar.MONTH), calendarDateActive.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(calendarDateActive.get(Calendar.YEAR), calendarDateActive.get(Calendar.MONTH), calendarDateActive.get(Calendar.DAY_OF_MONTH));
                break;
        }
    }
/////////////////////////////////////////////////////////////////////////

//        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//        public void onDateSet(DatePickerFragment view, int year, int month, int dayOfMonth) {
//            Calendar c = Calendar.getInstance();
//            c.set(Calendar.YEAR, year);
//            c.set(Calendar.MONTH, month);
//            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//
//            updateDateText(c);
//            startAlarm(c);
//        }

    //
    private void updateDateText(Calendar c) {
        String dateText = "Alarm set for: ";
        dateText += DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());

        addTextStartDate.setText(dateText);
    }

    //
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

}
