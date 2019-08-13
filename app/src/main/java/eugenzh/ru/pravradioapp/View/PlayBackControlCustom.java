package eugenzh.ru.pravradioapp.View;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;

import com.google.android.exoplayer2.C;

import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ui.TimeBar;

import com.google.android.exoplayer2.util.RepeatModeUtil;
import com.google.android.exoplayer2.util.Util;

import eugenzh.ru.pravradioapp.R;

public class PlayBackControlCustom extends FrameLayout {

    /** Listener to be notified about changes of the visibility of the UI control. */
    public interface VisibilityListener {

        /**
         * Called when the visibility changes.
         *
         * @param visibility The new visibility. Either {@link View#VISIBLE} or {@link View#GONE}.
         */
        void onVisibilityChange(int visibility);
    }

    /** The default fast forward increment, in milliseconds. */
    public static final int DEFAULT_FAST_FORWARD_MS = 15000;
    /** The default rewind increment, in milliseconds. */
    public static final int DEFAULT_REWIND_MS = 5000;
    /** The default show timeout, in milliseconds. */
    public static final int DEFAULT_SHOW_TIMEOUT_MS = 5000;
    /** The default repeat toggle modes. */
    public static final @RepeatModeUtil.RepeatToggleModes int DEFAULT_REPEAT_TOGGLE_MODES =
            RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE;

    /** The maximum number of windows that can be shown in a multi-window time bar. */
    public static final int MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR = 100;

    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;

    private final View previousButton;
    private final View nextButton;
    private final View playButton;
    private final View pauseButton;
    private final ImageView repeatToggleButton;
    private final View shuffleButton;
    private final TextView durationView;
    private final TextView positionView;
    private final TextView trackName;
    private final TimeBar timeBar;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;
    private final Timeline.Period period;
    private final Timeline.Window window;

    private final Drawable repeatOffButtonDrawable;
    private final Drawable repeatOneButtonDrawable;
    private final Drawable repeatAllButtonDrawable;
    private final String repeatOffButtonContentDescription;
    private final String repeatOneButtonContentDescription;
    private final String repeatAllButtonContentDescription;

    private VisibilityListener visibilityListener;

    private boolean isAttachedToWindow;
    private int rewindMs;
    private int fastForwardMs;
    private int showTimeoutMs;
    private @RepeatModeUtil.RepeatToggleModes int repeatToggleModes;
    private boolean showShuffleButton;
    private long hideAtMs;

    boolean enablePrevious = false;
    boolean enableNext = false;
    boolean playState = false;


    public PlayBackControlCustom(Context context) {
        this(context, null);
    }

    public PlayBackControlCustom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayBackControlCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, attrs);
    }

    public PlayBackControlCustom(
            Context context, AttributeSet attrs, int defStyleAttr, AttributeSet playbackAttrs) {
        super(context, attrs, defStyleAttr);
        int controllerLayoutId = R.layout.playback_ui_custom;
        rewindMs = DEFAULT_REWIND_MS;
        fastForwardMs = DEFAULT_FAST_FORWARD_MS;
        showTimeoutMs = DEFAULT_SHOW_TIMEOUT_MS;
        repeatToggleModes = DEFAULT_REPEAT_TOGGLE_MODES;
        hideAtMs = C.TIME_UNSET;
        showShuffleButton = false;
        if (playbackAttrs != null) {
            TypedArray a =
                    context
                            .getTheme()
                            .obtainStyledAttributes(playbackAttrs, R.styleable.PlayerControlView, 0, 0);
            try {
                rewindMs = a.getInt(R.styleable.PlayerControlView_rewind_increment, rewindMs);
                fastForwardMs =
                        a.getInt(R.styleable.PlayerControlView_fastforward_increment, fastForwardMs);
                showTimeoutMs = a.getInt(R.styleable.PlayerControlView_show_timeout, showTimeoutMs);
                controllerLayoutId =
                        a.getResourceId(R.styleable.PlayerControlView_controller_layout_id, controllerLayoutId);
                repeatToggleModes = getRepeatToggleModes(a, repeatToggleModes);
                showShuffleButton =
                        a.getBoolean(R.styleable.PlayerControlView_show_shuffle_button, showShuffleButton);
            } finally {
                a.recycle();
            }
        }
        period = new Timeline.Period();
        window = new Timeline.Window();
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());

        LayoutInflater.from(context).inflate(controllerLayoutId, this);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        durationView = findViewById(R.id.playback_duration);
        positionView = findViewById(R.id.playback_position);
        timeBar = findViewById(R.id.playback_progress);

        playButton = findViewById(R.id.playback_play);

        pauseButton = findViewById(R.id.playback_pause);

        previousButton = findViewById(R.id.playback_prev);

        nextButton = findViewById(R.id.playback_next);

        repeatToggleButton = findViewById(R.id.playback_repeat_toggle);

        shuffleButton = findViewById(R.id.playback_shuffle);

        trackName = findViewById(R.id.playback_track_name);
        trackName.setSelected(true);

        Resources resources = context.getResources();
        repeatOffButtonDrawable = resources.getDrawable(R.drawable.exo_controls_repeat_off);
        repeatOneButtonDrawable = resources.getDrawable(R.drawable.exo_controls_repeat_one);
        repeatAllButtonDrawable = resources.getDrawable(R.drawable.exo_controls_repeat_all);
        repeatOffButtonContentDescription =
                resources.getString(R.string.exo_controls_repeat_off_description);
        repeatOneButtonContentDescription =
                resources.getString(R.string.exo_controls_repeat_one_description);
        repeatAllButtonContentDescription =
                resources.getString(R.string.exo_controls_repeat_all_description);
    }

    @SuppressWarnings("ResourceType")
    private static @RepeatModeUtil.RepeatToggleModes int getRepeatToggleModes(
            TypedArray a, @RepeatModeUtil.RepeatToggleModes int repeatToggleModes) {
        return a.getInt(R.styleable.PlayerControlView_repeat_toggle_modes, repeatToggleModes);
    }

    public void setVisibilityListener(VisibilityListener listener) {
        this.visibilityListener = listener;
    }

    public void setRewindIncrementMs(int rewindMs) {
        this.rewindMs = rewindMs;
        updateNavigation();
    }

    public void setFastForwardIncrementMs(int fastForwardMs) {
        this.fastForwardMs = fastForwardMs;
        updateNavigation();
    }

    public @RepeatModeUtil.RepeatToggleModes int getRepeatToggleModes() {
        return repeatToggleModes;
    }

    public boolean getShowShuffleButton() {
        return showShuffleButton;
    }

    public void setShowShuffleButton(boolean showShuffleButton) {
        this.showShuffleButton = showShuffleButton;
        updateShuffleButton();
    }

     public void show() {
        if (!isVisible()) {
            setVisibility(VISIBLE);
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            updateAll();
        }
    }

    public void hide() {
        if (isVisible()) {
            setVisibility(GONE);
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            hideAtMs = C.TIME_UNSET;
        }
    }

    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    private void updateAll() {
        updatePlayPauseButton();
        updateNavigation();
        updateRepeatModeButton();
        updateShuffleButton();
    }

    private void updatePlayPauseButton() {
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }
        if (playButton != null && pauseButton != null) {
            if(playState) {
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
            }
            else{
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
            }
        }
    }

    private void updateNavigation() {
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }

        boolean isSeekable = true;
        setButtonEnabled(enablePrevious, previousButton);
        setButtonEnabled(enableNext, nextButton);
        if (timeBar != null) {
            timeBar.setEnabled(isSeekable);
        }
    }

    private void updateRepeatModeButton() {
        if (!isVisible() || !isAttachedToWindow || repeatToggleButton == null) {
            return;
        }
        if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE) {
            repeatToggleButton.setVisibility(View.GONE);
            return;
        }

        setButtonEnabled(true, repeatToggleButton);
        repeatToggleButton.setVisibility(View.VISIBLE);
    }

    private void updateShuffleButton() {
        if (!isVisible() || !isAttachedToWindow || shuffleButton == null) {
            return;
        }
        if (!showShuffleButton) {
            shuffleButton.setVisibility(View.GONE);
        } else {
            shuffleButton.setEnabled(true);
            shuffleButton.setVisibility(View.VISIBLE);
        }
    }

    private void setButtonEnabled(boolean enabled, View view) {
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
        view.setAlpha(enabled ? 1f : 0.3f);
        view.setVisibility(VISIBLE);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
        updateAll();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
    }

    public void setEnabledPrevius(boolean enabled){
        enablePrevious = enabled;
        updateNavigation();
    }

    public void setEnabledNext(boolean enabled){
        enableNext = enabled;
        updateNavigation();
    }

    public void setPlayState(boolean state){
        playState = state;
        updateAll();
    }

    public void setDurationBar(long duration){
        timeBar.setDuration(duration);
        durationView.setText(Util.getStringForTime(formatBuilder, formatter, duration));
    }

    public void setPositionBar(long position){
        timeBar.setPosition(position);
        positionView.setText(Util.getStringForTime(formatBuilder, formatter, position));
    }

    public void setPlayButtonClickListener(OnClickListener buttonClickListener){
        playButton.setOnClickListener(buttonClickListener);
    }

    public void setPauseButtonClickListener(OnClickListener buttonClickListener){
        pauseButton.setOnClickListener(buttonClickListener);
    }

    public void setPrevButtonClickListener(OnClickListener buttonClickListener){
        previousButton.setOnClickListener(buttonClickListener);
    }

    public void setNextButtonClickListener(OnClickListener buttonClickListener){
        nextButton.setOnClickListener(buttonClickListener);
    }

    public void setScrollListener(TimeBar.OnScrubListener scrollListener){
        timeBar.addListener(scrollListener);
    }

    public void setTrackName(String name){
        trackName.setText(name);
    }
}
