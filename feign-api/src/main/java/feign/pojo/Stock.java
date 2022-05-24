package feign.pojo;

import lombok.Data;

@Data
public class Stock {
    private String itemId;
    private double price;
    private int amount;
}
