package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
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
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (
                    ResultSet resultSet = ps.executeQuery();
            ) {
                while (resultSet.next()) {
                    int productId = resultSet.getInt("product_id");
                    int quantity = resultSet.getInt("quantity");
                    //How do i guard againt null pointer errors
                    Product product = productDao.getById(productId);
                    if (product == null){
                        continue;

                    }
                    shoppingCart.add(new ShoppingCartItem(product, quantity));
                }
            }

        }
        catch (SQLException e){
            throw new RuntimeException("Error fetching cart", e);
        }
        return shoppingCart;

    }

    @Override
    public void addProduct(int userId, int productId){
        String query = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?,?,1) ON DUPLICATE KEY UPDATE quantity = quantity + 1 ";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
                ){
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while adding product into cart :" + e.getMessage());
        }
    }

    @Override
    public void updateQuantity(int userId, int productId, int quantity){
        String query = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
                ){
            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(2,productId);
            ps.executeUpdate();
        }
        catch (SQLException e){
            System.out.println("Error updating shopping cart: " +e.getMessage() );
        }

    }

    @Override
    public void delete(int userId){
        String query = "DELETE FROM shopping_cart WHERE user_id = ?";
        try(Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(query)){
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
        catch (SQLException e){
            System.out.println("Cart delete error: " + e.getMessage());
        }
    }

    @Override
    public ShoppingCartItem getSingleItem(int userId, int productId){
        String query = "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_is = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
                ){
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try(
                    ResultSet rs = ps.executeQuery()
                    ){
                if(rs.next()){
                    int quantity = rs.getInt("quantity");
                    Product product = productDao.getById(productId);
                    return new ShoppingCartItem(product, quantity);
                }
            }


        }

        catch (SQLException e){
            System.out.println("Get single cart item error: " + e.getMessage());

        }

        return null;
    }

    @Override
    public void deleteItem(int userId, int productId){
        String sql = "DELETE FROM shopping_cart WHERE user_id=? AND product_id=?";
        try(Connection c = getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,userId);
            ps.setInt(2,productId);
            ps.executeUpdate();
        }
        catch(SQLException e){
            System.out.println("Error deleteing a single item: " + e.getMessage());

    }
    }


}
