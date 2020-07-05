import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class MySQLAccess {

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    public void readDataBase() throws Exception {

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select * from szajs.Apartments"))
        {

            writeResultSet(resultSet);

        } catch (Exception e) {
            throw e;
        }

    }

    Connection getConnection() throws SQLException{

        try {
            Connection conn = null;
            Properties connectionProps = new Properties();
            connectionProps.put("user", "root");
            connectionProps.put("password", "haslo");

            conn = DriverManager.getConnection("jdbc:mysql://localhost/szajs?serverTimezone=UTC&rewriteBatchedStatements=true", connectionProps);

            System.out.println("Connected to database");
            return conn;

        } catch (Exception e) {
            return null;
        }
    }


    public void addApartmentToBatch(Apartment apartment, PreparedStatement statement) throws SQLException {
        try {

            DateTimeFormatter mySQLformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            statement.setInt(1, 0);
            statement.setString(2, apartment.getSite());
            statement.setString(3, apartment.getTitle());
            statement.setFloat(4, apartment.getPrice());
            statement.setFloat(5, apartment.getArea());
            statement.setInt(6,apartment.getRooms());
            statement.setString(7, apartment.getPostDate().format(dateFormatter));
            statement.setString(8, apartment.getUrl());
            statement.setString(9, apartment.getAddDate().format(dateTimeFormatter));

            statement.addBatch();

        } catch (Exception e) {
            throw e;
        }

    }

    public LocalDate latestDate(Connection connection) throws SQLException {

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT MAX(PostDate) AS LatestDate from szajs.Apartments"))
        {

            LocalDate latestDate;

            resultSet.next();

            latestDate = LocalDate.parse(resultSet.getString("LatestDate"), dateFormatter);

            return latestDate;

        } catch (Exception e) {
            return null;
        }

    }

    public ResultSet latestApartments(Connection connection, LocalDate latestDate) throws SQLException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT Url AS Url from szajs.Apartments WHERE PostDate = ?");
            preparedStatement.setString(1,latestDate.format(dateFormatter));
            resultSet = preparedStatement.executeQuery();

            return resultSet;

        } catch (Exception e) {
            throw e;
        }
    }

    public boolean alreadyExists(ResultSet latestResultSet, Apartment apartment) throws SQLException {
        boolean exists = false;

        try {

            while (latestResultSet.next()) {

                if (apartment.getUrl().equals(latestResultSet.getString("Url"))) {
                    exists = true;
                    break;
                }
            }

            return exists;

        } catch (Exception e) {
            throw e;
        }
    }

    private void writeResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            int id = resultSet.getInt("ID");
            String site = resultSet.getString("Site");
            String title = resultSet.getString("Title");
            float price = resultSet.getFloat("Price");
            float area = resultSet.getFloat("Area");
            int rooms = resultSet.getInt("Rooms");
            String postDate = resultSet.getString("PostDate");
            String url = resultSet.getString("Url");
            String addDate = resultSet.getString("AddDate");

            System.out.println("ID: " + id + ", site: " + site + ", title: " + title + ", price: " + price + ", area: "
                    + area + ", rooms: " + rooms + ", postDate: " + postDate + ", url" + url + ", addDate: " + addDate);

        }
    }

}