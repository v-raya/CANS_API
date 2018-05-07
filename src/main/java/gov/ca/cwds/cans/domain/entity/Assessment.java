package gov.ca.cwds.cans.domain.entity;

import gov.ca.cwds.cans.domain.json.AssessmentJson;
import gov.ca.cwds.data.persistence.PersistentObject;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.Type;

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

  @Column(name = "creation_date_time")
  private ZonedDateTime creationDateTime;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "json")
  @Type(type = "AssessmentJsonType")
  private AssessmentJson json;

  @ManyToOne private Template template;

  @ManyToOne private Person person;

  @ManyToOne(optional = false)
  @NotNull
  private Cft cft;

  @Override
  public Serializable getPrimaryKey() {
    return id;
  }
}
