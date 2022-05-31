package WDM.controller;

import WDM.pojo.Payment;
import WDM.service.PaymentService;
import io.seata.core.exception.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    //    /payment/pay/{user_id}/{order_id}/{amount}
//    POST - subtracts the amount of the order from the user’s credit (returns failure if credit is not enough)
    @PostMapping("pay/{user_id}/{order_id}/{amount}")
    public String pay(@PathVariable("user_id") String userid, @PathVariable("order_id") String orderId, @PathVariable("amount") double amount) throws TransactionException {
        if (paymentService.pay(userid, amount)) {
            return "200";
        } else {
            return "400";
        }
    }

    ///payment/cancel/{user_id}/{order_id}
//    POST - cancels payment made by a specific user for a specific order.
    @PostMapping("cancel/{user_id}/{order_id}")
    public String cancel(@PathVariable("user_id") String userid, @PathVariable("order_id") String orderid) {
        try {
            paymentService.cancel(userid, orderid);
            return "200";
        } catch (Exception e) {
            return "400";
        }
    }

    //    /payment/status/{user_id}/{order_id}
//    GET - returns the status of the payment (paid or not)
//    Output JSON fields:
//            “paid” (true/false)
    @GetMapping("status/{user_id}/{order_id}")
    public Map<String, String> status(@PathVariable("user_id") String userid, @PathVariable("order_id") String orderid) {
        Map<String, String> map = new HashMap<>();
        if (paymentService.status(userid, orderid)) {
            map.put("paid", "true");
        } else {
            map.put("paid", "false");
        }
        return map;
    }


    //    /payment/add_funds/{user_id}/{amount}
//    POST - adds funds (amount) to the user’s (user_id) account
//    Output JSON fields:
//            “done” (true/false)
    @PostMapping("add_funds/{user_id}/{amount}")
    public Map<String, String> add(@PathVariable("user_id") String id, @PathVariable("amount") double amount) {
        Map<String, String> map = new HashMap<>();
        if (paymentService.add(id, amount)) {
            map.put("done", "true");
        } else {
            map.put("done", "false");
        }
        return map;
    }


    //    /payment/create_user
//    POST - creates a user with 0 credit
//    Output JSON fields:
//            “user_id” - the user’s id
    @PostMapping("create_user")
    public Map<String, String> create() {
        String userId = paymentService.create();
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        return map;
    }


    //    /payment/find_user/{user_id}
//    GET - returns the user information
//    Output JSON fields:
//            “user_id” - the user’s id
//            “credit” - the user’s credit
    @GetMapping("find_user/{user_id}")
    public Map<String, Object> queryById(@PathVariable("user_id") String user_id) {
        Payment user = paymentService.queryById(user_id);
        Map<String, Object> map = new HashMap<>(2);
        if (user == null) {
            map.put("400", "item not found!");
        } else {
            map.put("user_id", user.getUserId());
            map.put("credit", user.getCredit());
        }
        return map;
    }


}
