package gov.ca.cwds.cans.domain.entity;

import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.enumeration.AssessmentType;
import gov.ca.cwds.cans.domain.json.AssessmentJson;
import gov.ca.cwds.data.persistence.PersistentObject;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

/** An Assessment. */
@Entity
@Table(name = "assessment")
@Data
public class Assessment implements PersistentObject {

  private static final long serialVersionUID = 4921833959434495906L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private Long id;

  @Type(type = "AssessmentJsonType")
  @Column(name = "state")
  private AssessmentJson state;

  @Enumerated(EnumType.STRING)
  @Column(name = "assessment_type")
  private AssessmentType assessmentType;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private AssessmentStatus status;

  @Column(name = "instrument_id", insertable = false, updatable = false)
  private Long instrumentId;

  @ManyToOne private Instrument instrument;

  @ManyToOne private Person person;

  @ManyToOne private Cft cft;

  @Column(
      name = "create_timestamp",
      nullable = false,
      updatable = false
  )
  @CreationTimestamp
  private LocalDateTime createTimestamp;

  @Column(
      name = "update_timestamp",
      insertable = false
  )
  @UpdateTimestamp
  private LocalDateTime updateTimestamp;

  @Override
  public Serializable getPrimaryKey() {
    return id;
  }
}
