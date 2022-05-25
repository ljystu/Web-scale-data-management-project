package feign.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Item {
    String orderId;
    String itemId;
    double price;
    int amount;
}

