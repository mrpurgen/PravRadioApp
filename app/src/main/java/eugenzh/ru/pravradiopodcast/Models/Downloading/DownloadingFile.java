package eugenzh.ru.pravradiopodcast.Models.Downloading;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import eugenzh.ru.pravradiopodcast.Models.HttpConnection.DownloadAPI;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DownloadingFile {
    private String baseURL = "https://pravradio.ru/";
    private DownloadState downloadState;

    private HandlerResultDownload handlerResultDownload;

    public DownloadingFile(HandlerResultDownload handlers){
        handlerResultDownload = handlers;
    }

    public void downloadFile(String url, final String folderName, final String fileName){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .client(new OkHttpClient.Builder().build())
                .build();

        final DownloadAPI downloading = retrofit.create(DownloadAPI.class);

        String noBaseUrl = cuteBaseURL(url);

        Call<ResponseBody> call = downloading.downloadItem(noBaseUrl);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    DownloadTask downloadTask = new DownloadTask(folderName, fileName, response.body());
                    downloadTask.execute();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handlerResultDownload.onFailRequestDownload();
            }
        });
    }

    private String cuteBaseURL(String url){
        return url.replaceAll(baseURL, "");
    }

    private void saveToDisk(String folderName, String fileName, ResponseBody body){
        try {
            int count;
            downloadState = new DownloadState();

            String podcastFolderPath = pathToDownloadFolderConstr(folderName);
            File podcastFolder = new File(podcastFolderPath);
            podcastFolder.mkdirs();

            File fileOut = new File(podcastFolderPath, fileName);

            InputStream inputStream = body.byteStream();
            OutputStream outputStream = new FileOutputStream(fileOut);

            try {
                byte[] fileReader = new byte[1024 * 4];

                long fileSize = body.contentLength();

                downloadState.setTotalFileSize((int)(fileSize / Math.pow(1024, 2)));

                long fileSizeDownloaded = 0;


                while ( (count = inputStream.read(fileReader)) != -1) {

                    outputStream.write(fileReader, 0, count);

                    fileSizeDownloaded += count;
                    downloadState.setCurrentFileSize((int)(fileSizeDownloaded / Math.pow(1024, 2)));
                    downloadState.setProgress((int)((fileSizeDownloaded * 100) / fileSize));

                }
                handlerResultDownload.onSuccRequestDownload();
                outputStream.flush();

            } catch (IOException e) {
                handlerExceptionSaveFile(e);
            }  finally {
                inputStream.close();
                outputStream.close();
            }
        } catch (IOException e) {
            handlerExceptionCreateFile(e);
        }
    }

    private String pathToDownloadFolderConstr(String folderName){
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(Environment.getExternalStorageDirectory());
        pathBuilder.append(File.separator);
        pathBuilder.append("PravRadio");
        pathBuilder.append(File.separator);
        pathBuilder.append(folderName);

        return pathBuilder.toString();
    }

    private void handlerExceptionSaveFile(IOException e){
        e.getMessage();
    }

    private void handlerExceptionCreateFile(IOException e){
        e.getMessage();
    }

    public DownloadState getDownloadingInfo(){
        if (downloadState == null){
            return new DownloadState();
        }
        return downloadState;
    }

    private class DownloadTask extends AsyncTask<Void, Void, Void> {

        private String fileName;
        private String folderName;
        private ResponseBody responseBody;

        private DownloadTask(String folderName, String fileName, ResponseBody responseBody){
            this.folderName = folderName;
            this.fileName = fileName;
            this.responseBody = responseBody;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            saveToDisk(folderName, fileName, responseBody);
            return null;
        }
    }
}
