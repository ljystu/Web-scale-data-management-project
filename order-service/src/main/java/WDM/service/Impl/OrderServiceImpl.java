package WDM.service.Impl;


import WDM.mapper.OrderMapper;
import WDM.pojo.Order;
import WDM.service.OrderService;
import feign.clients.StockClient;
import feign.pojo.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.Element;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StockClient stockClient;

    /**
     * @param userId
     * @return
     */
    @Override
    public String createOrder(String userId) {
        String orderId = UUID.randomUUID().toString();
        orderMapper.createOrder(userId, orderId);
        return orderId;
    }

    /**
     * @param orderId
     * @return
     */
    @Override
    public Boolean removeOrder(String orderId) {
        return orderMapper.removeOrder(orderId) == Boolean.TRUE;
    }

    /**
     * @param orderId
     * @return
     */
    @Override
    public Order findOrder(String orderId) {
        Order order = orderMapper.findOrder(orderId);
        return order;
    }

    /**
     * @param orderId
     * @param itemId
     * @return
     */
    @Override
    public Boolean addItem(String orderId, String itemId) {
        //need Feign to call stock service.
        // get price of itemid
        //        double price = ;

        if (orderMapper.findItem(orderId).contains(itemId)) {
            orderMapper.updateAmount(itemId);
        } else {
            Stock stock = stockClient.findPrice(itemId);
            orderMapper.addItem(orderId, itemId, stock.getPrice());
        }
        return true;
//        return Boolean.TRUE;
    }

    /**
     * @param orderId
     * @param itemId
     * @return
     */
    @Override
    public Boolean removeItem(String orderId, String itemId) {
        return orderMapper.removeItem(orderId, itemId) == Boolean.TRUE;
    }

    /**
     * @param orderId
     * @return
     */
    @Override
    public String checkout(String orderId) {
        return null;
    }
}
