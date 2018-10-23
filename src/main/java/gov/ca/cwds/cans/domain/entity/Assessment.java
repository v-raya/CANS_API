package gov.ca.cwds.cans.domain.entity;

import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_CREATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_PERSON_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.NQ_ALL;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_CREATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_PERSON_ID;

import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.enumeration.AssessmentType;
import gov.ca.cwds.cans.domain.enumeration.CompletedAs;
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
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

/** An Assessment. */
@Entity
@Table(name = "assessment")
@NamedQuery(name = NQ_ALL, query = "FROM Assessment a order by status desc, event_date desc")
@FilterDef(
    name = FILTER_CREATED_BY_ID,
    parameters = @ParamDef(name = PARAM_CREATED_BY_ID, type = "long"))
@FilterDef(name = FILTER_PERSON_ID, parameters = @ParamDef(name = PARAM_PERSON_ID, type = "long"))
@Filter(name = FILTER_CREATED_BY_ID, condition = "created_by = :" + PARAM_CREATED_BY_ID)
@Filter(name = FILTER_PERSON_ID, condition = "person_id = :" + PARAM_PERSON_ID)
@Data
@Accessors(chain = true)
public class Assessment implements Persistent<Long> {
  private static final long serialVersionUID = 4921833959434495906L;

  public static final String NQ_ALL = "gov.ca.cwds.cans.domain.entity.Assessment.findAll";
  public static final String FILTER_CREATED_BY_ID = "createdByFilter";
  public static final String PARAM_CREATED_BY_ID = "createdBy";
  public static final String FILTER_PERSON_ID = "personIdFilter";
  public static final String PARAM_PERSON_ID = "personId";

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

  @Column(name = "can_release_confidential_info")
  private Boolean canReleaseConfidentialInfo;

  @Column(name = "has_caregiver")
  private Boolean hasCaregiver;

  @Column(name = "instrument_id", insertable = false, updatable = false)
  private Long instrumentId;

  @ManyToOne private Instrument instrument;

  @ManyToOne private Person person;

  @ManyToOne private Cft cft;

  @ManyToOne private County county;

  @ManyToOne
  @JoinColumn(name = "case_id")
  private Case theCase;

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
}
