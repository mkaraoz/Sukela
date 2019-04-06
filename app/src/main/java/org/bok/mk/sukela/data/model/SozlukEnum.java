package org.bok.mk.sukela.data.model;

public enum SozlukEnum
{
    EKSI(Contract.SOZLUK_ENUM_EKSI),
    INSTELA(Contract.SOZLUK_ENUM_INSTELA),
    ULUDAG(Contract.SOZLUK_ENUM_ULUDAG);

    private final String mName;

    SozlukEnum(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public static SozlukEnum getSozlukEnum(String name) {
        // required for older backups
        if (name.equals("İtü Sözlük")) { return INSTELA; }

        for (SozlukEnum s : SozlukEnum.values()) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        throw new RuntimeException(
                "Hatalı sözlük adı: " + name + ". Contract sınıfında tanımlı girileri kullanın.");
    }
}

