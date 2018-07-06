package gov.ca.cwds.cans.dao;

import com.google.common.collect.ImmutableList;
import gov.ca.cwds.cans.domain.entity.Persistent;
import gov.ca.cwds.cans.exception.DaoException;
import io.dropwizard.hibernate.AbstractDAO;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author denys.davydov
 */
public class AbstractCrudDao<T extends Persistent> extends AbstractDAO<T> implements CrudDao<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCrudDao.class);

  private SessionFactory sessionFactory;

  public AbstractCrudDao(SessionFactory sessionFactory) {
    super(sessionFactory);
    this.sessionFactory = sessionFactory;
  }

  @Override
  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public Session grabSession() {
    Session session;
    try {
      session = sessionFactory.getCurrentSession();
    } catch (HibernateException e) { //NOSONAR
      LOGGER.info("No hibernate session found, opening a new one: {}", e.getMessage());
      session = sessionFactory.openSession();
    }

    return session;
  }

  public Transaction joinTransaction(Session session) {
    Transaction txn = session.getTransaction();
    txn = txn != null ? txn : session.beginTransaction();

    if (TransactionStatus.NOT_ACTIVE == txn.getStatus() || !txn.isActive()) {
      txn.begin();
    }

    return txn;
  }

  @Override
  public T find(Serializable primaryKey) {
    grabSession();
    return get(primaryKey);
  }

  @Override
  public T delete(Serializable id) {
    final Session session = grabSession();
    T object = find(id);
    if (object != null) {
      session.delete(object);
    }
    return object;
  }

  @Override
  public T create(T object) {
    grabSession();
    if (object.getId() != null) {
      T databaseObject = find(object.getId());
      if (databaseObject != null) {
        String msg = MessageFormat.format("entity with id={0} already exists", object);
        LOGGER.error(msg);
        throw new EntityExistsException(msg);
      }
    }
    return persist(object);
  }

  @Override
  public T update(T object) {
    final Session session = grabSession();
    T databaseObject = find(object.getId());
    if (databaseObject == null) {
      String msg =
          MessageFormat.format("Unable to find entity with id={0}", object.getId());
      throw new EntityNotFoundException(msg);
    }
    session.evict(databaseObject);
    return persist(object);
  }

  @Override
  public List<T> findAll() {
    final String namedQueryName = constructNamedQueryName("findAll");
    final Session session = grabSession();
    final Transaction txn = joinTransaction(session);

    try {
      final List list = session.getNamedQuery(namedQueryName).list();
      final ImmutableList results = ImmutableList.copyOf(list);
      txn.commit();
      return results;
    } catch (HibernateException h) {
      txn.rollback();
      final String message = h.getMessage() + ". Transaction Status: " + txn.getStatus();
      throw new DaoException(message, h);
    }
  }

  protected String constructNamedQueryName(String suffix) {
    return getEntityClass().getName() + "." + suffix;
  }

}
