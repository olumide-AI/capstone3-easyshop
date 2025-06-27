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
    /**
     * Retrieves the entire shopping cart for the specified user.
     * Each cart item includes product details and quantity.
     * @param userId the ID of the user.
     * @return a ShoppingCart object containing all items.
     * @throws RuntimeException on database access errors.
     */
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
    /**
     * Adds a product to the user's cart. If the product already exists in the cart,
     * its quantity is incremented atomically.
     * @param userId the ID of the user.
     * @param productId the ID of the product.
     * @param qty the quantity to add.
     */
    @Override
    public void addProduct(int userId, int productId, int qty){
        //System.out.println("dao qty = " + qty);
        String query = """
                INSERT INTO shopping_cart (user_id, product_id, quantity)
                VALUES (?,?,?)
                ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)
                """;
        try(
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
                ){
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while adding product to cart", e);
        }
    }

    /**
     * Updates the quantity of a specific product in the user's cart.
     * @param userId the ID of the user.
     * @param productId the ID of the product.
     * @param quantity the new quantity to set.
     */
    @Override
    public void updateQuantity(int userId, int productId, int quantity){
        String query = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        try(
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
                ){
            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3,productId);
            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException("Error updating cart quantity", e);
        }

    }

    /**
     * Deletes all items in the user's shopping cart.
     * @param userId the ID of the user.
     */
    @Override
    public void delete(int userId){
        String query = "DELETE FROM shopping_cart WHERE user_id = ?";
        try(Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(query)){
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException("Error clearing cart", e);
        }
    }

    /**
     * Retrieves a single cart item (product and quantity) for the user.
     * @param userId the ID of the user.
     * @param productId the ID of the product.
     * @return a ShoppingCartItem, or null if not found or product is missing.
     */
    @Override
    public ShoppingCartItem getSingleItem(int userId, int productId){
        String query = "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        ShoppingCartItem shoppingCartItem = null;
        try(
                Connection connection = getConnection();
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
                    if (product == null){
                    }
                    else{
                        shoppingCartItem = new ShoppingCartItem(product,quantity);

                    }
                }
            }



        }

        catch (SQLException e){
            System.out.println("Get single cart item error: " + e.getMessage());

        }

        return shoppingCartItem;
    }

    /**
     * Deletes a specific item (product) from the user's shopping cart.
     * @param userId the ID of the user.
     * @param productId the ID of the product to delete.
     */
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
            throw new RuntimeException("Error deleting single item", e);

    }
    }


}
