package WDM.service.Impl;

import WDM.mapper.PaymentMapper;
import WDM.pojo.Payment;
import WDM.service.PaymentService;
import feign.clients.OrderClient;
import feign.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentMapper paymentMapper;

    @Autowired
    OrderClient orderClient;

    @Override
    public Boolean pay(String id, int funds) {
        return paymentMapper.pay(id,funds)==Boolean.TRUE;
    }

    @Override
    public Boolean cancel(String userid, String orderid) {
        Order order = orderClient.findOrder(orderid);
        return paymentMapper.add(userid, (int) order.getTotalCost())==Boolean.TRUE;
//        return paymentMapper.pay(id,funds)==Boolean.TRUE;
    }

    @Override
    public Boolean status(String userid, String orderid) {
        Order order = orderClient.findOrder(orderid);
        return order.isPaid();
    }

    @Override
    public Boolean add(String id, int funds) {
        return paymentMapper.add(id,funds)==Boolean.TRUE;
    }

    @Override
    public String create() {
        String id = UUID.randomUUID().toString();
        if(paymentMapper.create(id) == Boolean.TRUE){
            return id;
        }
        else{
            return "400: fail to create user";
        }

    }

    @Override
    public Payment queryById(String id) {
        return paymentMapper.queryById(id);
    }
}
