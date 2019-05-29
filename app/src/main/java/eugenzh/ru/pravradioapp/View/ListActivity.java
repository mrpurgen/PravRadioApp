package eugenzh.ru.pravradioapp.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.arellomobile.mvp.presenter.InjectPresenter;

import eugenzh.ru.pravradioapp.Common.TypeItems;
import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Presenters.ListViewPresenter;
import eugenzh.ru.pravradioapp.R;
import eugenzh.ru.pravradioapp.View.FragmentList.FragmentList;


public class ListActivity extends MainBaseActivity<ListViewPresenter> implements ListView{
    final static private String TAG_FRAGMENT_PODCAST = "TAG_FRAGMENT_PODCAST";

    Toolbar toolBar;

    @InjectPresenter
    ListViewPresenter presenter;

    @Override
    void createMenuInflate(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_podcast_menu, menu);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        Fragment fragment;
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("BUNDLE");

        TypeItems typeItems = (TypeItems) bundle.getSerializable("TYPE_ITEMS");
        TypeSourceItems typeSourceItems = (TypeSourceItems)bundle.getSerializable("TYPE_SOURCE");

        presenter.setTypeSource(typeSourceItems);
        createCommonPresenter(presenter);

        toolBar = findViewById(R.id.toolbar_list_activity);
        setSupportActionBar(toolBar);

        setTitle(typeSourceItems);
        displayBackButton();

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_PODCAST);

        if (fragment == null){
            fragment = FragmentList.newInstance(typeItems, typeSourceItems);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.list_activity, fragment, TAG_FRAGMENT_PODCAST);
            fragmentTransaction.commit();
        }
    }


    public static Intent newInstance(Context ctx, TypeSourceItems typeSource, TypeItems typeItems){
        Bundle bundle = new Bundle();
        bundle.putSerializable("TYPE_SOURCE", typeSource);
        bundle.putSerializable("TYPE_ITEMS", typeItems);

        Intent intent = new Intent(ctx, ListActivity.class);
        intent.putExtra("BUNDLE", bundle);

        return intent;
    }

    private void setTitle(TypeSourceItems typeSource){
        String title = presenter.getNameCategoryView();
        getSupportActionBar().setTitle(title);
    }

    private void displayBackButton(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.swap){
            presenter.reverseListItems();
        }
        else if (item.getItemId() != R.id.toolbar_search){
            super.onBackPressed();
        }
        return true;
    }
}
