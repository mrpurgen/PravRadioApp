package eugenzh.ru.pravradioapp.View.ViewPager;

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

import eugenzh.ru.pravradioapp.Common.TypeItems;
import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Models.DataView.CategoriesServerSinglton;
import eugenzh.ru.pravradioapp.Models.DataView.DateViewCategory;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewSubject;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.SelectedItemObserver;
import eugenzh.ru.pravradioapp.Presenters.ItemViewPresenter;
import eugenzh.ru.pravradioapp.Presenters.ItemViewPresenterFactory;
import eugenzh.ru.pravradioapp.R;
import eugenzh.ru.pravradioapp.View.FragmentList.FragmentList;


public class FragmentPageHost  extends MvpAppCompatFragment implements SelectedItemObserver {
    TypeSourceItems resourseType;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        resourseType = (TypeSourceItems) bundle.getSerializable("RESOURSE_TYPE");

        if (resourseType == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            DateViewSubject subject = CategoriesServerSinglton.getInstance();
            subject.subscripEventUpdateSelectedItem(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (resourseType == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            DateViewSubject subject = CategoriesServerSinglton.getInstance();
            subject.unsubscripEventUpdateSelectedItem(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        Fragment fragment = new FragmentList();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (resourseType == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
            bundle.putSerializable("TYPE_ITEM", TypeItems.TYPE_ITEM_PODCAST);
            fragmentTransaction.replace(R.id.view_page_memory, fragment);
        }
        else if(resourseType == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            bundle.putSerializable("TYPE_ITEM", TypeItems.TYPE_ITEM_CATEGORY);
            fragmentTransaction.replace(R.id.view_page_server, fragment);
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
        Bundle bundle = new Bundle();
        bundle.putSerializable("TYPE_ITEM", TypeItems.TYPE_ITEM_PODCAST);

        Fragment fragment = new FragmentList();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.view_page_server, fragment)
                .addToBackStack(null)
                .commit();
    }
}
