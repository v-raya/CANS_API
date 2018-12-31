package gov.ca.cwds.cans.validation;

import gov.ca.cwds.cans.domain.entity.Persistent;
import java.io.Serializable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/** @author CWDS TPT-2 Team */
public interface PersistentAware<T extends Persistent> {
  default T getPersisted(SessionFactory sessionFactory, Class<T> clazz, Serializable primaryKey) {
    Session session = sessionFactory.openSession();
    T entity;
    try {
      entity = session.get(clazz, primaryKey);
      if (entity != null) {
        session.detach(entity);
      }
    } finally {
      session.close();
    }
    return entity;
  }
}
