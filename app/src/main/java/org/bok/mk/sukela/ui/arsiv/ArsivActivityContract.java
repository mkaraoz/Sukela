package org.bok.mk.sukela.ui.arsiv;

import com.applandeo.materialcalendarview.EventDay;

import org.bok.mk.sukela.data.model.pack.Pack;

import java.util.Calendar;
import java.util.List;

interface ArsivActivityContract
{
    interface View
    {
        void setMaxDate(Calendar cal);

        void setMinDate(Calendar cal);

        void displayTimePicker(Calendar min, Calendar max, int firstDayOfWeek);

        void setDate(Calendar calendar);

        void startEntryScreen(Pack pack);

        Calendar getSelectedDate();

        void paintEvents(List<EventDay> events);
    }

    interface Presenter
    {
        void headerClicked();

        void fabClicked();

        void uiLoadCompleted();

        void datePicked(int y, int m, int d);

    }
}
