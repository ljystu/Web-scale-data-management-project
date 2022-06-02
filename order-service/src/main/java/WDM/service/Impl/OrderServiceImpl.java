package WDM.service.Impl;

import WDM.mapper.ItemMapper;
import WDM.mapper.OrderMapper;
import WDM.pojo.Item;
import WDM.pojo.Order;
import WDM.service.OrderService;
import feign.clients.PaymentClient;
import feign.clients.StockClient;
import feign.pojo.Stock;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.spring.annotation.GlobalLock;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private StockClient stockClient;

    @Autowired
    private PaymentClient paymentClient;


    /**
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public String createOrder(String userId) {
        String orderId = UUID.randomUUID().toString();
        if (orderMapper.createOrder(userId, orderId)) {
            return orderId;
        } else {
            return "400";
        }
    }

    /**
     * @param orderId
     * @return
     */
    @Override
    @Transactional
    public Boolean removeOrder(String orderId) {
        return orderMapper.removeOrder(orderId);
    }

    /**
     * @param orderId
     * @return
     */
    @GlobalLock
    @Override
//    @Transactional
    public Order findOrder(String orderId) {
        Order order = orderMapper.findOrder(orderId);
        List<Item> items = (itemMapper.findItem(orderId));
        List<String> itemIds = new LinkedList<>();
        for (Item item : items) {
            itemIds.add(item.getItemId());
        }
        order.setItems(itemIds);
        double cost = 0;
        for (Item item : items) {
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
    @Transactional
    public Boolean addItem(String orderId, String itemId) {
        for (Item item : itemMapper.findItem(orderId)) {
            if (item.getItemId().equals(itemId)) {
                itemMapper.updateAmount(itemId);
                return true;
            }
        }
        Stock stock = stockClient.findPrice(itemId);
        String id = UUID.randomUUID().toString();
        itemMapper.addItem(id, orderId, itemId, stock.getPrice());
        return true;
    }

    /**
     * @param orderId
     * @param itemId
     * @return
     */
    @Override
    @Transactional
    public Boolean removeItem(String orderId, String itemId) {
        return itemMapper.removeItem(orderId, itemId);
    }

    /**
     * @param order
     * @return
     */
    Lock lock = new ReentrantLock();

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public Boolean checkout(Order order) throws TransactionException {
        //how to rollback when error
//        Order order = orderMapper.findOrder(orderId);
        try {
//            lock.lock();
//            String XID = RootContext.getXID();
            log.info("Seata global transaction id{}", RootContext.getXID());
            List<Item> items = itemMapper.findItem(order.getOrderId());
            for (Item item : items) {
                if (stockClient.subtract(item.getItemId(), item.getAmount()).equals("400")) {
                    throw new TransactionException("stock failed");
                }
            }
            if (paymentClient.pay(order.getUserId(), order.getOrderId(), order.getTotalCost()).equals("400")) {
                throw new TransactionException("payment failed");
            }
            orderMapper.checkout(order.getOrderId());
            GlobalTransactionContext.reload(RootContext.getXID()).commit();
            return true;
        } catch (Exception e) {
            log.error("checkout failed:{}", e.getMessage(), e);
            log.info("Seata global transaction id=================>{}", RootContext.getXID());
            GlobalTransactionContext.reload(RootContext.getXID()).rollback();
            return false;
        } finally {
//            lock.unlock();
        }
    }

    /**
     * @param orderId
     * @return
     */
    @Transactional
    @Override
    public void cancelOrder(String orderId) {
        orderMapper.cancelOrder(orderId);
    }
}
