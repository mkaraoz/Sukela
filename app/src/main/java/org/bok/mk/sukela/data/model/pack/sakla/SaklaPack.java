package org.bok.mk.sukela.data.model.pack.sakla;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.model.FloatingColors;
import org.bok.mk.sukela.data.model.SozlukEnum;
import org.bok.mk.sukela.data.model.pack.Pack;

public abstract class SaklaPack implements Pack
{
    @Override
    public FloatingColors getFloatingColors() {
        return new FloatingColors(R.color.colorPrimary, R.color.blue_700,
                R.color.colorPrimaryDark, R.color.blue_500, R.color.blue_600, R.color.blue_800);
    }

    @Override
    public int getThemeId() {
        return R.style.SaveTheme;
    }

    @Override
    public int getBottomBarColorId() {
        return R.color.colorPrimary; // just placeholder, can be any one of them
    }

    @Override
    public SozlukEnum getSozluk() {
        return null; // can be any one of them
    }
}
