package eugenzh.ru.pravradioapp.View.FragmentList;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.ArrayList;
import java.util.List;

import eugenzh.ru.pravradioapp.Common.TypeItems;
import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Models.Item.Category;
import eugenzh.ru.pravradioapp.Models.Item.Item;
import eugenzh.ru.pravradioapp.Models.Item.Podcast;
import eugenzh.ru.pravradioapp.Presenters.ItemViewPresenter;
import eugenzh.ru.pravradioapp.Presenters.ItemViewPresenterFactory;
import eugenzh.ru.pravradioapp.R;

public class FragmentList extends MvpAppCompatFragment implements ItemView, SwipeRefreshLayout.OnRefreshListener {

    TypeItems typeItem;
    TypeSourceItems typeSourceITems;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    FragmentListAdapter recyclerAdapter;

    @InjectPresenter
    ItemViewPresenter presenter;

    @ProvidePresenter
    ItemViewPresenter providePresenter(){
        return ItemViewPresenterFactory.getPresenter(typeItem, typeSourceITems);
    }

    public static FragmentList newInstance(TypeItems typeItem, TypeSourceItems typeSource){
        Bundle bundle = new Bundle();
        bundle.putSerializable("TYPE_ITEM", typeItem);
        bundle.putSerializable("RESOURSE_TYPE", typeSource);

        FragmentList fragmentList = new FragmentList();
        fragmentList.setArguments(bundle);

        return fragmentList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        typeItem = (TypeItems) bundle.getSerializable("TYPE_ITEM");
        typeSourceITems = (TypeSourceItems) bundle.getSerializable("RESOURSE_TYPE");

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.recyclerview_divider);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(drawable);

        recyclerView = view.findViewById(R.id.recycler_list);
        progressBar = view.findViewById(R.id.progress_bar_status_load_item);
        swipeRefresh = view.findViewById(R.id.swipe_refresh_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAdapter = new FragmentListAdapter();
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
        recyclerView.addItemDecoration(dividerItemDecoration);

        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(R.color.colorRedMaterial);

        return view;
    }

    @Override
    public void updateList(List<Item> list) {
        recyclerAdapter.setItemsList(list);
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showWaitLoad() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideWaitLoad() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(false);
        presenter.updateContent();
    }

    class FragmentListCategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView categoryTitle;

         FragmentListCategoryHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.category_list_item, parent, false));

            itemView.setOnClickListener(this);

            categoryTitle = itemView.findViewById(R.id.category_name);
        }

        void setCategoryTitle(String title){
            categoryTitle.setText(title);
        }

        @Override
        public void onClick(View view) {
             presenter.onClick(getLayoutPosition());
        }
    }

     class FragmentListPodcastHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView podcastTitle;
        TextView podcastDate;

         FragmentListPodcastHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.podcast_list_item, parent, false));

             itemView.setOnClickListener(this);

            podcastDate = itemView.findViewById(R.id.podcast_date);
            podcastTitle = itemView.findViewById(R.id.podcast_title);
        }

         void setPodcastTitle(String title){
            podcastTitle.setText(title);
        }

         void setPodcastDate(String date){
            podcastDate.setText(date);
        }

         @Override
         public void onClick(View view) {
             presenter.onClick(getLayoutPosition());
         }
     }

     class FragmentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<Item> items = new ArrayList<>();

        private final int CATEGORY = 0;
        private final int PODCAST = 1;

        void setItemsList(List<Item> items){
            this.items = items;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            RecyclerView.ViewHolder viewHolder;
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

            switch (i){
                case CATEGORY:
                    viewHolder = new FragmentListCategoryHolder(inflater, viewGroup);
                    break;
                case PODCAST:
                    viewHolder = new FragmentListPodcastHolder(inflater, viewGroup);
                    break;
                default:
                    viewHolder = new FragmentListCategoryHolder(inflater, viewGroup);
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            switch (viewHolder.getItemViewType()){
                case CATEGORY:
                    FragmentListCategoryHolder ch = (FragmentListCategoryHolder) viewHolder;
                    configureCategoryViewHolder(ch, i);
                    break;
                case PODCAST:
                    FragmentListPodcastHolder ph = (FragmentListPodcastHolder) viewHolder;
                    configurePodcastViewHolder(ph, i);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (items.get(position) instanceof Category){
                return CATEGORY;
            }
            else if (items.get(position) instanceof Podcast){
                return PODCAST;
            }
            return -1;
        }

        private void configureCategoryViewHolder(FragmentListCategoryHolder ch, int position){
            Category category = (Category) items.get(position);
            ch.setCategoryTitle(category.getName());
        }

        private void configurePodcastViewHolder(FragmentListPodcastHolder ph, int position){
            Podcast podcast = (Podcast) items.get(position);
            ph.setPodcastDate(podcast.getDate().toString());
            ph.setPodcastTitle(podcast.getName());
        }
    }
}