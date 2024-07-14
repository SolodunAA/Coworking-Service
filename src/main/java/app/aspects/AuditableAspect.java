package app.aspects;

import app.dao.AuditDao;
import app.dto.AuditItem;
import app.start.CoworkingApp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AuditableAspect {

    @Pointcut("within(@app.annotations.Auditable *) && execution(* * (..))")
    public void annotatedByAuditable() {
    }

    @Around("annotatedByAuditable()")
    public Object audit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            Object res = proceedingJoinPoint.proceed();
            Signature signature = proceedingJoinPoint.getSignature();
            String user = resolveUser(proceedingJoinPoint);
            AuditDao auditDao = CoworkingApp.SERVICES_FACTORY.getDaoFactory().getAuditDao();
            auditDao.addAuditItem(new AuditItem(user, signature.toString()));
            return res;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String resolveUser(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            HttpServletRequest request = (HttpServletRequest) proceedingJoinPoint.getArgs()[0];
            HttpSession session = request.getSession();
            if (session == null) {
                return "unknown";
            }
            String login = (String) session.getAttribute("login");
            if (login != null && !login.isEmpty()) {
                return login;
            }
        } catch (Exception e) {
        }
        return "unknown";
    }
}