package eugenzh.ru.pravradioapp.Models.DataView;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;

public class CategoriesDateViewFactory {
    public static DateViewCategory getCategories(TypeSourceItems typeSource){
        if(typeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
            return CategoriesMemorySinglton.getInstance();
        }
        else if(typeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            return CategoriesServerSinglton.getInstance();
        }
        /// TODO: add throw exception
        return null;
    }
}
