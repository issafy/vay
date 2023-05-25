package com.niit.vay.services;

import com.niit.vay.models.ShipOrder;
import com.niit.vay.repositories.CartRepository;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class VayJdbcService {


    private final ShipOrderService shipOrderService;
    private final CartRepository cartRepository;
    private final CartService cartService;

    public Connection connection;
    public Statement getFromDb;


    public VayJdbcService(ShipOrderService shipOrderService, CartRepository cartRepository, CartService cartService) {
        this.shipOrderService = shipOrderService;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }

    public List<Integer> getBarChartData() throws SQLException {
        List<Integer> barChartData = new ArrayList<>();
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        Statement getFromDb = dbConnection.createStatement();
        ResultSet barChartsResults = getFromDb.executeQuery("SELECT COUNT(ship_order_id) as 'Sales' FROM ship_order where (year(created), processed) = (year(now()), true) GROUP BY MONTH(created) ORDER BY month(created);");
        while (barChartsResults.next()) {
            barChartData.add(Integer.valueOf(barChartsResults.getString("Sales")));
        }
        dbConnection.close();

        return barChartData;
    }

    public List<Long> getLine1ChartData() throws SQLException {
        List<Long> line1ChartData = new ArrayList<>();
        long monthlySales = 0L;
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        PreparedStatement preparedStatement = dbConnection.prepareStatement("select * from ship_order where (month(created), year(created), processed) = (?, year(now()) - 1, true);");
        for (int i = 1; i <= 12; i++) {
            preparedStatement.setInt(1, i);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                monthlySales += cartService.getCartTotal(resultSet.getLong("cart_id"));
            line1ChartData.add(monthlySales);
        }
        dbConnection.close();
        return line1ChartData;
    }

    public List<Long> getLine2ChartData() throws SQLException {
        List<Long> line2ChartData = new ArrayList<>();
        long monthlySales = 0L;
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        PreparedStatement preparedStatement = dbConnection.prepareStatement("select * from ship_order where (month(created), year(created), processed) = (?, year(now()), true);");
        for (int i = 1; i <= 12; i++) {
            preparedStatement.setInt(1, i);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                monthlySales += cartService.getCartTotal(resultSet.getLong("cart_id"));
            line2ChartData.add(monthlySales);
        }
        dbConnection.close();
        return line2ChartData;
    }

    public Long getCurrentMonthlySales() {
        List<ShipOrder> shipOrders = shipOrderService.getProcessedShipOrders();
        Long monthlySales = 0l;
        LocalDate shipDate;
        LocalDate today = LocalDate.now();
        for(ShipOrder shipOrder: shipOrders) {
            shipDate = LocalDate.ofInstant(shipOrder.getCreated(), ZoneOffset.UTC);
            if (shipDate.getYear() == today.getYear() && shipDate.getMonthValue() == today.getMonthValue())
                monthlySales += cartService.getCartTotal(shipOrder.getCart().getCartId());
        }
        return monthlySales;
    }

    public Long getPreviousMonthlySales() {
        List<ShipOrder> shipOrders = shipOrderService.getProcessedShipOrders();
        Long previousMonthlySales = 0l;
        LocalDate shipDate;
        LocalDate today = LocalDate.now();
        for(ShipOrder shipOrder: shipOrders) {
            shipDate = LocalDate.ofInstant(shipOrder.getCreated(), ZoneOffset.UTC);
            if (shipDate.getYear() == today.getYear() && shipDate.getMonthValue() == today.getMonthValue() - 1)
                previousMonthlySales += cartService.getCartTotal(shipOrder.getCart().getCartId());
        }
        return previousMonthlySales;
    }

    public String getMonthlySalesBorM() throws SQLException {
        if ((getCurrentMonthlySales() - getPreviousMonthlySales()) < 0) {
            return "-" + (getCurrentMonthlySales() - getPreviousMonthlySales()) * 100 / getPreviousMonthlySales() + "%";
        }
        return "+" + (getCurrentMonthlySales() - getPreviousMonthlySales()) * 100 / getPreviousMonthlySales() + "%";
    }

    public Long getMonthlyUsers() throws SQLException {
        long monthlyUsers = 0L;
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        Statement getFromDb = dbConnection.createStatement();
        ResultSet monthlyResults = getFromDb.executeQuery("select * from users where (month(created), year(created), enabled) = (month(now()), year(now()), false);");
        while (monthlyResults.next())
            monthlyUsers += 1;
        dbConnection.close();
        return monthlyUsers;
    }

    public Long getPreviousMonthlyUsers() throws SQLException {
        long previousMonthlyUsers = 0L;
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        Statement getFromDb = dbConnection.createStatement();
        ResultSet previousMonthlyResults = getFromDb.executeQuery("select * from users where (month(created), year(created), enabled) = (month(now()) - 1, year(now()), false);");
        while (previousMonthlyResults.next())
            previousMonthlyUsers += 1;
        dbConnection.close();
        return previousMonthlyUsers;
    }

    //    BorM = Bonus or Minus !!!
    public String getMonthlyUsersBorM() throws SQLException {
        Long Borm = (getMonthlyUsers() - getPreviousMonthlyUsers()) * 100 / getPreviousMonthlyUsers();
        if (Borm < 0)
            return "-" + Borm + "%";
        return "+" + Borm + "%";
    }

    public Long getMonthlyEnabledUsers() throws SQLException{
        long monthlyUsers = 0L;
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        Statement getFromDb = dbConnection.createStatement();
        ResultSet monthlyResults = getFromDb.executeQuery("select * from users where (month(created), year(created), enabled) = (month(now()), year(now()), true);");
        while (monthlyResults.next())
            monthlyUsers += 1;
        dbConnection.close();
        return monthlyUsers;
    }

    public Long getPreviousMonthlyEnabledUsers() throws SQLException{
        long monthlyUsers = 0L;
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        Statement getFromDb = dbConnection.createStatement();
        ResultSet monthlyResults = getFromDb.executeQuery("select * from users where (month(created), year(created), enabled) = (month(now()) - 1, year(now()), true);");
        while (monthlyResults.next())
            monthlyUsers += 1;
        dbConnection.close();
        return monthlyUsers;
    }

    //    BorM = Bonus or Minus !!!
    public String getMonthlyEnabledUsersBorM() throws SQLException {
        long BorM = (getMonthlyEnabledUsers() - getPreviousMonthlyEnabledUsers()) * 100 / getPreviousMonthlyEnabledUsers();
        if (BorM < 0)
            return "-" + BorM + "%";
        return "+" + BorM + "%";
    }

    public List<Integer> getTop5ProductCounts() throws SQLException {
        List<Integer> top5ProductCounts = new ArrayList<>();
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        Statement getFromDb = dbConnection.createStatement();
        ResultSet results = getFromDb.executeQuery("select distinct product_name, li.quantity from line_item, product join line_item li on product.product_id = li.product_id order by li.quantity desc limit 5");
        while (results.next())
            top5ProductCounts.add(Integer.valueOf(results.getString("quantity")));
        dbConnection.close();
        return top5ProductCounts;
    }

    public List<String> getTop5ProductNames() throws SQLException {
        List<String> top5ProductNames = new ArrayList<>();
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        Statement getFromDb = dbConnection.createStatement();
        ResultSet results = getFromDb.executeQuery("select distinct product_name, li.quantity from line_item, product join line_item li on product.product_id = li.product_id order by li.quantity desc limit 5");
        while (results.next())
            top5ProductNames.add(results.getString("product_name"));
        dbConnection.close();
        return top5ProductNames;
    }

    //    BorM = Bonus or Minus...concatenated !!!
    public String getYearlyOverview() {
        List<ShipOrder> shipOrders = shipOrderService.getProcessedShipOrders();
        Float yearlySales = 0f;
        Float previousYearlySales = 0f;
        LocalDate shipDate;
        LocalDate today = LocalDate.now();
        for(ShipOrder shipOrder: shipOrders) {
            shipDate = LocalDate.ofInstant(shipOrder.getCreated(), ZoneOffset.UTC);
            if (shipDate.getYear() == today.getYear())
                yearlySales += cartService.getCartTotal(shipOrder.getCart().getCartId());
            else if (shipDate.getYear() == today.getYear() - 1)
                previousYearlySales += cartService.getCartTotal(shipOrder.getCart().getCartId());
        }
        try {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            Float BorM = ((yearlySales - previousYearlySales) / previousYearlySales) * 100;
            if (BorM < 0)
                return "-" + decimalFormat.format(BorM) + "%";
            return "+" + decimalFormat.format(BorM) + "%";
        } catch (ArithmeticException e) {
            return "+" + 0 + "%";
        }
    }

    public String getMonthlyOverview() {
        List<ShipOrder> shipOrders = shipOrderService.getProcessedShipOrders();
        Float monthlySales = 0f;
        Float previousMonthlySales = 0f;
        LocalDate shipDate;
        LocalDate today = LocalDate.now();
        for(ShipOrder shipOrder: shipOrders) {
            shipDate = LocalDate.ofInstant(shipOrder.getCreated(), ZoneOffset.UTC);
            if (shipDate.getYear() == today.getYear() && shipDate.getMonthValue() == today.getMonthValue())
                monthlySales += cartService.getCartTotal(shipOrder.getCart().getCartId());
            else if (shipDate.getYear() == today.getYear() && shipDate.getMonthValue() == today.getMonthValue() - 1)
                previousMonthlySales += cartService.getCartTotal(shipOrder.getCart().getCartId());
        }
        try {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            Float BorM = ((monthlySales - previousMonthlySales) / previousMonthlySales) * 100;
            if (BorM < 0)
                return decimalFormat.format(BorM) + "%";
            return "+" + decimalFormat.format(BorM) + "%";
        } catch (ArithmeticException e) {
            return "+" + 0 + "%";
        }

    }

    public String getDailyOverview()  {
        List<ShipOrder> shipOrders = shipOrderService.getProcessedShipOrders();
        Float dailySales = 0f;
        Long totalSales = shipOrderService.getTotalSales();
        LocalDate shipDate;
        LocalDate today = LocalDate.now();
        for(ShipOrder shipOrder: shipOrders) {
            shipDate = LocalDate.ofInstant(shipOrder.getCreated(), ZoneOffset.UTC);
            if (shipDate.getYear() == today.getYear() && shipDate.getDayOfYear() == today.getDayOfYear())
                dailySales += cartService.getCartTotal(shipOrder.getCart().getCartId());
        }
        try {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            Float BorM = (dailySales / totalSales) * 100;
            return "+" + decimalFormat.format(BorM) + "%";
        } catch (ArithmeticException e) {
            return "+" + 0 + "%";
        }
    }

    public Integer getMonthlySalesCount() throws SQLException {
        int salesCount = 0;
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        Statement getFromDb = dbConnection.createStatement();
        ResultSet resultSet = getFromDb.executeQuery("select count(ship_order_id) from ship_order where (month(created), year(created), processed) = (month(now()), year(now()), true);");
        while (resultSet.next())
            salesCount += 1;
        dbConnection.close();
        return salesCount;
    }

    public Integer getPreviousMonthlySalesCount() throws SQLException {
        int salesCount = 0;
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        Statement getFromDb = dbConnection.createStatement();
        ResultSet resultSet = getFromDb.executeQuery("select count(ship_order_id) from ship_order where (month(created), year(created), processed) = (month(now()) - 1, year(now()), true);");
        while (resultSet.next())
            salesCount += 1;
        dbConnection.close();
        return salesCount;
    }


    public List<String> getSearchQuery() throws SQLException {
        List<String> strings = new ArrayList<String>();
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        Statement getFromDb = dbConnection.createStatement();
        ResultSet results = getFromDb.executeQuery("select product_name from product;");
        while (results.next())
            strings.add(results.getString("product_name"));
        results = getFromDb.executeQuery("select category_name from category;");
        while (results.next())
            strings.add(results.getString("category_name"));
        dbConnection.close();
        return strings;
    }

    public void removeLineItem(Long lineItemId) throws SQLException {
        List<String> strings = new ArrayList<String>();
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        PreparedStatement getFromDb = dbConnection.prepareStatement("delete from cart_line_items where cart_line_items.line_items_line_item_id = ?");
        getFromDb.setLong(1, lineItemId);
        getFromDb.executeQuery();
        dbConnection.close();
    }

    public void seed() throws SQLException, IOException {
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "skyXplorer", "Kaiz0ku9_9");
        Reader reader = new BufferedReader(new FileReader("src/main/resources/vay_seeder.sql"));
        ScriptRunner sr = new ScriptRunner(dbConnection);
        sr.runScript(reader);
        dbConnection.close();
    }


}
