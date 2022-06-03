package WDM;


import feign.clients.PaymentClient;
import feign.clients.StockClient;
import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@MapperScan("WDM.mapper")
@SpringBootApplication
@EnableFeignClients(clients = {StockClient.class, PaymentClient.class})
@EnableAutoDataSourceProxy

public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}