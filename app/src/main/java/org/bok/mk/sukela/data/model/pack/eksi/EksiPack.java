package org.bok.mk.sukela.data.model.pack.eksi;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.model.FloatingColors;
import org.bok.mk.sukela.data.model.SozlukEnum;
import org.bok.mk.sukela.data.model.pack.Pack;

public abstract class EksiPack implements Pack
{
    @Override
    public FloatingColors getFloatingColors() {
        return new FloatingColors(R.color.green_600, R.color.green_800,
                R.color.green_900, R.color.eksi, R.color.green_C100, R.color.green_C200);
    }

    @Override
    public int getThemeId() {
        return R.style.EksiTheme;
    }

    @Override
    public int getBottomBarColorId() {
        return R.color.green_600;
    }

    @Override
    public SozlukEnum getSozluk() {
        return SozlukEnum.EKSI;
    }
}
