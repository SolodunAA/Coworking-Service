package app.aspects;

import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ErrorValidationAspect {
    @Pointcut("within(@app.annotations.Exceptionable *) && execution(* * (..))")
    public void annotatedByExceptionable() {}

    @Around("annotatedByExceptionable()")
    public Object errorChecking(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            Object res = proceedingJoinPoint.proceed();
            return res;
        } catch (Throwable e) {
            HttpServletResponse response = (HttpServletResponse) proceedingJoinPoint.getArgs()[1];
            response.setStatus(500);
            response.setContentType("application/json");
            String res = "Total error";
            response.getOutputStream().write(res.getBytes());
            System.out.println("Method " + proceedingJoinPoint.getSignature() + " has exception " + e.getMessage());
        }
        return null;
    }
}
