package org.bok.mk.sukela.helper;

/**
 * Created by mk on 18.09.2016.
 */
public enum SozlukEnum
{
    EKSI(Contract.SOZLUK_ENUM_EKSI),
    INSTELA(Contract.SOZLUK_ENUM_INSTELA),
    ULUDAG(Contract.SOZLUK_ENUM_ULUDAG),
    INCI(Contract.SOZLUK_ENUM_INCI);

    private String mName;

    SozlukEnum(String name)
    {
        mName = name;
    }

    public String getName()
    {
        return mName;
    }

    public static SozlukEnum getSozlukEnum(String name)
    {
        if (name.equals("İtü Sözlük"))
            return INSTELA;

        for (SozlukEnum s : SozlukEnum.values())
        {
            if (s.getName().equals(name))
            {
                return s;
            }
        }
        throw new RuntimeException("Hatalı sözlük adı: " + name + ". Contract sınıfında tanımlı girileri kullanın.");
    }
}
