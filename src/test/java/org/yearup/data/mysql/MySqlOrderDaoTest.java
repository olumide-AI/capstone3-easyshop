package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yearup.models.Order;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MySqlOrderDaoTest {

    @Mock
    DataSource dataSource;
    @Mock
    Connection connection;
    @Mock
    PreparedStatement ps;
    @Mock
    ResultSet rs;

    @InjectMocks
    MySqlOrderDao orderDao;

    @BeforeEach
    void setUp() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(42);          // fake PK
    }

    @Test
    void createOrderWithId() throws Exception {

        // Arrange
        Order in = new Order();
        in.setUserId(7);
        in.setAddress("123 Main");
        in.setCity("Dallas");
        in.setState("TX");
        in.setZip("75051");
        in.setShippingAmount(BigDecimal.ZERO);

        //Act
        Order out = orderDao.create(in);

        //Assert
        assertThat(out.getOrderId()).isEqualTo(42);

        InOrder order = inOrder(ps);
        order.verify(ps).setInt       (1, 7);
        order.verify(ps).setString    (2, "123 Main");
        order.verify(ps).setString    (3, "Dallas");
        order.verify(ps).setString    (4, "TX");
        order.verify(ps).setString    (5, "75051");
        order.verify(ps).setBigDecimal(6, BigDecimal.ZERO);
        verify(ps).executeUpdate();
    }
}
