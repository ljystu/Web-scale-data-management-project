package WDM.config;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
/**
 * @author jxys
 * @since 2020年6月8日09:37:55
 * 类描述： 用于处理程序调用发生异常的时候由于异常被处理以后无法触发事务，而进行的处理，使之可以正常的触发事务。
 */
@Aspect
@Component
public class WorkAspect {
    private final static Logger logger = LoggerFactory.getLogger(WorkAspect.class);

    @Before("execution(* WDM.service.Impl.StockServiceImpl.subtract())")
    public void before(JoinPoint joinPoint) throws TransactionException {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        logger.info("拦截到需要分布式事务的方法," + method.getName());
        // 此处可用redis或者定时任务来获取一个key判断是否需要关闭分布式事务
        // 模拟动态关闭分布式事务
        if ((int)(Math.random() * 100) % 2 == 0) {
            GlobalTransaction tx = GlobalTransactionContext.getCurrentOrCreate();
            tx.begin(300000, "test-client");
        } else {
            logger.info("关闭分布式事务");
        }
    }
    @AfterThrowing(throwing = "e", pointcut = "execution(* WDM.service.Impl.StockServiceImpl.subtract())")
    public void doRecoveryActions(Throwable e) throws TransactionException {
        logger.info("方法执行异常:{}", e.getMessage());
        if (!StringUtils.isBlank(RootContext.getXID()))
            GlobalTransactionContext.reload(RootContext.getXID()).rollback();
    }

    @AfterReturning(value = "execution(* WDM.service.Impl.StockServiceImpl.subtract())", returning = "result")
    public void afterReturning(JoinPoint point, Object result) throws TransactionException {
        logger.info("方法执行结束:{}", result);
        if ((Boolean)result) {
            if (!StringUtils.isBlank(RootContext.getXID())) {
                logger.info("分布式事务Id:{}", RootContext.getXID());
                GlobalTransactionContext.reload(RootContext.getXID()).commit();
            }
        }
    }

}
