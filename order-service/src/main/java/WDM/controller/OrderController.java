package WDM.controller;

import WDM.pojo.Order;
import WDM.service.OrderService;
import com.alibaba.fastjson.serializer.MapSerializer;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    //    /orders/create/{user_id}
    //    POST - creates an order for the given user, and returns an order_id
    //    Output JSON fields:
    //            “order_id”  - the order’s id
    @PostMapping("create/{userId}")
    public Map<String, String> createOrder(@PathVariable("userId") String userId) {
        Map<String, String> map = new HashMap<>();
        String orderId = orderService.createOrder(userId);
        map.put("order_id", orderId);
        return map;

    }

    ///orders/remove/{order_id}
    //    DELETE - deletes an order by ID
    @DeleteMapping("remove/{orderId}")
    public String removeOrder(@PathVariable("orderId") String orderId) {
        if (orderService.removeOrder(orderId)) {
            return "200";
        } else {
            return "400";
        }
    }

    ///orders/find/{order_id}
    //    GET - retrieves the information of an order (id, payment status, items included and user id)
    //    Output JSON fields:
    //            “order_id”  - the order’s id
    //“paid” (true/false)
    //            “items”  - list of item ids that are included in the order
    //“user_id”  - the user’s id that made the order
    //“total_cost” - the total cost of the items in the order
    @GetMapping("find/{orderId}")
    public Order findOrder(@PathVariable("orderId") String orderId) {
        return orderService.findOrder(orderId);
    }

    ///orders/addItem/{order_id}/{item_id}
    //    POST - adds a given item in the order given
    @PostMapping("addItem/{orderId}/{itemId}")
    public String addItem(@PathVariable("orderId") String orderId, @PathVariable("itemId") String itemId) {
        if (orderService.addItem(orderId, itemId)) {
            return "200";
        } else {
            return "400";
        }
    }

    ///orders/removeItem/{order_id}/{item_id}
    //    DELETE - removes the given item from the given order
    @DeleteMapping("removeItem/{orderId}/{itemId}")
    public String removeItem(@PathVariable("orderId") String orderId, @PathVariable("itemId") String itemId) {
        if (orderService.removeItem(orderId, itemId)) {
            return "200";
        } else {
            return "400";
        }
    }
    Lock lock = new ReentrantLock();
    ///orders/checkout/{order_id}
    //    POST - makes the payment (via calling the payment service), subtracts the stock (via the stock service) and returns a status (success/failure).
    @PostMapping("checkout/{orderId}")
    public String checkout(@PathVariable("orderId") String orderId) {
        try {
            lock.lock();
            Order order = orderService.findOrder(orderId);
            if (orderService.checkout(order)) {
                return "200";
            } else {
                return "400";
            }
        } catch (Exception e) {
            return "400";
        }
        finally {
            lock.unlock();
        }
    }

    @PostMapping("cancel/{orderId}")
    public void cancel(@PathVariable("orderId") String orderId) {
        orderService.cancelOrder(orderId);

    }
}
