package eugenzh.ru.pravradioapp.View.FragmentList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentList extends MvpAppCompatFragment implements ItemView {

    TypeItems typeItem;
    TypeSourceItems typeSourceITems;

    @InjectPresenter
    ItemViewPresenter presenter;

    @ProvidePresenter
    ItemViewPresenter providePresenter(){
        return ItemViewPresenterFactory.getPresenter(typeItem);
    }

    RecyclerView recyclerView;
    FragmentListAdapter recyclerAdapter;



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

        recyclerView = view.findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerAdapter = new FragmentListAdapter();
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onClick(long duration) {

    }

    @Override
    public void updateList(List<Item> list) {
        recyclerAdapter.setItemsList(list);
        recyclerAdapter.notifyDataSetChanged();
    }

    class FragmentListCategoryHolder extends RecyclerView.ViewHolder{
        TextView categoryTitle;

         FragmentListCategoryHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.category_list_item, parent, false));

            categoryTitle = itemView.findViewById(R.id.category_name);
        }

        void setCategoryTitle(String title){
            categoryTitle.setText(title);
        }
    }

     class FragmentListPodcastHolder extends RecyclerView.ViewHolder{
        TextView podcastTitle;
        TextView podcastDate;

         FragmentListPodcastHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.podcast_list_item, parent, false));

            podcastDate = itemView.findViewById(R.id.podcast_date);
            podcastTitle = itemView.findViewById(R.id.podcast_title);
        }

         void setPodcastTitle(String title){
            podcastTitle.setText(title);
        }

         void setPodcastDate(String date){
            podcastDate.setText(date);
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
