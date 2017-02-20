package org.bok.mk.sukela.helper.exception;

import java.io.IOException;

/**
 * Created by mk on 22.12.2016.
 */
public class JerichoFileReadException extends IOException
{
    /**
     * thrown when jericho cannot read a local file as a source
     */
    public JerichoFileReadException(final String message)
    {
        super(message);
    }
}
