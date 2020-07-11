
public class SzajsApp {
    public static void main(String[] args) {
        try {
            MorizonScraper morizon = new MorizonScraper();
            morizon.addNewApartments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}