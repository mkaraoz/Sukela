package org.bok.mk.sukela.util;

import android.arch.persistence.room.TypeConverter;

import org.bok.mk.sukela.data.model.SozlukEnum;

public class SozlukEnumConverter
{
    @TypeConverter
    public static SozlukEnum toSozlukEnum(String value) {
        return SozlukEnum.getSozlukEnum(value);
    }

    @TypeConverter
    public static String toString(SozlukEnum sozlukEnum) {
        return sozlukEnum.getName();
    }
}
