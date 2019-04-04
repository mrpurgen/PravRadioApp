package eugenzh.ru.pravradioapp.Models.DataView;

import java.util.ArrayList;
import java.util.List;

import eugenzh.ru.pravradioapp.Models.Item.Item;

abstract public class DataView<T extends Item> {
    List<T> items;

    public List<T> getItems(){
        return items;
    }
}
