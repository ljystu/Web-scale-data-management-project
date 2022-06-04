package feign.pojo;

import lombok.Data;

@Data
public class Stock {
    private long itemId;
    private double price;
    private int amount;
}
