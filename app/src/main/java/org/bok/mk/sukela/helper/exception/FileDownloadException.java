package org.bok.mk.sukela.helper.exception;

import java.io.IOException;

/**
 * Created by mk on 22.12.2016.
 */

public class FileDownloadException extends IOException
{
    /**
     * thrown when a file download operation fails
     */
    public FileDownloadException(String message)
    {
        super(message);
    }
}
