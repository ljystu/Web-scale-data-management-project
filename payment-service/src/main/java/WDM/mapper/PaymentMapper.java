package WDM.mapper;

import WDM.pojo.Payment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


public interface PaymentMapper {

    @Update("update payment set credit = credit - #{funds} where userid = #{id}")
    Boolean pay(long id, double funds);

    @Update("update payment set credit = credit + #{funds} where userid = #{id}")
    Boolean add(long id, double funds);

    @Insert("insert into payment(userid, credit) values(#{userId}, 0)")
    Boolean create(long userId);

    @Select("select * from payment where userid = #{id}")
    Payment queryById(long id);
}
