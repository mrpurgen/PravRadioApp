package eugenzh.ru.pravradiopodcast.Models.DataStore;

import eugenzh.ru.pravradiopodcast.Common.TypeSourceItems;

public class CategoriesStoreFactory {
    public static DataStoreCategory getCategories(TypeSourceItems typeSource){
        if(typeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
            return CategoriesMemoryStoreSinglton.getInstance();
        }
        else if(typeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            return CategoriesServerStoreSinglton.getInstance();
        }
        /// TODO: add throw exception
        return null;
    }
}
