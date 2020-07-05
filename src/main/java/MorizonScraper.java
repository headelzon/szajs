import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlMeta;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MorizonScraper extends Scraper {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDateTime currentDate = LocalDateTime.now();
    LocalDateTime previousDayDate = currentDate.plusDays(-1);

    void scrape(){
        /*
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        try {

            String searchUrl = "https://www.morizon.pl/do-wynajecia/mieszkania/najnowsze/krakow/?ps%5Bnumber_of_rooms_from%5D=1&ps%5Bnumber_of_rooms_to%5D=2";
            HtmlPage page = client.getPage(searchUrl);

            //get number of pages
            HtmlElement pages = page.getFirstByXPath("//ul[contains(@class, 'mz-pagination-number')]/li[last()-1]");
            int numberOfPages = Integer.parseInt(pages.asText());

            //set number of pages for testing
            numberOfPages = 2;

            int j = 1;

            for (int i = 1; i <= numberOfPages; i++) {

                if (i > 1) {

                    //download next page
                    page = client.getPage(searchUrl + "&page=" + i);

                }

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

                            apartment1.setId(j);
                            apartment1.setSite("morizon");
                            apartment1.setTitle(itemTitle.asText());
                            apartment1.setPrice(Float.parseFloat(itemPrice.getContentAttribute()));
                            apartment1.setArea(Float.parseFloat(itemArea.asText()));
                            apartment1.setRooms(Integer.parseInt(itemRooms.asText()));

                            switch (itemPostDate.asText().toLowerCase()) {
                                case "dzisiaj" -> apartment1.setPostDate(currentDate);
                                case "wczoraj" -> apartment1.setPostDate(previousDayDate);
                                default -> {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                    apartment1.setPostDate(LocalDate.parse(itemPostDate.asText(), formatter));
                                }
                            }

                            apartment1.setUrl(itemAnchor.getHrefAttribute());
                            apartment1.setAddDate(currentDate);

                            System.out.println(j + ". " + apartment1.toString());

                            if (j <= 3) {
                                MySQLAccess dao = new MySQLAccess();
                                dao.addNewApartment(apartment1);
                            }

                            j++;

                        } else {
                            System.out.println("reklama");
                        }

                    }

                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

         */
    }

    void addApartmentsBatch() throws SQLException {
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        MySQLAccess dao = new MySQLAccess();
        Connection connection = dao.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("insert into szajs.Apartments values (?, ?, ?, ?, ? , ?, ?, ?, ?)");
        LocalDate latestDate = dao.latestDate(connection);
        ResultSet latestApartments = null;
        int newApartments = 0;

        try {

            long start = System.currentTimeMillis();

            String searchUrl = "https://www.morizon.pl/do-wynajecia/mieszkania/najnowsze/krakow/?ps%5Bnumber_of_rooms_from%5D=1&ps%5Bnumber_of_rooms_to%5D=2";
            HtmlPage page = client.getPage(searchUrl);

            //get number of pages
            HtmlElement pages = page.getFirstByXPath("//ul[contains(@class, 'mz-pagination-number')]/li[last()-1]");
            int numberOfPages = Integer.parseInt(pages.asText());

            //set number of pages for testing
            numberOfPages = 10;

            int j = 1;

            for (int i = 1; i <= numberOfPages; i++) {

                if (i > 1) {

                    //download next page
                    page = client.getPage(searchUrl + "&page=" + i);

                }

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

                            Apartment apartment1 = null;

                            if (latestDate == null) {
                                apartment1 = createApartment(item, postDate, itemAnchor);
                                dao.addApartmentToBatch(apartment1,preparedStatement);
                                newApartments++;
                            } else {

                                if (postDate.isAfter(latestDate)) {
                                    apartment1 = createApartment(item, postDate, itemAnchor);
                                    dao.addApartmentToBatch(apartment1, preparedStatement);
                                    newApartments++;

                                } else if (postDate.isEqual(latestDate)) {

                                    if (latestApartments == null) {
                                        latestApartments = dao.latestApartments(connection, latestDate);
                                    }

                                    apartment1 = createApartment(item, postDate, itemAnchor);

                                    if (!(dao.alreadyExists(latestApartments, apartment1))) {
                                        dao.addApartmentToBatch(apartment1, preparedStatement);
                                        newApartments++;
                                    }

                                } else break;
                            }

                            j++;

                        } else {
                            //System.out.println("reklama");
                        }

                    }

                }

            }

            System.out.println("Parsed " + ( j - 1 ) + " records, time taken "+(System.currentTimeMillis()-start) + " ms");

            start = System.currentTimeMillis();
            preparedStatement.executeBatch();
            System.out.println("Added " + newApartments + " records, time taken "+(System.currentTimeMillis()-start) + " ms");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }finally{
            if (latestApartments != null) { latestApartments.close(); }
            if (preparedStatement != null) { preparedStatement.close(); }
            if (connection != null) { connection.close(); }
        }

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

