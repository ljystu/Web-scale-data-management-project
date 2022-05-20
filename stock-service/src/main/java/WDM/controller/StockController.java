package WDM.controller;


import WDM.pojo.Stock;
import WDM.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.nacos.api.config.ConfigType.JSON;

@RestController
@RequestMapping("stock")
public class StockController {
    @Autowired
    private StockService stockService;

    //    /stock/find/{item_id}
    //    GET - returns an item’s availability and price.
    //    Output JSON fields:
    //            “stock” - the item’s stock
    //            “price” - the item’s price

    @GetMapping("find/{id}")
    public Map<String, Object> queryById(@PathVariable String id) {
        Stock stock = stockService.queryById(id);
        Map<String, Object> map = new HashMap<>(2);
        if (stock == null) {
            map.put("400", "item not found!");
        } else {
            map.put("stock", stock.getNumber());
            map.put("price", stock.getPrice());
        }
        return map;
    }

    //    /stock/subtract/{item_id}/{amount}
    //    POST - subtracts an item from stock by the amount specified.
    @PostMapping("subtract/{id}/{amount}")
    public String subtract(@PathVariable String id, @PathVariable int amount) {
        if (stockService.subtract(id, amount) == Boolean.TRUE) {
            return "200";
        } else {
            return "400";
        }
    }

    ///stock/add/{item_id}/{amount}
    //    POST - adds the given number of stock items to the item count in the stock
    @PostMapping("add/{id}/{amount}")
    public String add(@PathVariable String id, @PathVariable int amount) {
        if (stockService.add(id, amount) == Boolean.TRUE) {
            return "200";
        } else {
            return "400";
        }
    }

    ///stock/item/create/{price}
    //    POST - adds an item and its price, and returns its ID.
    //    Output JSON fields:
    //            “item_id” - the item’s id
    @PostMapping("item/create/{price}")
    public String create(@PathVariable double price) {
        return stockService.create(price);

    }

}
