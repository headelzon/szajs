import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SzajsApp {
    public static void main(String[] args) {

        morizonScraper();

    }

    static void morizonScraper(){
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
            numberOfPages = 3;

            int j = 1;

            for (int i = 1; i <= numberOfPages; i++) {

                if (i > 1) {

                    //download next page
                    page = client.getPage(searchUrl + "&page=" + i);

                }

                j = Scraper.newResults(page, j, "morizon", "//section[contains(@class, 'single-result__content')]",
                        ".//a[contains(@class, 'property-url')]", ".//h2",
                        ".//meta[@itemprop='price']", ".//li[2]/b", ".//li[1]/b",
                        ".//span[contains(@class, 'single-result__category--date')]");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}