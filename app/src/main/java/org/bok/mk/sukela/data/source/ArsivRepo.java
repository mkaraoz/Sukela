package org.bok.mk.sukela.data.source;

import android.support.annotation.NonNull;

import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.source.db.EntryDao;

import java.util.List;

public class ArsivRepo implements ArsivDataSource
{
    private static ArsivRepo INSTANCE = null;
    private final EntryDao mEntryDao;

    private ArsivRepo(@NonNull EntryDao entryDao) {
        mEntryDao = entryDao;
    }

    public static ArsivRepo getInstance(@NonNull EntryDao entryDao) {
        if (INSTANCE == null) {
            synchronized (UserRepo.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ArsivRepo(entryDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public List<String> getArsivTags() {
        return mEntryDao.getArsivTags(Contract.TAG_ARCHIVE_DAY+"%");
    }
}
