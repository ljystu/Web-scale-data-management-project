package WDM.service.Impl;

import WDM.mapper.PaymentMapper;
import WDM.pojo.Payment;
import WDM.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentMapper paymentMapper;

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