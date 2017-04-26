package com.example;

import javax.xml.crypto.Data;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;

public class Main {

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            connection = DatabaseUtils.getInstance().getConnection();
            connection.setAutoCommit(false);

            String insertPerson = "INSERT INTO person (name, dob, gender, contacted) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertPerson, Statement.RETURN_GENERATED_KEYS);
            LocalDate contacted = LocalDate.now();
            long contactedMillis = contacted.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
            LocalDate bday = LocalDate.of(1997, 7, 7);
            long bdayMillis = bday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
            preparedStatement.setString(1, "Sven Seven " + System.currentTimeMillis());
            preparedStatement.setDate(2, new Date(contactedMillis));
            preparedStatement.setString(3, "M");
            preparedStatement.setDate(4, new Date(bdayMillis));
            preparedStatement.executeUpdate();


            rs = preparedStatement.getGeneratedKeys();
            rs.next();
            int personId = rs.getInt(1);
            System.out.println(personId);

            String insertAddress = "INSERT INTO address (street1, city, stateAbbr, zip, person_id) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertAddress);
            preparedStatement.setString(1, "777 Seven St");
            preparedStatement.setString(2, "Seattle");
            preparedStatement.setString(3, "WA");
            preparedStatement.setString(4, "98104");
            preparedStatement.setString(5, String.valueOf(personId));

            String insertEmail = "INSERT INTO email (email, person_id) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(insertEmail);
            preparedStatement.setString(1, "svenseven@gmail.com");
            preparedStatement.setString(2, String.valueOf(personId));

            if (!Boolean.parseBoolean(args[0])) {
                throw new SQLException("rolling back");
            }
            connection.commit();

        } catch (SQLException e) {
            DatabaseUtils.printSQLException(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                DatabaseUtils.printSQLException(e1);
            }
        } finally {
            try {
                preparedStatement = connection.prepareStatement("SELECT * FROM person");
                rs = preparedStatement.executeQuery();
                System.out.println("id, name, dob, gender, contacted");
                while (rs.next()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(rs.getInt("id") + ",");
                    sb.append(rs.getString("name") == null ? "," : rs.getString("name") + ",");
                    sb.append(rs.getDate("dob") == null ? "," : rs.getDate("dob") + ",");
                    sb.append(rs.getString("gender") == null ? "," : rs.getString("gender") + ",");
                    sb.append(rs.getDate("contacted") == null ? "," : rs.getDate("contacted") + ",");
                    System.out.println(sb.toString());
                }


            } catch (SQLException e) {
                DatabaseUtils.printSQLException(e);
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    DatabaseUtils.printSQLException(e);
                }
            }
        }
    }
}
