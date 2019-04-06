package org.bok.mk.sukela.ui.main;

import org.bok.mk.sukela.ui.base.BasePresenter;
import org.bok.mk.sukela.util.SharedPrefsHelper;

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter
{
    private static final String EXPANDED_HEADER = "EXPANDED_HEADER";
  
    private final SharedPrefsHelper mPreferences;

    MainPresenter(SharedPrefsHelper preferences) {
        mPreferences = preferences;
    }

    @Override
    public void saveAnimState(Object tag) {
        mPreferences.put(EXPANDED_HEADER, (String) tag);
    }

    @Override
    public String getAnimState() {
        return mPreferences.get(EXPANDED_HEADER, "E");
    }
}
