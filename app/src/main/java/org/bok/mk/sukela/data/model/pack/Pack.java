package org.bok.mk.sukela.data.model.pack;

import org.bok.mk.sukela.data.model.FloatingColors;
import org.bok.mk.sukela.data.model.SozlukEnum;

import java.io.Serializable;

public interface Pack extends Serializable
{
    String getTitle();

    FloatingColors getFloatingColors();

    String getTag();

    int getThemeId();

    int getBottomBarColorId();

    SozlukEnum getSozluk();
}
