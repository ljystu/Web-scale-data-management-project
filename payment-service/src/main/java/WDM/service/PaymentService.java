package WDM.service;

import WDM.pojo.Payment;


public interface PaymentService {
    Boolean pay(String id, double funds);

    Boolean cancel(String userid, String orderid);

    Boolean status(String userid, String orderid);

    Boolean add(String id, int funds);

    String create();

    Payment queryById(String id);

}
