package eugenzh.ru.pravradioapp.View;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.arellomobile.mvp.MvpAppCompatActivity;

import eugenzh.ru.pravradioapp.Presenters.MainBasePresenter;
import eugenzh.ru.pravradioapp.R;

abstract public class MainBaseActivity<T extends MainBasePresenter> extends MvpAppCompatActivity implements SearchView.OnQueryTextListener{
    private final String TAG_BUNDLE_TEXT_QUERY_SEARCH = "TAG_TEXT_QUERY_SEARCH ";

    private SearchView searchView;
    private String textQuery;

    protected MainBasePresenter commonPresenter;

    void createCommonPresenter(MainBasePresenter presenter){
        commonPresenter = presenter;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TAG_BUNDLE_TEXT_QUERY_SEARCH, textQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            textQuery = savedInstanceState.getString(TAG_BUNDLE_TEXT_QUERY_SEARCH);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.toolbar_search);
        searchView = (SearchView)menuItem.getActionView();

        if(!TextUtils.isEmpty(textQuery)){
            menuItem.expandActionView();
            searchView.setQuery(textQuery, false);
            searchView.clearFocus();
        }

        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        textQuery = s;
        commonPresenter.filter(textQuery);
        return false;
    }
}
