package org.bok.mk.sukela.data.model.pack.instela;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.model.FloatingColors;
import org.bok.mk.sukela.data.model.SozlukEnum;
import org.bok.mk.sukela.data.model.pack.Pack;

public abstract class InstelaPack implements Pack
{
    @Override
    public FloatingColors getFloatingColors() {
        return new FloatingColors(R.color.instela, R.color.cyan_800, R.color.cyan_900,
                R.color.cyan_500, R.color.cyan_600, R.color.cyan_800);
    }

    @Override
    public int getThemeId() {
        return R.style.InstelaTheme;
    }

    @Override
    public int getBottomBarColorId() {
        return R.color.instela;
    }

    @Override
    public SozlukEnum getSozluk() {
        return SozlukEnum.INSTELA;
    }
}
