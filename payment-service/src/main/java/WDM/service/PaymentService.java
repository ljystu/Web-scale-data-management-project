package WDM.service;

import WDM.pojo.Payment;
import org.springframework.stereotype.Repository;


public interface PaymentService {
    Boolean add(String id, int funds);

    String create();

    Payment queryById(String id);

}
