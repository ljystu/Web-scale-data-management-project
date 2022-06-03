package feign.clients;

import feign.config.MultipartSupportConfig;
import feign.pojo.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "paymentservice", configuration = MultipartSupportConfig.class)
public interface PaymentClient {
    @PostMapping("payment/pay/{userId}/{orderId}/{amount}")
    String pay(@PathVariable("userId") String userid, @PathVariable("orderId") String orderId, @PathVariable("amount") double amount);

    @GetMapping("payment/find_user/{user_id}")
    Payment getCredit(@PathVariable("userId") String userid);

}
