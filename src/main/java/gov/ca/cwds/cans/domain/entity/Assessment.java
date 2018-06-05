package gov.ca.cwds.cans.domain.entity;

import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.enumeration.AssessmentType;
import gov.ca.cwds.cans.domain.json.AssessmentJson;
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
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

/** An Assessment. */
@Entity
@Table(name = "assessment")
@Data
@Accessors(chain = true)
public class Assessment implements Persistent<Long> {

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

  @Column(name = "submitted_timestamp")
  private LocalDateTime submittedTimestamp;

  @ManyToOne
  @JoinColumn(name = "submitted_by")
  private Person submittedBy;
}
