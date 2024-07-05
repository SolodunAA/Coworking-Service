package app.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
@Aspect
public class LoggableAspect {
    @Pointcut("within(@app.annotations.Loggable *) && execution(* * (..))")
    public void annotatedByLoggable() {}

    @Around("annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("calling method " + proceedingJoinPoint.getSignature());
        long start = System.currentTimeMillis();
        Object res = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis() - start;
        System.out.println("Execution of " + proceedingJoinPoint.getSignature() +
                " finished in " + end + " millis.");
        return res;
    }
}
