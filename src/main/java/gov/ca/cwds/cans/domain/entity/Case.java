package gov.ca.cwds.cans.domain.entity;

import static gov.ca.cwds.cans.domain.entity.Case.NQ_FIND_BY_EXTERNAL_ID;
import static gov.ca.cwds.cans.domain.entity.Case.NQ_PARAM_EXTERNAL_ID;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

/** @author denys.davydov */
@Entity
@Table(name = "cases")
@NamedQuery(
    name = NQ_FIND_BY_EXTERNAL_ID,
    query = "from gov.ca.cwds.cans.domain.entity.Case c where c.externalId =:" + NQ_PARAM_EXTERNAL_ID
)
@Data
@Accessors(chain = true)
public class Case implements Persistent<Long> {
  private static final long serialVersionUID = 4921833959434495906L;

  public static final String NQ_FIND_BY_EXTERNAL_ID = "Case.findByExternalId";
  public static final String NQ_PARAM_EXTERNAL_ID = "externalId";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private Long id;

  @Column(name = "external_id")
  private String externalId;

  @Column(name = "created_timestamp", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdTimestamp;

  @ManyToOne
  @JoinColumn(name = "created_by", updatable = false, nullable = false)
  private Person createdBy;

}
