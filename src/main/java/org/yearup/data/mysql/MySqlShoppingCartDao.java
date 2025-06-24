package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    //Lets us get product using productId
    private final ProductDao productDao;
    private final DataSource dataSource;

    @Autowired
    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
        this.dataSource = dataSource;
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        String query = "SELECT product_id, quantity FROM shopping_cart WHERE user_id = ?";
        ShoppingCart shoppingCart = new ShoppingCart();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (
                    ResultSet resultSet = ps.executeQuery();
            ) {
                while (resultSet.next()) {
                    int productId = resultSet.getInt("product_id");
                    int quantity = resultSet.getInt("quantity");
                    //How do i guard againt null pointer errors
                    shoppingCart.add(new ShoppingCartItem(productDao.getById(productId), quantity));
                }
            }

        }
        catch (SQLException e){
            System.out.println("Error in the MYSQL ShoppingCartDao: " + e.getMessage());
        }
        return shoppingCart;

    }

    @Override

}
