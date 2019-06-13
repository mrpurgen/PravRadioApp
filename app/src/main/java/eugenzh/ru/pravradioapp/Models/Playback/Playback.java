package eugenzh.ru.pravradioapp.Models.Playback;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;

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

    interface Callback{
        void onCompletion();
        void onError(String errMsg);
        void onPlaybackStatusChanged(int state);
    }
}
