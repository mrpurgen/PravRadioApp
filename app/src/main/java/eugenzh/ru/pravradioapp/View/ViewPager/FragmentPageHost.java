package eugenzh.ru.pravradioapp.View.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.lang.reflect.Type;
import java.util.List;

import eugenzh.ru.pravradioapp.Common.TypeItems;
import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Models.DataView.CategoriesDateViewFactory;
import eugenzh.ru.pravradioapp.Models.DataView.CategoriesServerSinglton;
import eugenzh.ru.pravradioapp.Models.DataView.DateViewCategory;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewSubject;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.SelectedItemObserver;
import eugenzh.ru.pravradioapp.Presenters.ItemViewPresenter;
import eugenzh.ru.pravradioapp.Presenters.ItemViewPresenterFactory;
import eugenzh.ru.pravradioapp.Presenters.PageHostPresenter;
import eugenzh.ru.pravradioapp.R;
import eugenzh.ru.pravradioapp.View.FragmentList.FragmentList;
import eugenzh.ru.pravradioapp.View.ListActivity;


public class FragmentPageHost  extends MvpAppCompatFragment implements SelectedItemObserver, PageHostView {
    final static private String TAG_FRAGMENT_SERVER_DATA = "TAG_FRAGMENT_SERVER_DATA";
    final static private String TAG_FRAGMENT_MEMORY_DATA = "TAG_FRAGMENT_MEMORY_DATA";
    TypeSourceItems resourseType;

    @InjectPresenter
    PageHostPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        resourseType = (TypeSourceItems) bundle.getSerializable("RESOURSE_TYPE");

        DateViewSubject subject = CategoriesDateViewFactory.getCategories(resourseType);
        subject.subscripEventUpdateSelectedItem(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        DateViewSubject subject = CategoriesDateViewFactory.getCategories(resourseType);
        subject.unsubscripEventUpdateSelectedItem(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String tag;

        if (resourseType == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
            tag = TAG_FRAGMENT_MEMORY_DATA;
        }
        else if (resourseType == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            tag = TAG_FRAGMENT_SERVER_DATA;
        }
        else{
            tag = "TAG_UNKNOW_FRAGMENT";
        }

        FragmentManager fragmentManager = getFragmentManager();

        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment ==  null){
            fragment = new FragmentList();
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        bundle.putSerializable("TYPE_ITEM", TypeItems.TYPE_ITEM_CATEGORY);

        if (resourseType == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
            fragmentTransaction.replace(R.id.view_page_memory, fragment, tag);
        }
        else if(resourseType == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            fragmentTransaction.replace(R.id.view_page_server, fragment, tag);
        }

        fragment.setArguments(bundle);
        fragmentTransaction.commit();

        if (resourseType == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
            return inflater.inflate(R.layout.view_page_memory, container, false);
        }
        else if(resourseType == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            return inflater.inflate(R.layout.view_page_server, container, false);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void update(long selectedItemdID) {
        Fragment fragment = FragmentList.newInstance(TypeItems.TYPE_ITEM_PODCAST, resourseType);
        int viewID = 0;

        if (resourseType == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            viewID = R.id.view_page_server;
        }
        else if (resourseType == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
            viewID = R.id.view_page_memory;
        }
        else{
            viewID = R.id.view_page_server;
        }

        Intent intent = ListActivity.newInstance(getContext(), resourseType, TypeItems.TYPE_ITEM_PODCAST);
        startActivity(intent);
    }
}
