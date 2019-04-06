package org.bok.mk.sukela;

import android.content.Context;
import android.support.annotation.NonNull;

import org.bok.mk.sukela.data.source.ArsivRepo;
import org.bok.mk.sukela.data.source.EntryRepo;
import org.bok.mk.sukela.data.source.UserRepo;
import org.bok.mk.sukela.data.source.db.EntryDatabase;
import org.bok.mk.sukela.data.source.repo.LocalEntryRepository;
import org.bok.mk.sukela.data.source.db.UserDatabase;
import org.bok.mk.sukela.data.source.repo.RemoteEntryRepository;

import static org.bok.mk.sukela.util.Preconditions.checkNotNull;

public class Injection
{
    public static EntryRepo provideEntryRepository(@NonNull Context context) {
        checkNotNull(context);
        EntryDatabase database = EntryDatabase.getInstance(context);
        return EntryRepo.getInstance(RemoteEntryRepository.getInstance(),
                LocalEntryRepository.getInstance(database.entryDao()));
    }

    public static UserRepo provideUserRepository(@NonNull Context context, @NonNull EntryRepo entryRepo) {
        checkNotNull(context);
        return UserRepo.getInstance(
                UserDatabase.getInstance(context).userDao(),
                entryRepo);
    }

    public static ArsivRepo provideArsivRepository(@NonNull Context context) {
        checkNotNull(context);
        return ArsivRepo.getInstance(
                EntryDatabase.getInstance(context).entryDao());
    }
}
