import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlMeta;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MorizonScraper extends Scraper {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDateTime currentDate = LocalDateTime.now();
    LocalDateTime previousDayDate = currentDate.plusDays(-1);

    void addNewApartments() {
        MySQLAccess dao = new MySQLAccess();
        LocalDate lastDate = dao.lastDate();
        long numberOfRecords = dao.countItems();

        Collection<Apartment> newApartments = scrape(lastDate);
        long start = System.currentTimeMillis();
        dao.apartmentBatchInsert(newApartments);
        long end = System.currentTimeMillis();

        System.out.println("Added " + (dao.countItems() - numberOfRecords) + " records, time taken " + (end - start) + " ms");
    }

    private Collection<Apartment> scrape(LocalDate lastDate) {

        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        int INITIAL_CAPACITY = 1000;
        Collection<Apartment> apartmentArrayList = new ArrayList<>(INITIAL_CAPACITY);

        try {

            long start = System.currentTimeMillis();

            String searchUrl = "https://www.morizon.pl/do-wynajecia/mieszkania/najnowsze/krakow/?ps%5Bnumber_of_rooms_from%5D=1&ps%5Bnumber_of_rooms_to%5D=2";

            HtmlPage page = client.getPage(searchUrl);

            //get number of pages
            HtmlElement pages = page.getFirstByXPath("//ul[contains(@class, 'mz-pagination-number')]/li[last()-1]");
            int numberOfPages = Integer.parseInt(pages.asText());

            //set number of pages for testing
            numberOfPages = 5;

            for (int i = 1; i <= numberOfPages; i++) {

                if (i > 1) {
                    //download next page
                    page = client.getPage(searchUrl + "&page=" + i);
                }

                //get all ads from page
                List<HtmlElement> items = page.getByXPath("//section[contains(@class, 'single-result__content')]");

                if (items.isEmpty()) {
                    System.out.println("No items found");
                } else {

                    for (HtmlElement item : items) {

                        HtmlAnchor itemAnchor = item.getFirstByXPath(".//a[contains(@class, 'property-url')]");

                        if (itemAnchor != null) {
                            HtmlElement itemPostDate = item.getFirstByXPath(".//span[contains(@class, 'single-result__category--date')]");
                            LocalDate postDate;

                            switch (itemPostDate.asText().toLowerCase()) {
                                case "dzisiaj" -> postDate = currentDate.toLocalDate();
                                case "wczoraj" -> postDate = previousDayDate.toLocalDate();
                                default -> postDate = LocalDate.parse(itemPostDate.asText(), formatter);
                            }

                            if ((lastDate != null) && postDate.isBefore(lastDate)) {
                                break;
                            } else {
                                Apartment apartment1 = createApartment(item, postDate, itemAnchor);
                                apartmentArrayList.add(apartment1);
                            }

                        }

                    }

                }

            }

            System.out.println("Parsed " + apartmentArrayList.size() + " records, time taken " + (System.currentTimeMillis() - start) + " ms");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return apartmentArrayList;
    }

    private Apartment createApartment(HtmlElement item, LocalDate postDate, HtmlAnchor itemAnchor){
        Apartment apartment1 = new Apartment();

        HtmlElement itemTitle = item.getFirstByXPath(".//h2");
        HtmlMeta itemPrice = item.getFirstByXPath(".//meta[@itemprop='price']");
        HtmlElement itemArea = item.getFirstByXPath(".//li[2]/b");
        HtmlElement itemRooms = item.getFirstByXPath(".//li[1]/b");

        apartment1.setId(0);
        apartment1.setSite("morizon");
        apartment1.setTitle(itemTitle.asText());
        apartment1.setPrice(Float.parseFloat(itemPrice.getContentAttribute()));
        apartment1.setArea(Float.parseFloat(itemArea.asText()));
        apartment1.setRooms(Integer.parseInt(itemRooms.asText()));
        apartment1.setPostDate(postDate);
        apartment1.setUrl(itemAnchor.getHrefAttribute());
        apartment1.setAddDate(currentDate);

        return apartment1;
    }

}

