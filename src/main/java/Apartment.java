import java.time.LocalDate;
import java.util.Objects;

public class Apartment {
    int id;
    String site;
    String title;
    double price;
    double area;
    int rooms;
    LocalDate postDate;
    String url;

    public Apartment(int id, String site, String title, double price, double area, int rooms, LocalDate postDate, String url) {
        this.id = id;
        this.site = site;
        this.title = title;
        this.price = price;
        this.area = area;
        this.rooms = rooms;
        this.postDate = postDate;
        this.url = url;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
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
                url.equals(apartment.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, site, title, price, area, rooms, postDate, url);
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
                '}';
    }
}
