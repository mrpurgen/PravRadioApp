package eugenzh.ru.pravradiopodcast.Models.Item;

import java.util.Objects;

abstract public class Item {
    private Long id = 0L;
    private String name = "";
    private String url = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Item model = (Item)obj;

        return Objects.equals(id, model.id);
    }
}
