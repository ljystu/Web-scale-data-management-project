package WDM.controller;

import WDM.pojo.Payment;
import WDM.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("Payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    //    /payment/add_funds/{user_id}/{amount}
//    POST - adds funds (amount) to the user’s (user_id) account
//    Output JSON fields:
//            “done” (true/false)
    @PostMapping("add/{id}/{amount}")
    public String add(@PathVariable String id, @PathVariable int amount) {
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
    public Map<String, Object> queryById(@PathVariable String id) {
        Payment user = paymentService.queryById(id);
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
