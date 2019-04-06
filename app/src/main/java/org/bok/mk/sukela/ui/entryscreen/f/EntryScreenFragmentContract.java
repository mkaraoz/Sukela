package org.bok.mk.sukela.ui.entryscreen.f;

interface EntryScreenFragmentContract
{
    interface View
    {
        void openUrl(final String url);

        void printEntryTitle(String title);

        void setMainBackgroundColor(int mainBackgroundColorResId);

        void setTitleBoxBackColor(int titleBoxBackgroundDrawableId);

        void setEntryTitleColor(int titleBoxColor);

        void loadEntryBody(String data, String mimeType, String encoding);

        String getStringRes(int resId);

        void setWebViewTextSize(int zoom);
    }

    interface Presenter
    {
        int getBottomColor();

        void titleClicked();

        void createUi();
    }
}
