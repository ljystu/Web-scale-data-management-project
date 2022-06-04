package WDM.service;

import WDM.pojo.Payment;
import feign.FeignException;
import io.seata.core.exception.TransactionException;


public interface PaymentService {
    Boolean pay(long id, double funds) throws FeignException, TransactionException;

    Boolean cancel(long userid, long orderid) throws TransactionException;

    Boolean status(long userid, long orderid);

    Boolean add(long id, double funds);

    String create();

    Payment queryById(long id);

}
