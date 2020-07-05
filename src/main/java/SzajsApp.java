import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SzajsApp {
    public static void main(String[] args) throws Exception {

        MorizonScraper morizon = new MorizonScraper();
        morizon.addApartmentsBatch();

        MySQLAccess dao = new MySQLAccess();
        dao.readDataBase();

    }
}