package WDM;


import feign.clients.PaymentClient;
import feign.clients.StockClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@MapperScan("WDM.mapper")
@SpringBootApplication
@EnableFeignClients(clients = {StockClient.class, PaymentClient.class})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}