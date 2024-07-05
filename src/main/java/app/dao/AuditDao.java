package app.dao;

import app.dto.AuditItem;

import java.util.List;

/**
 * interface contains, add and show information about users actions
 */
public interface AuditDao {
    /**
     * add action in auditCache
     * @param auditItem action we want to add to auditCache
     */
    void addAuditItem(AuditItem auditItem);
    /**
     * @return audit record size
     */
    int AuditItemsSize();
    /**
     * @param limit number of records you want to see
     * @return List with "limit" user actions
     */
    List<AuditItem> getAuditItems(int limit);
}
