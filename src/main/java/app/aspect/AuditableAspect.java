package app.aspect;

import app.dao.AuditDao;
import app.dto.AuditItem;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Scope("singleton")
public class AuditableAspect {

    private final AuditDao auditDao;

    @Autowired
    public AuditableAspect(AuditDao auditDao) {
        this.auditDao = auditDao;
    }

    @Pointcut("within(@app.annotation.Auditable *) && execution(* * (..))")
    public void annotatedByAuditable() {
    }

    @Around("annotatedByAuditable()")
    public Object audit(ProceedingJoinPoint proceedingJoinPoint)  {
        try {
            Object res = proceedingJoinPoint.proceed();
            Signature signature = proceedingJoinPoint.getSignature();
            String user = resolveUser(proceedingJoinPoint);
            auditDao.addAuditItem(new AuditItem(user, signature.toString()));
            return res;
        } catch (Throwable e) {
            System.out.println("Failed to create audit line. Internal error " + e);
            return ResponseEntity
                    .status(500)
                    .body("Internal error");
        }
    }

    public String resolveUser(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            HttpSession session = (HttpSession) proceedingJoinPoint.getArgs()[0];
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