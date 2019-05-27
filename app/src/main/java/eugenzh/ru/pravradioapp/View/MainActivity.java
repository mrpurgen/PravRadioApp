package eugenzh.ru.pravradioapp.View;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import eugenzh.ru.pravradioapp.Models.DataView.Observer.SelectedItemObserver;
import eugenzh.ru.pravradioapp.Presenters.MainViewPresenter;
import eugenzh.ru.pravradioapp.R;
import eugenzh.ru.pravradioapp.View.ViewPager.ViewPagerAdapter;

public class MainActivity extends MvpAppCompatActivity implements MainView{
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    Toolbar toolbar;

    @InjectPresenter
    MainViewPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        toolbar = findViewById(R.id.toolbar);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        setSupportActionBar(toolbar);
    }
}
