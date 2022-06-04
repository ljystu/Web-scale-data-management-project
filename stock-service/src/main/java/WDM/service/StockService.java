package WDM.service;

import WDM.pojo.Stock;
import feign.FeignException;
import io.seata.core.exception.TransactionException;

public interface StockService {
    Stock queryById(long id);

    Boolean subtract(long id, int amount) throws TransactionException, FeignException;

    Boolean add(long id, int amount);

    String create(double price);
}
