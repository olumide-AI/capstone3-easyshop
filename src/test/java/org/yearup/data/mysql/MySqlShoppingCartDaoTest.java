package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yearup.data.ProductDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MySqlShoppingCartDaoTest {

    @Mock
    DataSource dataSource;
    @Mock
    Connection connection;
    @Mock
    PreparedStatement ps;
    @Mock
    ResultSet rs;
    @Mock
    ProductDao productDao;

    @InjectMocks
    MySqlShoppingCartDao cartDao;

    @BeforeEach
    void init() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);

    }

    @Test
    public void getByUserIdEmptyCartReturnEmptyList() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        ShoppingCart shoppingCart = cartDao.getByUserId(2);

        assertThat(shoppingCart.getItems()).isEmpty();
        verify(ps).setInt(1,2);
    }
    @Test
    public void addProduct() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        cartDao.addProduct(3, 8, 1);

        InOrder order = inOrder(ps);
        order.verify(ps).setInt(1, 3);
        order.verify(ps).setInt(2, 8);
        order.verify(ps).setInt(3, 1);
        verify(ps).executeUpdate();

    }


}
