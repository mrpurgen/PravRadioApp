package eugenzh.ru.pravradiopodcast.Models.Downloading;

public class DownloadState {
    private int downloadProgress;
    private int totalFileSize;
    private int currentFileSize;

    public int getProgress() {
        return downloadProgress;
    }

    public void setProgress(int progress) {
        downloadProgress = progress;
    }

    public int getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(int totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    public int getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(int currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

}
