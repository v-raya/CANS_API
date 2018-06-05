package gov.ca.cwds.cans.domain.entity;

import gov.ca.cwds.cans.domain.json.AssessmentJson;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

/** A Instrument. */
@Entity
@Table(name = "instrument")
@Data
@Accessors(chain = true)
public class Instrument implements Persistent<Long> {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private Long id;

  @ManyToOne private County county;

  @Column(name = "prototype")
  @Type(type = "AssessmentJsonType")
  private AssessmentJson prototype;

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

}
