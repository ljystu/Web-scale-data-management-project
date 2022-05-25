package WDM.controller;

import WDM.pojo.Payment;
import WDM.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    //    /payment/pay/{user_id}/{order_id}/{amount}
//    POST - subtracts the amount of the order from the user’s credit (returns failure if credit is not enough)
    @PostMapping("pay/{user_id}/{order_id}/{amount}")
    public String pay(@PathVariable("userid") String userid, @PathVariable("amount") int amount){
        if (paymentService.pay(userid, amount) == Boolean.TRUE) {
            return "200";
        } else {
            return "400";
        }
    }

    //    /payment/add_funds/{user_id}/{amount}
//    POST - adds funds (amount) to the user’s (user_id) account
//    Output JSON fields:
//            “done” (true/false)
    @PostMapping("add_funds/{id}/{amount}")
    public String add(@PathVariable("id") String id, @PathVariable("amount") int amount) {
        if (paymentService.add(id, amount) == Boolean.TRUE) {
            return "200";
        } else {
            return "400";
        }
    }


    //    /payment/create_user
//    POST - creates a user with 0 credit
//    Output JSON fields:
//            “user_id” - the user’s id
    @PostMapping("create_user")
    public String create() {
        return paymentService.create();
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
            map.put("credits", user.getCredit());
        }
        return map;
    }


}
