package feign.pojo;

import lombok.Data;

@Data
public class Payment {
    private String userId;
    private double credit;
}