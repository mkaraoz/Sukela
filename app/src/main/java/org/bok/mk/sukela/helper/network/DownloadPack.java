package org.bok.mk.sukela.helper.network;

/**
 * Created by mk on 03.01.2017.
 */

public class DownloadPack
{
    public final String urlAddress, directory, fileName;

    private DownloadPack(String urlAddress, String directory, String fileName)
    {
        this.urlAddress = urlAddress;
        this.directory = directory;
        this.fileName = fileName;
    }

    public static DownloadPack create(String urlAddress, String directory, String fileName)
    {
        return new DownloadPack(urlAddress, directory, fileName);
    }
}
