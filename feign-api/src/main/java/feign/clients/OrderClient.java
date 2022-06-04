package feign.clients;


import feign.config.MultipartSupportConfig;
import feign.pojo.Order;
import feign.pojo.Stock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "orderservice", configuration = MultipartSupportConfig.class)
public interface OrderClient {
    @GetMapping("orders/find/{order_id}")
    Order findOrder(@PathVariable("order_id") long orderId);

    @PostMapping("orders/cancel/{orderId}")
    void cancel(@PathVariable("orderId") long orderId);
}
