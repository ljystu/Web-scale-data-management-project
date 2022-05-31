package WDM.service.Impl;

import WDM.mapper.PaymentMapper;
import feign.FeignException;
import feign.pojo.Item;
import WDM.pojo.Payment;
import WDM.service.PaymentService;
import feign.clients.OrderClient;
import feign.clients.StockClient;
import feign.pojo.Order;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentMapper paymentMapper;

    @Autowired
    OrderClient orderClient;

    @Autowired
    StockClient stockClient;

    @Override
    @Transactional
    public Boolean pay(String id, double funds) throws FeignException, TransactionException {

        log.info("Seata global transaction id=================>{}",RootContext.getXID());
        RootContext.bind(RootContext.getXID());
        try {
            paymentMapper.pay(id, funds);
        }
        catch (Exception e){
            GlobalTransactionContext.reload(RootContext.getXID()).rollback();
//            throw e;
            return false;
        }
        return true;
    }
    Lock lock = new ReentrantLock();
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public Boolean cancel(String userid, String orderId) throws TransactionException {
        try {
            lock.lock();
            Order order = orderClient.findOrder(orderId);
            for (Item item : order.getItems()) {
                stockClient.add(item.getItemId(), item.getAmount());
            }
            paymentMapper.add(userid, (int) order.getTotalCost());
            orderClient.cancel(orderId);
        } catch (FeignException e) {
            log.error("checkout failed:{}", e.getCause(), e);
            log.info("Seata global transaction id=================>{}", RootContext.getXID());
            GlobalTransactionContext.reload(RootContext.getXID()).rollback();
            return false;
        }
        finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public Boolean status(String userid, String orderid) {
        Order order = orderClient.findOrder(orderid);
        return order.isPaid();
    }

    @Override
    public Boolean add(String id, double funds) {
        return paymentMapper.add(id, funds);
    }

    @Override
    public String create() {
        String id = UUID.randomUUID().toString();
        if (paymentMapper.create(id)) {
            return id;
        } else {
            return "400: fail to create user";
        }

    }

    @Override
    public Payment queryById(String id) {
        return paymentMapper.queryById(id);
    }
}
