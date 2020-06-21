import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlMeta;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Scraper {
    public static int newResults(HtmlPage page, int startId, String site, String resultsXPath, String anchorXPath, String titleXpath,
                           String priceXPath, String areaXPath, String roomsXPath, String postDateXPath) {

        LocalDate currentDate = LocalDate.now();
        LocalDate previousDayDate = currentDate.plusDays(-1);

        List<HtmlElement> items = page.getByXPath("//section[contains(@class, 'single-result__content')]");

        if (items.isEmpty()) {
            System.out.println("No items found");
        } else {

            for (HtmlElement item : items) {

                HtmlAnchor itemAnchor = item.getFirstByXPath(".//a[contains(@class, 'property-url')]");

                if (itemAnchor != null) {

                    Apartment apartment1 = new Apartment();

                    HtmlElement itemTitle = item.getFirstByXPath(".//h2");
                    HtmlMeta itemPrice = item.getFirstByXPath(".//meta[@itemprop='price']");
                    HtmlElement itemArea = item.getFirstByXPath(".//li[2]/b");
                    HtmlElement itemRooms = item.getFirstByXPath(".//li[1]/b");
                    HtmlElement itemPostDate = item.getFirstByXPath(".//span[contains(@class, 'single-result__category--date')]");

                    apartment1.setId(startId);
                    apartment1.setSite(site);
                    apartment1.setTitle(itemTitle.asText());
                    apartment1.setPrice(Double.parseDouble(itemPrice.getContentAttribute()));
                    apartment1.setArea(Double.parseDouble(itemArea.asText()));
                    apartment1.setRooms(Integer.parseInt(itemRooms.asText()));

                    switch (itemPostDate.asText()) {
                        case "dzisiaj" -> apartment1.setPostDate(currentDate);
                        case "wczoraj" -> apartment1.setPostDate(previousDayDate);
                        default -> {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            apartment1.setPostDate(LocalDate.parse(itemPostDate.asText(), formatter));
                        }
                    }

                    apartment1.setUrl(itemAnchor.getHrefAttribute());

                    System.out.println(startId + ". " + apartment1.toString());

                    startId++;

                } else {
                    System.out.println("reklama");
                }

            }

        }

        return  startId;
    }
}
