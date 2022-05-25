package WDM.mapper;

import WDM.pojo.Payment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;


public interface PaymentMapper {

    @Update("update payment set credit = credit - #{funds} where userid = #{id}")
    Boolean pay(String id, double funds);



    @Update("update payment set credit = credit + #{funds} where userid = #{id}")
    Boolean add(String id, int funds);

    @Insert("insert into payment(userid, credit) values(#{userId}, 0)")
    Boolean create(String userId);

    @Select("select * from payment where userid = #{id}")
    Payment queryById(String id);
}
