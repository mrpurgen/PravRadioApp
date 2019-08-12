package eugenzh.ru.pravradioapp.Models.Item;

final public class Category extends Item{
    public Category(String name, String url) {
        this.setId(0L);
        this.setName(name);
        this.setUrl(url);
    }

    public Category(String name, long id) {
        this.setId(id);
        this.setName(name);
    }

    public Category(String name) {
        this.setId(0L);
        this.setName(name);
    }

    public Category(long id, String name, String url) {
        this.setId(id);
        this.setName(name);
        this.setUrl(url);
    }
}
