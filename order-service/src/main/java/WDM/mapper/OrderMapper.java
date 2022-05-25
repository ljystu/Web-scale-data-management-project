package WDM.mapper;

import WDM.pojo.Item;
import WDM.pojo.Order;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


public interface OrderMapper {

    @Insert("insert into orderinfo(orderid, userid, paid) values(#{orderId}, #{userId}, false)")
    Boolean createOrder(String userId, String orderId);

    ///orders/remove/{order_id}
    //    DELETE - deletes an order by ID
    @Delete({"delete from orderinfo where orderid = #{orderId};", "delete from orderitem where orderid = #{orderId};"})
    Boolean removeOrder(String orderId);

    ///orders/find/{order_id}
    //    GET - retrieves the information of an order (id, payment status, items included and user id)
    //    Output JSON fields:
    //            “order_id”  - the order’s id
    //            “paid” (true/false)
    //            “items”  - list of item ids that are included in the order
    //            “user_id”  - the user’s id that made the order
    //            “total_cost” - the total cost of the items in the order
    @Select("select * from orderinfo where orderid = #{orderId};")
    Order findOrder(String orderId);


    ///orders/checkout/{order_id}
    // add one new column "amount" to orderitem to indicate purchase amount (makes comparison easier.)
    //    POST - makes the payment (via calling the payment service), subtracts the stock (via the stock service) and returns a status (success/failure).

//    Boolean checkout(String orderId);
}