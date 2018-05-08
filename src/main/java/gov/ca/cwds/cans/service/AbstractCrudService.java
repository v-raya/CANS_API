package gov.ca.cwds.cans.service;

import gov.ca.cwds.cans.util.Require;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.persistence.PersistentObject;

/**
 * @author denys.davydov
 */
public abstract class AbstractCrudService<T extends PersistentObject> {

  protected BaseDaoImpl dao;

  protected AbstractCrudService(BaseDaoImpl dao) {
    this.dao = dao;
  }

  public T create(final T entity) {
    Require.requireNotNullAndNotEmpty(entity);
    return (T) dao.create(entity);
  }

  public T read(final Long id) {
    Require.requireNotNullAndNotEmpty(id);
    return (T) dao.find(id);
  }

  public T update(final T entity) {
    Require.requireNotNullAndNotEmpty(entity);
    return (T) dao.update(entity);
  }

  public T delete(final Long id) {
    Require.requireNotNullAndNotEmpty(id);
    return (T) dao.delete(id);
  }
}
