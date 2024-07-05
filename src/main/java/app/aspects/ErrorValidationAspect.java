package app.aspects;

import app.dao.AuditDao;
import app.dto.AuditItem;
import app.start.CoworkingApp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
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
            return proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            HttpServletResponse response = (HttpServletResponse) proceedingJoinPoint.getArgs()[1];
            response.setStatus(500);
            response.setContentType("application/json");
            String res = "Something went wrong. Try again";
            response.getOutputStream().write(res.getBytes());
        }
        return null;
    }
}
