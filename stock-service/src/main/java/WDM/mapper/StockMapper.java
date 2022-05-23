package WDM.mapper;

import WDM.pojo.Stock;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface StockMapper {
    @Select("select * from stock where itemid = #{id}")
    Stock queryById(String id);

    @Update("update stock set amount = amount - #{num} where itemid = #{id} and amount >= #{num}")
    Boolean subtract(String id, int num);

    @Update("update stock set amount = amount + #{num} where itemid = #{id}")
    Boolean add(String id, int num);

    @Insert("insert into stock(itemid, price) values(#{id}, #{price})")
    Boolean create(String id, double price);
}
