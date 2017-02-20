package org.bok.mk.sukela.helper.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;

import org.apache.commons.io.FileUtils;
import org.bok.mk.sukela.helper.callbacks.MultiFileDownloadCallback;
import org.bok.mk.sukela.helper.callbacks.SingleFileDownloadCallback;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by mk on 24.08.2015.
 */
public class NetworkManager
{
    private NetworkManager()
    {
        throw new IllegalAccessError("You are not welcome here!");
    }

    /**
     * URL si verilen web sayfasını /data/data/org.bok.mk.sukela/files klasörü altına kaydeder.
     * Eğer 5 saniye içinde sayfaya bağlanamazsa ve ya 10 saniye içinde sayfayı okuyamazsa time-
     * out exception atar.
     *
     * @param urlAddress Page to be downloaded
     * @param fullPath   Path to save the file
     * @throws IOException if page cannot be found, connected or read
     */
    public static void downloadPage(String urlAddress, String fullPath) throws IOException
    {
        URL url = new URL(urlAddress);
        File tempFile = new File(fullPath);
        org.apache.commons.io.FileUtils.copyURLToFile(url, tempFile, 5000, 10000);
    }

    public static boolean isOnline(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    public static void downloadPage(String urlAddress, String directory, String fileName, MultiFileDownloadCallback feedback) throws IOException
    {
        int count;
        InputStream input = null;
        OutputStream output = null;
        try
        {
            URL url = new URL(urlAddress);
            URLConnection connection = url.openConnection();

            /**
             * The content length is not always available because by default Android request a GZIP
             * compressed response. The  Content-Encoding and  Content-Length response headers are
             * cleared in this case. Gzip compression can be disabled by setting the acceptable encodings
             */
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.connect();

            // get file length
            int lengthOfFile = connection.getContentLength();
            if (lengthOfFile < 0)
            {
                lengthOfFile = 250000; // average download size
            }

            // input stream to read file - with 8k buffer
            input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            String fullPath = directory + File.separator + fileName;
            output = new FileOutputStream(fullPath);

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1)
            {
                total += count;
                long progress = (total * 100) / lengthOfFile;
                feedback.updateProgress((int) Math.floor(progress));
                Log.d("_MK", progress + "");
                output.write(data, 0, count);

                if (feedback.isTaskCancelled())
                {
                    FileUtils.deleteQuietly(new File(directory));
                    return;
                }
            }
        }
        finally
        {
            try
            {
                output.flush();
                output.close();
                input.close();
            }
            catch (Exception ignore)
            {
            }
        }
    }

    public static void downloadPage(DownloadPack pack, String tag, SingleFileDownloadCallback callback)
    {
        ANRequest.DownloadBuilder downBuilder = AndroidNetworking.download(pack.urlAddress, pack.directory, pack.fileName);
        ANRequest request = downBuilder.setTag(tag).setPriority(Priority.MEDIUM).build();
        request.setDownloadProgressListener(callback);
        request.startDownload(callback);
    }
}
