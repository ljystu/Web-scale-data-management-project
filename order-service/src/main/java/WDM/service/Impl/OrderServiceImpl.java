package WDM.service.Impl;


import WDM.mapper.ItemMapper;
import WDM.mapper.OrderMapper;
import WDM.pojo.Item;
import WDM.pojo.Order;
import WDM.service.OrderService;
import feign.clients.StockClient;
import feign.pojo.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private StockClient stockClient;

    /**
     * @param userId
     * @return
     */
    @Override
    public String createOrder(String userId) {
        String orderId = UUID.randomUUID().toString();
        if(orderMapper.createOrder(userId, orderId) == Boolean.TRUE){
            return orderId;
        }
        else{
            return "400";
        }

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
        order.setItems(itemMapper.findItem(orderId));
        double cost = 0;
        for (Item item : order.getItems()) {
            cost += (item.getAmount() * item.getPrice());
        }
        order.setTotalCost(cost);
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
        for (Item item : itemMapper.findItem(orderId)) {
            if (item.getItemId().equals(itemId)) {
                itemMapper.updateAmount(itemId);
                return true;
            }
        }
        Stock stock = stockClient.findPrice(itemId);
        itemMapper.addItem(orderId, itemId, stock.getPrice());
        return true;
    }

    /**
     * @param orderId
     * @param itemId
     * @return
     */
    @Override
    public Boolean removeItem(String orderId, String itemId) {
        return itemMapper.removeItem(orderId, itemId) == Boolean.TRUE;
    }

    /**
     * @param order
     * @return
     */
    @Override
    public String checkout(Order order) {


        return null;
    }
}