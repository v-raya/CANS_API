package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.changelog.AbstractChangeLogDto;
import gov.ca.cwds.cans.domain.dto.changelog.ChangeLogDtoFactory;
import gov.ca.cwds.cans.domain.dto.changelog.ChangeLogDtoParameters;
import gov.ca.cwds.cans.domain.entity.Persistent;
import gov.ca.cwds.cans.domain.entity.envers.NsRevisionEntity;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;

/**
 * Change log service
 *
 * @author CWDS API Team
 */
public class ChangeLogService {

  private SessionFactory sessionFactory;
  @Inject private PersonService personService;

  @Inject
  public ChangeLogService(@CansSessionFactory final SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private AuditReader getAuditReader() {
    return AuditReaderFactory.get(sessionFactory.getCurrentSession());
  }

  @SuppressWarnings({"unchecked", "fb-contrib:CLI_CONSTANT_LIST_INDEX"})
  public <E extends Persistent, D extends AbstractChangeLogDto> List<D> getChageLog4EntityById(
      final Class<E> entityClass, final Long id, final Class<D> dtoClass) {
    List<Object[]> revisions = queryAudit4changeLog(entityClass, id);
    List<D> changeLog = new ArrayList<>();
    D changeLogDto;
    E prevEntity = null;
    ChangeLogDtoParameters<E> dtoParams;
    for (Object[] revision : revisions) {
      dtoParams =
          new ChangeLogDtoParameters<E>()
              .setPrevious(prevEntity)
              .setCurrent((E) revision[0])
              .setRevisionEntity((NsRevisionEntity) revision[1])
              .setRevisionType((RevisionType) revision[2]);
      dtoParams.setUser(personService.findByExternalId(dtoParams.getRevisionEntity().getUserId()));

      changeLogDto = ChangeLogDtoFactory.newInstance(dtoClass, dtoParams);
      if (changeLogDto != null) {
        changeLog.add(changeLogDto);
        prevEntity = (E) revision[0];
      }
    }
    Collections.reverse(changeLog.subList(0, changeLog.size()));
    return changeLog;
  }

  private <E extends Persistent> List<Object[]> queryAudit4changeLog(
      final Class<E> entityClass, Long id) {
    AuditReader auditReader = getAuditReader();
    if (!auditReader.isEntityNameAudited(entityClass.getName())) {
      return Collections.emptyList();
    }
    return auditReader
        .createQuery()
        .forRevisionsOfEntity(entityClass, false, true)
        .addOrder(AuditEntity.revisionProperty("timestamp").asc())
        .add(AuditEntity.id().eq(id))
        .getResultList();
  }
}
