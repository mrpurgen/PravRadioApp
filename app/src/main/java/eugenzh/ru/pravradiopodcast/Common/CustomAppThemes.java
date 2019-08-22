package eugenzh.ru.pravradiopodcast.Common;

import java.util.HashMap;
import java.util.Map;

public enum CustomAppThemes {

    DEFAULT_THEME(0),
    DARK_THEME(1);

    private int value;
    private static Map valueMap = new HashMap(2);

    CustomAppThemes(int value){
        this.value = value;
    }

    static {
        for (CustomAppThemes theme: CustomAppThemes.values()){
            valueMap.put(theme.value, theme);
        }
    }

    public static  CustomAppThemes valueOf(int theme){
        return (CustomAppThemes)valueMap.get(theme);
    }

    public int getValue(){
        return value;
    }
}
