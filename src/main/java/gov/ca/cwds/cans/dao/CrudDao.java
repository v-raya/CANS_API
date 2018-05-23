package gov.ca.cwds.cans.dao;

import gov.ca.cwds.cans.domain.entity.Persistent;
import java.io.Serializable;
import java.util.List;
import org.hibernate.SessionFactory;

/**
 * @author denys.davydov
 */
public interface CrudDao <T extends Persistent> {
  T find(Serializable primaryKey);
  T delete(Serializable id);
  T create(T object);
  T update(T object);
  List<T> findAll();

  SessionFactory getSessionFactory();
}