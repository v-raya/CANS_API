package gov.ca.cwds.cans.service;

import gov.ca.cwds.cans.dao.CrudDao;
import gov.ca.cwds.cans.domain.entity.Persistent;
import gov.ca.cwds.cans.util.Require;

/** @author denys.davydov */
public abstract class AbstractCrudService<T extends Persistent> {

  protected CrudDao dao;

  protected AbstractCrudService(CrudDao dao) {
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
