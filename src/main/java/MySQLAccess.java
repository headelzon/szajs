import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Properties;

public class MySQLAccess {

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    public void readDataBase() {

        try (Connection connection = getConnection()) {
            assert connection != null;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("select * from szajs.Apartments"))
            {
                writeResultSet(resultSet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int countItems() {

        try (Connection connection = getConnection()) {
            assert connection != null;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("select count(*) from szajs.Apartments"))
            {
                resultSet.next();
                return resultSet.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Connection getConnection() {

        try {
            Connection conn;
            Properties connectionProps = new Properties();
            connectionProps.put("user", "root");
            connectionProps.put("password", "haslo");

            conn = DriverManager.getConnection("jdbc:mysql://localhost/szajs?serverTimezone=UTC&rewriteBatchedStatements=true", connectionProps);

            System.out.println("Connected to database");
            return conn;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void apartmentBatchInsert(Collection<Apartment> apartmentsCollection) {
        try (Connection connection = getConnection()) {
            assert connection != null;
            try (PreparedStatement preparedStatement =
                         connection.prepareStatement(
                                 "insert into szajs.Apartments values (?, ?, ?, ?, ? , ?, ?, ?, ?) " +
                                         "on duplicate key update url=url")) {

                for (Apartment apartment : apartmentsCollection) {

                    preparedStatement.setInt(1, 0);
                    preparedStatement.setString(2, apartment.getSite());
                    preparedStatement.setString(3, apartment.getTitle());
                    preparedStatement.setFloat(4, apartment.getPrice());
                    preparedStatement.setFloat(5, apartment.getArea());
                    preparedStatement.setInt(6, apartment.getRooms());
                    preparedStatement.setString(7, apartment.getPostDate().format(dateFormatter));
                    preparedStatement.setString(8, apartment.getUrl());
                    preparedStatement.setString(9, apartment.getAddDate().format(dateTimeFormatter));

                    preparedStatement.addBatch();
                }

                preparedStatement.executeBatch();

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    LocalDate lastDate() {

        try (Connection connection = getConnection()) {
            assert connection != null;
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT MAX(PostDate) AS LatestDate from szajs.Apartments"))
            {

                LocalDate lastDate;
                resultSet.next();
                lastDate = LocalDate.parse(resultSet.getString("LatestDate"), dateFormatter);

                return lastDate;

            } catch (NullPointerException e) {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeResultSet(ResultSet resultSet) {

        try {
            while (resultSet.next()) {

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
                        + area + ", rooms: " + rooms + ", postDate: " + postDate + ", url: " + url + ", addDate: " + addDate);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}