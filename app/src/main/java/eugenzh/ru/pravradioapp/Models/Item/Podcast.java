package eugenzh.ru.pravradioapp.Models.Item;

import java.sql.Date;

final public class Podcast extends Item{
    private Date date = new Date(0L);
    private Long categoryId;

    public Podcast(Long id, Long categoryId, String name, Date date, String url) {
        this.setId(id);
        this.categoryId = categoryId;
        this.setName(name);
        this.setDate(date);
        this.setUrl(url);
    }

    public Podcast(Long categoryId, String name, Date date, String url) {
        this.setId(0L);
        this.categoryId = categoryId;
        this.setName(name);
        this.setDate(date);
        this.setUrl(url);
    }

    public Podcast(String name) {
        this.setName(name);
    }

    public Podcast(Long id, String name){
        this.setId(id);
        this.setName(name);
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId){
        this.categoryId = categoryId;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
}
