package eugenzh.ru.pravradioapp.Services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import eugenzh.ru.pravradioapp.Models.Downloading.DownloadState;
import eugenzh.ru.pravradioapp.Models.Downloading.DownloadingFile;
import eugenzh.ru.pravradioapp.Models.Downloading.HandlerResultDownload;
import eugenzh.ru.pravradioapp.R;

public class DownloadService extends IntentService implements HandlerResultDownload {
    final private static String DOWNLOAD_SERVICE_URL_KEY = "DOWNLOAD_SERVICE_URL_KEY";
    final private static String DOWNLOAD_SERVICE_FOLDER_NAME_KEY = "DOWNLOAD_SERVICE_FOLDER_NAME_KEY";
    final private static String DOWNLOAD_SERVICE_FILE_NAME_KEY = "DOWNLOAD_SERVICE_FILE_NAME_KEY";

    final static private String NOTIFICATION_DOWNLOAD_DEFAULT_CHANNEL_ID = "download_default_channel";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private boolean is_download_execute;
    private DownloadingFile downloadingFile;

    public static void startService(Context context, String url, String folderName, String fileName){
        Intent intent = new Intent(context, DownloadService.class);

        intent.putExtra(DOWNLOAD_SERVICE_URL_KEY, url);
        intent.putExtra(DOWNLOAD_SERVICE_FOLDER_NAME_KEY, folderName);
        intent.putExtra(DOWNLOAD_SERVICE_FILE_NAME_KEY, fileName);

        context.startService(intent);
    }

    public DownloadService(){
        super("Download podcast service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String url = intent.getStringExtra(DOWNLOAD_SERVICE_URL_KEY);
        String fileName = intent.getStringExtra(DOWNLOAD_SERVICE_FILE_NAME_KEY);
        String folderName = intent.getStringExtra(DOWNLOAD_SERVICE_FOLDER_NAME_KEY);

        createNotificationDownloadProgress(fileName);
        initDownloadFile(url, folderName, fileName);
    }

    private void initDownloadFile(String url, String folderName, String fileName){
        downloadingFile = new DownloadingFile(this);
        downloadingFile.downloadFile(url, folderName, fileName);
        sendProgressNotificationTask();
    }

    private void createNotificationDownloadProgress(String fn){
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(this, notificationManager);

        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_DOWNLOAD_DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle(fn)
                .setContentText(getResources().getString(R.string.download_notify_title))
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
    }

     private void createNotificationChannel(Context context, NotificationManager nm){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String name = context.getString(R.string.notify_channel_name);
            String desc = context.getString(R.string.notify_download_channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_DOWNLOAD_DEFAULT_CHANNEL_ID, name, importance);
            channel.setDescription(desc);
            nm.createNotificationChannel(channel);
        }
    }

    private void sendProgressNotificationTask(){
        is_download_execute = true;

        while (is_download_execute) {
            DownloadState download = downloadingFile.getDownloadingInfo();
            sendProgressNotification(download);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendProgressNotification(DownloadState download){
        notificationBuilder.setProgress(100,download.getProgress(),false);
        notificationBuilder.setContentText(String.format("Downloaded (%d/%d) MB",download.getCurrentFileSize(),download.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onSuccRequestDownload() {
        is_download_execute = false;
        notificationManager.cancel(0);
        notificationBuilder.setProgress(0,0,false);
        notificationBuilder.setContentText("File Downloaded");
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onFailRequestDownload() {

    }
}
