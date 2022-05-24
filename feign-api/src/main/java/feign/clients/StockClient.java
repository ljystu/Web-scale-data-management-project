package feign.clients;


import feign.pojo.Stock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "stockservice")
public interface StockClient {
    @GetMapping("stock/find/{itemId}")
    Stock findPrice(@PathVariable("itemId") String itemId);
}
