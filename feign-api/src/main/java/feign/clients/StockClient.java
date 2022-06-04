package feign.clients;


import feign.config.MultipartSupportConfig;
import feign.pojo.Stock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "stockservice",configuration = MultipartSupportConfig.class)
public interface StockClient {
    @GetMapping("stock/find/{itemId}")
    Stock findPrice(@PathVariable("itemId") long itemId);

    @PostMapping("stock/subtract/{itemId}/{amount}")
    String subtract(@PathVariable("itemId") long id, @PathVariable("amount") int amount);

    @PostMapping("stock/add/{itemId}/{amount}")
    String add(@PathVariable("itemId") long id, @PathVariable("amount") int amount);

    @PostMapping("stock/findStock/{itemId}")
    int findStock(@PathVariable("itemId") long itemId);
}
