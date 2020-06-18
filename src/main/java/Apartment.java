import java.util.Objects;

public class Apartment {
    String flatName;
    int price;
    int area;
    String postDate;
    String url;

    public Apartment(String flatName, int price, int area, String postDate, String url) {
        this.flatName = flatName;
        this.price = price;
        this.area = area;
        this.postDate = postDate;
        this.url = url;
    }

    public String getFlatName() {
        return flatName;
    }

    public void setFlatName(String flatName) {
        this.flatName = flatName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
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
        return price == apartment.price &&
                area == apartment.area &&
                flatName.equals(apartment.flatName) &&
                Objects.equals(postDate, apartment.postDate) &&
                url.equals(apartment.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flatName, price, area, postDate, url);
    }
}
