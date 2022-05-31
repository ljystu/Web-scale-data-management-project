package WDM;


import feign.clients.PaymentClient;
import feign.clients.StockClient;
import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Scanner;


@MapperScan("WDM.mapper")
@SpringBootApplication
@EnableFeignClients(clients = {StockClient.class, PaymentClient.class})
//@EnableAutoDataSourceProxy    // 开启seata
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}