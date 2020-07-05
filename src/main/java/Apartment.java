import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Apartment {
    int id;
    String site;
    String title;
    float price;
    float area;
    int rooms;
    LocalDate postDate;
    String url;
    LocalDateTime addDate;

    public Apartment(int id, String site, String title, float price, float area, int rooms, LocalDate postDate, String url, LocalDateTime addDate) {
        this.id = id;
        this.site = site;
        this.title = title;
        this.price = price;
        this.area = area;
        this.rooms = rooms;
        this.postDate = postDate;
        this.url = url;
        this.addDate = addDate;
    }

    public Apartment() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public LocalDate getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDate postDate) {
        this.postDate = postDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getAddDate() {
        return addDate;
    }

    public void setAddDate(LocalDateTime addDate) {
        this.addDate = addDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Apartment apartment = (Apartment) o;
        return id == apartment.id &&
                Double.compare(apartment.price, price) == 0 &&
                Double.compare(apartment.area, area) == 0 &&
                rooms == apartment.rooms &&
                site.equals(apartment.site) &&
                title.equals(apartment.title) &&
                postDate.equals(apartment.postDate) &&
                url.equals(apartment.url) &&
                addDate.equals(apartment.addDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, site, title, price, area, rooms, postDate, url, addDate);
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "id=" + id +
                ", site='" + site + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", area=" + area +
                ", rooms=" + rooms +
                ", postDate=" + postDate +
                ", url='" + url + '\'' +
                ", addDate=" + addDate +
                '}';
    }
}
