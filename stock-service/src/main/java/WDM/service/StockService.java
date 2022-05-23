package WDM.service;
import WDM.pojo.Stock;

public interface StockService {
    Stock queryById(String id);

    Boolean subtract(String id, int amount);

    Boolean add(String id, int amount);

    String create(double price);
}
