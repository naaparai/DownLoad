package com.example.ashok.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView textviewDownload, textViewCancel;
    private ThinDownloadManager downloadManager;
    File file;
    int downloadId;
    private NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        file = getOutputMediaFile(1);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(file.getAbsolutePath()));
        intent.setDataAndType(Uri.parse(file.getAbsolutePath()), "video/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, Intent.createChooser(intent, "Open folder"), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        textviewDownload = (TextView) findViewById(R.id.textViewDownload);
        textViewCancel = (TextView) findViewById(R.id.textViewCancel);
        downloadManager = new ThinDownloadManager();
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.cloud_download)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
        ;

        String url = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_10mb.mp4";
        // ex: "/Users/axet/Downloads"
        String path = file.getAbsolutePath();
        Uri downloadUri = Uri.parse(url);
        Uri destinationUri = Uri.parse(path);
        final DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadListener(new DownloadStatusListener() {
                    @Override
                    public void onDownloadComplete(int id) {
                        Toast.makeText(getApplicationContext(), "download complete", Toast.LENGTH_SHORT).show();
                        mBuilder.setContentText("Download complete")
                                // Removes the progress bar
                                .setProgress(0, 0, false);
                        mNotifyManager.notify(id, mBuilder.build());
                    }

                    @Override
                    public void onDownloadFailed(int id, int errorCode, String errorMessage) {

                    }

                    @Override
                    public void onProgress(int id, long totalBytes, long downlaodedBytes, int progress) {
                        Log.i("progress", "" + progress);
                        mBuilder.setProgress(100, progress, false);
                        // Displays the progress bar for the first time.
                        mNotifyManager.notify(id, mBuilder.build());
                        // Sleeps the thread, simulating an operation
                        // that takes time
                    }
                });


        textviewDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                textviewDownload.setVisibility(View.GONE);
                textViewCancel.setVisibility(View.VISIBLE);
                try {
                    // ex: http://www.youtube.com/watch?v=Nj6PFaDmp6c
                    file = getOutputMediaFile(1);
                    downloadId = downloadManager.add(downloadRequest);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewCancel.setVisibility(View.GONE);
                textviewDownload.setVisibility(View.VISIBLE);
                downloadManager.cancel(downloadId);
            }
        });



    }

    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() +
                File.separator + "DownloadVideoDemo");
        boolean success = true;
        if (!mediaStorageDir.exists()) {
            success = mediaStorageDir.mkdir();
        }
        if (success) {
            // Do something on success
        } else {
            Log.d("MyCameraApp", "failed to create directory");
            return null;
        }
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 0) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
