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

import java.lang.reflect.Type;

import eugenzh.ru.pravradioapp.Common.TypeItems;
import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.R;
import eugenzh.ru.pravradioapp.View.FragmentList.FragmentList;

public class FragmentPageHost  extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        TypeSourceItems resourseType = (TypeSourceItems) bundle.getSerializable("RESOURSE_TYPE");

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
}
