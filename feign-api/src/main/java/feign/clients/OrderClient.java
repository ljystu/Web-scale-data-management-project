package feign.clients;


import feign.config.MultipartSupportConfig;
import feign.pojo.Order;
import feign.pojo.Stock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "order-service", configuration = MultipartSupportConfig.class)
public interface OrderClient {
    @GetMapping("orders/find/{orderId}")
    Order findOrder(@PathVariable("orderId") long orderId);

    @PostMapping("orders/cancel/{orderId}")
    void cancel(@PathVariable("orderId") long orderId);
}
