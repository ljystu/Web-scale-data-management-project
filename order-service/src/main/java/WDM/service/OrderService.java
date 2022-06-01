package WDM.service;


import WDM.pojo.Order;
import io.seata.core.exception.TransactionException;


public interface OrderService {

    String createOrder( String userId);

    Boolean removeOrder(String orderId);

    Order findOrder( String orderId);

    Boolean addItem(String orderId,  String itemId);

    Boolean removeItem(String orderId, String itemId);

    Boolean checkout(Order order) throws TransactionException;

    void cancelOrder(String orderId);
}
