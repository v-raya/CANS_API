package gov.ca.cwds.cans.domain.entity;

import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_CREATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_CREATED_UPDATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_PERSON_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.NQ_ALL;
import static gov.ca.cwds.cans.domain.entity.Assessment.NQ_ALL_FOR_CLIENT;
import static gov.ca.cwds.cans.domain.entity.Assessment.NQ_ALL_FOR_CLIENT_WITH_DELETED;
import static gov.ca.cwds.cans.domain.entity.Assessment.NQ_FIND_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_CLIENT_IDENTIFIER;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_CREATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_CREATED_UPDATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_PERSON_ID;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.enumeration.AssessmentType;
import gov.ca.cwds.cans.domain.enumeration.CompletedAs;
import gov.ca.cwds.cans.domain.enumeration.ServiceSource;
import gov.ca.cwds.cans.domain.json.AssessmentJson;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

/** An Assessment. */
@Audited(targetAuditMode = NOT_AUDITED)
@Entity
@Table(name = "assessment")
@SQLDelete(
    sql = "UPDATE {h-schema}assessment SET status = 'DELETED' WHERE id = ?",
    check = ResultCheckStyle.COUNT)
@Loader(namedQuery = NQ_FIND_BY_ID)
@NamedQuery(name = NQ_FIND_BY_ID, query = "FROM Assessment WHERE id = ? AND status <> 'DELETED'")
@Where(clause = "status <> 'DELETED'")
@NamedQuery(name = NQ_ALL, query = "FROM Assessment a order by status desc, event_date desc")
// HQL @Where is applied - Doesn't return DELETED records
@NamedQuery(
    name = NQ_ALL_FOR_CLIENT,
    query =
        "FROM Assessment a WHERE person.externalId = :"
            + PARAM_CLIENT_IDENTIFIER
            + "  ORDER by status desc, event_date desc")

// SQL @Where is not applied - Does return DELETED records
@NamedNativeQuery(
    name = NQ_ALL_FOR_CLIENT_WITH_DELETED,
    query =
        "SELECT a.* FROM assessment a "
            + " INNER JOIN person p ON p.id = a.person_id"
            + " WHERE p.external_id = :"
            + PARAM_CLIENT_IDENTIFIER
            + " ORDER by status desc, event_date desc",
    resultClass = Assessment.class)
@FilterDef(
    name = FILTER_CREATED_BY_ID,
    parameters = @ParamDef(name = PARAM_CREATED_BY_ID, type = "long"))
@FilterDef(
    name = FILTER_CREATED_UPDATED_BY_ID,
    parameters = @ParamDef(name = PARAM_CREATED_UPDATED_BY_ID, type = "long"))
@FilterDef(name = FILTER_PERSON_ID, parameters = @ParamDef(name = PARAM_PERSON_ID, type = "long"))
@Filter(name = FILTER_CREATED_BY_ID, condition = "created_by = :" + PARAM_CREATED_BY_ID)
@Filter(
    name = FILTER_CREATED_UPDATED_BY_ID,
    condition =
        "( created_by = :"
            + PARAM_CREATED_UPDATED_BY_ID
            + " OR updated_by = :"
            + PARAM_CREATED_UPDATED_BY_ID
            + " )")
@Filter(name = FILTER_PERSON_ID, condition = "person_id = :" + PARAM_PERSON_ID)
@Data
@Accessors(chain = true)
public class Assessment implements Persistent<Long> {

  public static final String NQ_FIND_BY_ID = "gov.ca.cwds.cans.domain.entity.Assessment.findById";
  public static final String NQ_ALL = "gov.ca.cwds.cans.domain.entity.Assessment.findAll";
  public static final String NQ_ALL_FOR_CLIENT =
      "gov.ca.cwds.cans.domain.entity.Assessment.findAllForClient";
  public static final String NQ_ALL_FOR_CLIENT_WITH_DELETED =
      "gov.ca.cwds.cans.domain.entity.Assessment.findAllForClientWithDeletes";
  public static final String FILTER_CREATED_BY_ID = "createdByFilter";
  public static final String PARAM_CREATED_BY_ID = "createdBy";
  public static final String FILTER_CREATED_UPDATED_BY_ID = "createdByUpdatedByFilter";
  public static final String PARAM_CREATED_UPDATED_BY_ID = "createdByUpdatedBy";
  public static final String FILTER_PERSON_ID = "personIdFilter";
  public static final String PARAM_PERSON_ID = "personId";
  public static final String PARAM_CLIENT_IDENTIFIER = "clientIdentifier";
  private static final long serialVersionUID = 4921833959434495906L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private Long id;

  @Type(type = "AssessmentJsonType")
  @Column(name = "state")
  private AssessmentJson state;

  @Column(name = "event_date")
  private LocalDate eventDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "assessment_type")
  private AssessmentType assessmentType;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private AssessmentStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "completed_as")
  private CompletedAs completedAs;

  @Column(name = "service_source_id")
  private String serviceSourceId;

  @Enumerated(EnumType.STRING)
  @Type(type = "PostgreSqlEnum")
  @Column(name = "service_source")
  private ServiceSource serviceSource;

  @Column(name = "can_release_confidential_info")
  private Boolean canReleaseConfidentialInfo;

  @Column(name = "has_caregiver")
  private Boolean hasCaregiver;

  @Column(name = "instrument_id", insertable = false, updatable = false)
  private Long instrumentId;

  @ManyToOne private Instrument instrument;

  @ManyToOne private Person person;

  @ManyToOne private County county;

  @Column(name = "created_timestamp", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdTimestamp;

  @ManyToOne
  @JoinColumn(name = "created_by", updatable = false, nullable = false)
  private Person createdBy;

  @Column(name = "updated_timestamp", insertable = false)
  @UpdateTimestamp
  private LocalDateTime updatedTimestamp;

  @ManyToOne
  @JoinColumn(name = "updated_by")
  private Person updatedBy;

  @Column(name = "completed_timestamp")
  private LocalDateTime completedTimestamp;

  @ManyToOne
  @JoinColumn(name = "completed_by")
  private Person completedBy;

  @Column(name = "conducted_by")
  private String conductedBy;
}
