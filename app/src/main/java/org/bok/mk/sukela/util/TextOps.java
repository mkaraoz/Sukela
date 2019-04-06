package org.bok.mk.sukela.util;

public class TextOps
{
    private TextOps() {
    }

    public static String removeTurkishChars(String text) {
        String result = text.replaceAll("ı", "i");
        result = result.replaceAll("ü", "u");
        result = result.replaceAll("ğ", "g");
        result = result.replaceAll("ş", "s");
        result = result.replaceAll("ö", "o");
        result = result.replaceAll("ç", "c");

        return result;
    }

    public static String restoreTurkishChars(String entryBody) {
        entryBody = entryBody.replaceAll("ð", "ğ");
        entryBody = entryBody.replaceAll("ý", "ı");
        entryBody = entryBody.replaceAll("þ", "ş");
        return entryBody;
    }
}
