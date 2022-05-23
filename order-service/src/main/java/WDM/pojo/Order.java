package WDM.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private String orderId;
    private double totalCost;
    private boolean paid;
    private String userId;
    private List<String> itemId;
}

