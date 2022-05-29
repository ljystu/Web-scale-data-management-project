package WDM.service;


import WDM.pojo.Order;
import io.seata.core.exception.TransactionException;
//import io.seata.core.exception.TransactionException;

public interface OrderService {

    String createOrder( String userId);

    ///orders/remove/{order_id}
    //    DELETE - deletes an order by ID
    Boolean removeOrder(String orderId);

    Order findOrder( String orderId);

    ///orders/addItem/{order_id}/{item_id}
    //    POST - adds a given item in the order given
    Boolean addItem(String orderId,  String itemId);

    ///orders/removeItem/{order_id}/{item_id}
    //    DELETE - removes the given item from the given order
    Boolean removeItem(String orderId, String itemId);

    ///orders/checkout/{order_id}
    //    POST - makes the payment (via calling the payment service), subtracts the stock (via the stock service) and returns a status (success/failure).
    Boolean checkout(Order order) throws TransactionException;

}
