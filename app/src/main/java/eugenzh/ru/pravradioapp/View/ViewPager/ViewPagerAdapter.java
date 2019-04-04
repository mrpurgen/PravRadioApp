package eugenzh.ru.pravradioapp.View.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    final private int PAGE_COUNT = 2;
    final static public int PAGE_AUDIOARCHIVE = 0;
    final static public int PAGE_DOWNLOADED = 1;

    private String tabTitles[] = new String[] { "Аудиоархив", "Загружено"};

    public ViewPagerAdapter(FragmentManager fm, Context ctx){
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return createFragment(i);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    private Fragment createFragment(int i) {
        Bundle bundle = new Bundle();
        Fragment fragment = new FragmentPageHost();

        if (i == PAGE_AUDIOARCHIVE) {
            bundle.putSerializable("RESOURSE_TYPE", TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER);
        } else if (i == PAGE_DOWNLOADED) {
            bundle.putSerializable("RESOURSE_TYPE", TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY);
        }
        fragment.setArguments(bundle);
        return fragment;
    }
}
