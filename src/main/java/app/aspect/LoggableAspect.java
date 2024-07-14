package app.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Scope("singleton")
public class LoggableAspect {
    @Pointcut("within(@app.annotation.Loggable *) && execution(* * (..))")
    public void annotatedByLoggable() {}

    @Around("annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            System.out.println("calling method " + proceedingJoinPoint.getSignature());
            long start = System.currentTimeMillis();
            Object res = proceedingJoinPoint.proceed();
            long end = System.currentTimeMillis() - start;
            System.out.println("Execution of " + proceedingJoinPoint.getSignature() +
                    " finished in " + end + " millis.");
            return res;

        } catch (Throwable e) {
            System.out.println("Failed to create log. Internal error " + e);
            return ResponseEntity
                    .status(500)
                    .body("internal error");
        }
    }

}