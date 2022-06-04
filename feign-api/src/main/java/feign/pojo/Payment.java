package feign.pojo;

import lombok.Data;

@Data
public class Payment {
    private long userId;
    private double credit;
}