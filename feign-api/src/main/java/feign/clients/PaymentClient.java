package feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("paymentservice")
public interface PaymentClient {
    @PostMapping("payment/pay/{userId}/{orderId}/{amount}")
    String pay(@PathVariable("userId") String userid, @PathVariable("orderId") String orderId, @PathVariable("amount") double amount);

}
