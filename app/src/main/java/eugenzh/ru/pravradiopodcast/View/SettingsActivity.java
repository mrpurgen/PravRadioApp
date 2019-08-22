package eugenzh.ru.pravradiopodcast.View;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import eugenzh.ru.pravradiopodcast.Common.CustomAppThemes;
import eugenzh.ru.pravradiopodcast.Models.Settings.SettingsViews;
import eugenzh.ru.pravradiopodcast.Presenters.SettingsPresenter;
import eugenzh.ru.pravradiopodcast.R;

public class SettingsActivity extends MvpAppCompatActivity implements SettingsView, SwitchCompat.OnCheckedChangeListener {


    @InjectPresenter
    SettingsPresenter mPresenter;

    Toolbar mToolbar;
    SwitchCompat mDarkThemeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCustomTheme();

        setContentView(R.layout.settings_layout);

        mToolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle(R.string.settingsTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDarkThemeSwitch = findViewById(R.id.darkThemeSwitch);

        mDarkThemeSwitch.setOnCheckedChangeListener(this);

        setStateSwitch();
    }

    private void setCustomTheme(){
        CustomAppThemes theme = mPresenter.getCurrentTheme(getApplicationContext());

        if (theme == CustomAppThemes.DARK_THEME){
            setTheme(R.style.DarkTheme);
        }

        else if (theme == CustomAppThemes.DEFAULT_THEME){
            setTheme(R.style.DefaultTheme);
        }
    }

    private void setStateSwitch(){
        CustomAppThemes theme = mPresenter.getCurrentTheme(getApplicationContext());

        if (theme == CustomAppThemes.DARK_THEME){
            mDarkThemeSwitch.setChecked(true);
        }
        else{
            mDarkThemeSwitch.setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();

        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mPresenter.switchDarkTheme(b, getApplicationContext());
    }

    @Override
    public void updateTheme() {
        recreate();
    }
}
