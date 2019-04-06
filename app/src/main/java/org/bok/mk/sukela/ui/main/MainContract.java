package org.bok.mk.sukela.ui.main;

interface MainContract
{
    interface View
    {

    }

    interface Presenter {
        void saveAnimState(Object tag);
        String getAnimState();
    }
}
