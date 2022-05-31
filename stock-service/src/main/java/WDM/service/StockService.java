package WDM.service;
import WDM.pojo.Stock;
import feign.FeignException;
import io.seata.core.exception.TransactionException;

public interface StockService {
    Stock queryById(String id);

    Boolean subtract(String id, int amount) throws TransactionException, FeignException;

    Boolean add(String id, int amount);

    String create(double price);
}
