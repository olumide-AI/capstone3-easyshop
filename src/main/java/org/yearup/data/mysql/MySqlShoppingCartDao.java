package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCart;

import javax.sql.DataSource;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }
    @Override
    public ShoppingCart getByUserId(int userId){}

    @Override
    public void addProduct(int userId, int productId){

    }



}
