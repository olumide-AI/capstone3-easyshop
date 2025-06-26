package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yearup.data.ProductDao;
import org.yearup.models.ShoppingCart;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
