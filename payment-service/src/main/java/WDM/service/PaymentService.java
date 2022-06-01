package WDM.service;

import WDM.pojo.Payment;
import feign.FeignException;
import io.seata.core.exception.TransactionException;


public interface PaymentService {
    Boolean pay(String id, double funds) throws FeignException, TransactionException;

    Boolean cancel(String userid, String orderid) throws TransactionException;

    Boolean status(String userid, String orderid);

    Boolean add(String id, double funds);

    String create();

    Payment queryById(String id);

}
