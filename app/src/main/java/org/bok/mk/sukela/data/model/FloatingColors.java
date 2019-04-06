package org.bok.mk.sukela.data.model;

public class FloatingColors
{
    public final int FAB_MENU_COLOR_NORMAL;
    public final int FAB_MENU_COLOR_PRESSED;
    public final int FAB_MENU_COLOR_RIPPLE;
    public final int FAB_ITEM_COLOR_NORMAL;
    public final int FAB_ITEM_COLOR_PRESSED;
    public final int FAB_ITEM_COLOR_RIPPLE;

    public FloatingColors(int menuColorNormal, int menuColorPressed, int menuColorRipple,
                          int itemColorNormal, int itemColorPressed, int itemColorRipple) {

        FAB_MENU_COLOR_NORMAL = menuColorNormal;
        FAB_MENU_COLOR_PRESSED = menuColorPressed;
        FAB_MENU_COLOR_RIPPLE = menuColorRipple;
        FAB_ITEM_COLOR_NORMAL = itemColorNormal;
        FAB_ITEM_COLOR_PRESSED = itemColorPressed;
        FAB_ITEM_COLOR_RIPPLE = itemColorRipple;
    }
}
