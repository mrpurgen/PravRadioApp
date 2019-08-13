package eugenzh.ru.pravradiopodcast.Models.Playback;

public interface Playback {
    void start();
    void stop();
    void setState(int state);
    int getState();
    boolean isConnected();
    boolean isPlaying();
    long getCurrentStreamPosition();
    void updateLastKnowStreamPosition();
    void play();
    void pause();
    void seekTo(long position);
    void setCallback(Callback callback);
    void returnFromPause();

    interface Callback{
        void onCompletion();
        void onError(String errMsg);
        void onPlaybackStatusChanged(int state);
    }
}
