package org.bok.mk.sukela.ui.arsiv;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.DatePicker;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.github.clans.fab.FloatingActionButton;

import org.bok.mk.sukela.Injection;
import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.pack.Pack;
import org.bok.mk.sukela.ui.entryscreen.a.EntryScreenActivity;

import java.util.Calendar;
import java.util.List;

public class ArsivActivity extends AppCompatActivity implements ArsivActivityContract.View, DatePickerDialog.OnDateSetListener
{
    private static final String LOG_TAG = ArsivActivity.class.getSimpleName();

    private ArsivPresenter mPresenter;
    private CalendarView mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        mPresenter = new ArsivPresenter(
                Injection.provideArsivRepository(this));
        mPresenter.attachView(this);

        mCalendarView = findViewById(R.id.calendarView);
        // bu listener default kütüphanede yok. Ben 1.5.1 sürümünü indirip kendim ekledim.
        // https://github.com/Applandeo/Material-Calendar-View
        mCalendarView.setOnHeaderClickListener(() -> mPresenter.headerClicked());

        FloatingActionButton fab = findViewById(R.id.fab_item_hebe);
        fab.setOnClickListener(view -> mPresenter.fabClicked());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.uiLoadCompleted();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        mPresenter.datePicked(y, m, d);
    }

    @Override
    public void setMaxDate(Calendar maxDate) {
        mCalendarView.setMaximumDate(maxDate);
    }

    @Override
    public void setMinDate(Calendar minDate) {
        mCalendarView.setMinimumDate(minDate);
    }

    @Override
    public void displayTimePicker(Calendar min, Calendar max, int firstDayOfWeek) {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(ArsivActivity.this,
                ArsivActivity.this, y, m, d);
        datePickerDialog.getDatePicker().setMaxDate(max.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(min.getTimeInMillis());
        datePickerDialog.getDatePicker().setFirstDayOfWeek(firstDayOfWeek);
        datePickerDialog.show();
    }

    @Override
    public void setDate(Calendar calendar) {
        try {
            mCalendarView.setDate(calendar);
        }
        catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    @Override
    public void startEntryScreen(Pack pack) {
        this.runOnUiThread(() -> {
            Intent i = new Intent(ArsivActivity.this, EntryScreenActivity.class);
            i.putExtra(Contract.PACK, pack);
            startActivity(i);
        });
    }

    @Override
    public Calendar getSelectedDate() {
        return mCalendarView.getFirstSelectedDate();
    }

    @Override
    public void paintEvents(List<EventDay> events) {
        this.runOnUiThread(() -> mCalendarView.setEvents(events));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("Y", mCalendarView.getFirstSelectedDate().get(Calendar.YEAR));
        savedInstanceState.putInt("M", mCalendarView.getFirstSelectedDate().get(Calendar.MONTH));
        savedInstanceState.putInt("D",
                mCalendarView.getFirstSelectedDate().get(Calendar.DAY_OF_MONTH));
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int y = savedInstanceState.getInt("Y");
        int m = savedInstanceState.getInt("M");
        int d = savedInstanceState.getInt("D");
        Calendar c = Calendar.getInstance();
        c.set(y, m, d);
        try {
            mCalendarView.setDate(c);
        }
        catch (OutOfDateRangeException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }
}
