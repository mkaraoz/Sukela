package org.bok.mk.sukela.data.model.pack.uludag;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.model.FloatingColors;
import org.bok.mk.sukela.data.model.SozlukEnum;
import org.bok.mk.sukela.data.model.pack.Pack;

public abstract class UludagPack implements Pack
{
    @Override
    public FloatingColors getFloatingColors() {
        return new FloatingColors(R.color.uludag, R.color.dark_purple_700,
                R.color.dark_purple_900, R.color.dark_purple_400, R.color.dark_purple_500,
                R.color.dark_purple_700);
    }

    @Override
    public int getThemeId() {
        return R.style.UludagTheme;
    }

    @Override
    public int getBottomBarColorId() {
        return R.color.uludag;
    }

    @Override
    public SozlukEnum getSozluk() {
        return SozlukEnum.ULUDAG;
    }
}
