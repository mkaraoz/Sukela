package org.bok.mk.sukela.helper.callbacks;

/**
 * Created by mk on 26.12.2016.
 */

public interface MultiFileDownloadCallback {
    void updateProgress(int progress);

    boolean isTaskCancelled();
}
