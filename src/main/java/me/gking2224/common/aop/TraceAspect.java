package me.gking2224.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import me.gking2224.common.utils.ExceptionUtils;

@Aspect
@Component
public class TraceAspect {
    
    private static Logger logger = LoggerFactory.getLogger(TraceAspect.class);

    @Pointcut("execution(* me.gking2224..service.*Service.*(..))")
    public void serviceMethod() {}

    @Pointcut("execution(* me.gking2224..dao.*Dao.*(..))")
    public void daoMethod() {}

    @Pointcut("execution(* me.gking2224..web.mvc.*Controller.*(..))")
    public void controllerMethod() {}

    @Pointcut("serviceMethod() || daoMethod() || controllerMethod()")
    public void mainLayers() {}
    
    @Around("mainLayers()")
    public Object logBefore(ProceedingJoinPoint joinPoint) throws Throwable{
        Signature signature = joinPoint.getSignature();
        logger.trace(String.format("--> %s", signature));
        try {
            Object rv = joinPoint.proceed();
            logger.trace(String.format("<-- %s - return %s", signature, rv));
            return rv;
        }
        catch (Throwable t) {
            Throwable rootCause = t.getCause() == null ? t : ExceptionUtils.getRootCause(t);
            logger.trace(String.format("<!!-- %s - threw %s (root cause = %s)", signature, t, rootCause));
            throw t;
        }
    }
}
