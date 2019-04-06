package org.bok.mk.sukela.data.model.pack.eksi;

import org.bok.mk.sukela.data.model.Contract;

public class UserPack extends EksiPack
{
    private final String username;

    public UserPack(String username) {
        this.username = username;
    }

    @Override
    public String getTitle() {
        return username;
    }

    @Override
    public String getTag() {
        return createTag(username);
    }

    public static String createTag(String name) {
        return Contract.TAG_USER + "_" + name;
    }
}
