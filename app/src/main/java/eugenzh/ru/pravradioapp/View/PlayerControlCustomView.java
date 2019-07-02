/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eugenzh.ru.pravradioapp.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.RepeatModeUtil;
import com.google.android.exoplayer2.util.Util;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;

import eugenzh.ru.pravradioapp.Presenters.PlayerControlPresenter;
import eugenzh.ru.pravradioapp.R;

public class PlayerControlCustomView extends FrameLayout implements PlayerControlView{

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
  boolean isSeekable = false;

  private MvpDelegate parentDelegat;
  private MvpDelegate<PlayerControlCustomView> delegat;

  @InjectPresenter
  PlayerControlPresenter presenter;

  @ProvidePresenter
  PlayerControlPresenter providePresenter(){
    return new PlayerControlPresenter(getContext().getApplicationContext());
  }

  public PlayerControlCustomView(Context context) {
    this(context, null);
  }

  public PlayerControlCustomView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PlayerControlCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, attrs);
  }

  public PlayerControlCustomView(
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
    timeBar.addListener(new TimeBar.OnScrubListener() {
      @Override
      public void onScrubStart(TimeBar timeBar, long position) {
        presenter.onStartScroll();
      }

      @Override
      public void onScrubMove(TimeBar timeBar, long position) {
        presenter.onSetPosition(position);
      }

      @Override
      public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
        presenter.onStopScroll(position);
      }
    });

    playButton = findViewById(R.id.playback_play);
    playButton.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            presenter.onPlayPressed();
        }
    });

    pauseButton = findViewById(R.id.playback_pause);
    pauseButton.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View view) {
        presenter.onPausePressed();
      }
    });

    previousButton = findViewById(R.id.playback_prev);
    previousButton.setOnClickListener(new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            presenter.onPrevPressed();
        }
    });

    nextButton = findViewById(R.id.playback_next);
    nextButton.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            presenter.onNextPressed();
        }
    });

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

  public void init(MvpDelegate parent){
      parentDelegat = parent;

      getMvpDelegate().onCreate();
      getMvpDelegate().onAttach();
  }

    public MvpDelegate<PlayerControlCustomView> getMvpDelegate() {
        if (delegat != null) {
            return delegat;
        }

        delegat = new MvpDelegate<>(this);
        delegat.setParentDelegate(parentDelegat, String.valueOf(getId()));
        return delegat;
    }

    @Override
    protected void detachViewFromParent(View child) {
        super.detachViewFromParent(child);

        getMvpDelegate().onSaveInstanceState();
        getMvpDelegate().onDetach();
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

  public int getShowTimeoutMs() {
    return showTimeoutMs;
  }

  public @RepeatModeUtil.RepeatToggleModes int getRepeatToggleModes() {
    return repeatToggleModes;
  }


  /** Returns whether the shuffle button is shown. */
  public boolean getShowShuffleButton() {
    return showShuffleButton;
  }

  /**
   * Sets whether the shuffle button is shown.
   *
   * @param showShuffleButton Whether the shuffle button is shown.
   */
  public void setShowShuffleButton(boolean showShuffleButton) {
    this.showShuffleButton = showShuffleButton;
    updateShuffleButton();
  }

  /**
   * Shows the playback controls. If {@link #getShowTimeoutMs()} is positive then the controls will
   * be automatically hidden after this duration of time has elapsed without user input.
   */
  public void show() {
    if (!isVisible()) {
      setVisibility(VISIBLE);
      if (visibilityListener != null) {
        visibilityListener.onVisibilityChange(getVisibility());
      }
      updateAll();
    }
  }

  /** Hides the controller. */
    public void hide() {
        if (isVisible()) {
            setVisibility(GONE);
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            hideAtMs = C.TIME_UNSET;
        }
    }

  /** Returns whether the controller is currently visible. */
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

  @Override
  public void playView() {
    playState = true;
    enablePrevious = true;
    enableNext = true;
    isSeekable = true;
    updateAll();
  }

  @Override
  public void pauseView() {
    playState = false;
    updateAll();
  }

  @Override
    public void setTrackName(String trackName) {
        this.trackName.setText(trackName);
    }

  @Override
  public void setTrackDuration(long duration) {
    timeBar.setDuration(duration);
    durationView.setText(Util.getStringForTime(formatBuilder, formatter, duration));
  }

  @Override
  public void setTrackPosition(long position) {
    timeBar.setPosition(position);
    positionView.setText(Util.getStringForTime(formatBuilder, formatter, position));
  }

  @Override
  public void hidePanel() {
    hide();
  }

  @Override
  public void showPanel() {
    show();
  }

  @Override
  public void setPosiotionProgressBar(long position) {
    timeBar.setPosition(position);
    positionView.setText(Util.getStringForTime(formatBuilder, formatter, position));
  }
}
