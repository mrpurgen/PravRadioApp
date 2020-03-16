package eugenzh.ru.pravradiopodcast.View;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import eugenzh.ru.pravradiopodcast.Presenters.MainViewPresenter;
import eugenzh.ru.pravradiopodcast.R;
import eugenzh.ru.pravradiopodcast.View.ViewPager.ViewPagerAdapter;

public class MainActivity extends MainBaseActivity<MainViewPresenter> implements MainView{


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private Toolbar toolbar;
    PlayerControlCustomView playerControl;

    @InjectPresenter
    MainViewPresenter presenter;

    @ProvidePresenter
    MainViewPresenter providerPresenter(){
        return new MainViewPresenter(getApplicationContext());
    }

    @Override
    void createMenuInflate(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_category_menu, menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createCommonPresenter(presenter);
        setCurrentTheme();

        setContentView(R.layout.activity_main);

        playerControl = findViewById(R.id.player_ctrl_view);
        playerControl.init(getMvpDelegate());

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        toolbar = findViewById(R.id.toolbar);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkTheme();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.toolbar_settings){
            presenter.settingsChanged();
        }

        return true;
    }

    @Override
    public void showSettingsView()
    {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }
}
