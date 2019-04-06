package org.bok.mk.sukela.data.source.sozluk.sozlock;

public class SozlockFactory
{
    public static Sozlock getInstance() {
        return new SozlockRunner();
    }
}
