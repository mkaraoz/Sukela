package org.bok.mk.sukela.ui.arsiv;

import com.applandeo.materialcalendarview.EventDay;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.model.pack.Pack;
import org.bok.mk.sukela.data.model.pack.eksi.ArsivPack;
import org.bok.mk.sukela.data.source.ArsivDataSource;
import org.bok.mk.sukela.data.source.ArsivRepo;
import org.bok.mk.sukela.ui.base.BasePresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArsivPresenter extends BasePresenter<ArsivActivityContract.View> implements ArsivActivityContract.Presenter
{
    private final ArsivDataSource mArsivRepo;

    ArsivPresenter(ArsivRepo arsivRepo) {
        mArsivRepo = arsivRepo;
    }

    @Override
    public void headerClicked() {
        Calendar min = this.getMinDate();
        //calview ve datepicker uyumsuz, buna 1 eklemek gerekiyor.
        min.add(Calendar.DATE, 1);
        Calendar max = this.getMaxDate();
        mView.displayTimePicker(min, max, Calendar.MONDAY);
    }

    private Calendar getMaxDate() {
        return Calendar.getInstance();
    }

    private Calendar getMinDate() {
        // Arşiv kayıtları 4 Haziran 2015 tarihinde başlatılmış
        // ay sıfırdan başlıyor, 4 ilk geçerli gün
        Calendar cal = Calendar.getInstance();
        cal.set(2015, 5, 3);
        return cal;
    }

    @Override
    public void fabClicked() {
        Calendar cal = mView.getSelectedDate();
        Pack pack = new ArsivPack(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
        mView.startEntryScreen(pack);
    }

    @Override
    public void uiLoadCompleted() {
        mView.setMaxDate(getMaxDate());
        mView.setMinDate(getMinDate());
        paintEvents();
    }

    private void paintEvents() {
        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            List<EventDay> events = new ArrayList<>();
            List<String> tags = mArsivRepo.getArsivTags();
            for (String tag : tags) {
                Calendar c = Calendar.getInstance();
                int[] date = ArsivPack.split(tag);
                // calview month starts from zero
                c.set(date[0], date[1] - 1, date[2]);
                events.add(new EventDay(c, R.drawable.debe_blue_icon));
            }
            mView.paintEvents(events);
        });
        e.shutdown();
    }

    @Override
    public void datePicked(int y, int m, int d) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(y, m, d);
        mView.setDate(calendar);
    }
}
