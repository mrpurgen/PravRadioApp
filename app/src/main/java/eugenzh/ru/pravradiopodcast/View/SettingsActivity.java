package eugenzh.ru.pravradiopodcast.View;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import eugenzh.ru.pravradiopodcast.Presenters.SettingsPresenter;
import eugenzh.ru.pravradiopodcast.R;

public class SettingsActivity extends MvpAppCompatActivity implements SettingsView {

    @InjectPresenter
    SettingsPresenter mPresenter;

    Toolbar mToolbar;
    SwitchCompat mDarkThemeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        mToolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Настройки");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDarkThemeSwitch = findViewById(R.id.darkThemeSwitch);

        mDarkThemeSwitch.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                   recreate();
                }
                else{
                    CustomToast.showMessage(getApplicationContext(), "CHECKED FALSE");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();

        return true;
    }
}
