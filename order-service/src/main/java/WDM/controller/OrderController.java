package WDM.controller;

import WDM.pojo.Order;
import WDM.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public String createOrder(@PathVariable("userId") String userId) {
        return orderService.createOrder(userId);
    }

    ///orders/remove/{order_id}
    //    DELETE - deletes an order by ID
    @PostMapping("remove/{orderId}")
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
    @PostMapping("removeItem/{orderId}/{itemId}")
    public String removeItem(@PathVariable("orderId") String orderId, @PathVariable("itemId") String itemId) {
        if (orderService.removeItem(orderId, itemId)) {
            return "200";
        } else {
            return "400";
        }
    }

    ///orders/checkout/{order_id}
    //    POST - makes the payment (via calling the payment service), subtracts the stock (via the stock service) and returns a status (success/failure).
    @PostMapping("checkout/{orderId}")
    public String checkout(@PathVariable("orderId") String orderId) {
//        if (orderService.check(orderId, itemId)) {
//            return "200";
//        } else {
//            return "400";
//        }
        return "200";
    }
}
