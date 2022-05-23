package WDM.mapper;

import WDM.pojo.Payment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;


public interface PaymentMapper {

    @Update("update payment set credit = credit + #{funds} where userid = #{id}")
    Boolean add(String id, int funds);

    @Insert("insert into user(userid, credit) values(#{id}, 0)")
    String create();

    @Select("select * from user where userid = #{id}")
    Payment queryById(String id);
}
