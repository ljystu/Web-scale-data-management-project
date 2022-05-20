package WDM.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {

    @GetMapping("{orderId}")
    public String findOrder(@PathVariable("orderId") Integer orderId){
        return "hello";
    }
}
