package WDM.service.Impl;

import WDM.mapper.StockMapper;
import WDM.pojo.Stock;
import WDM.service.StockService;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StockServiceImpl implements StockService {


    @Autowired
    StockMapper stockMapper;

    @Override
    public Stock queryById(String id) {
        return stockMapper.queryById(id);
    }

    @Override
    public Boolean subtract(String id, int amount) {
        return stockMapper.subtract(id, amount) == Boolean.TRUE;
    }

    @Override
    public Boolean add(String id, int amount) {
        return stockMapper.add(id, amount) == Boolean.TRUE;
    }

    @Override
    public String create(double price) {
        String id = UUID.randomUUID().toString();
        stockMapper.create(id, price);
        return id;
    }
}
