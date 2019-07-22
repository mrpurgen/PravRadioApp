package eugenzh.ru.pravradioapp.Common;

import java.util.HashMap;
import java.util.Map;

public enum TypeSourceItems {
    TYPE_SOURCE_UNDEFINED(0),
    TYPE_SOURCE_ITEMS_SERVER(1),
    TYPE_SOURCE_ITEMS_MEMORY(2);

    private final int value;
    private static Map valueMap = new HashMap(3);

    TypeSourceItems(int value){
        this.value = value;
    }

    static {
        for(TypeSourceItems type: TypeSourceItems.values()){
            valueMap.put(type.value, type);
        }
    }

    public static TypeSourceItems valueOf(int typeSource){
        return (TypeSourceItems)valueMap.get(typeSource);
    }

    public int getValue(){
        return value;
    }
}
