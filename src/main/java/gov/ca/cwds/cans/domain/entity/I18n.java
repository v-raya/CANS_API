package gov.ca.cwds.cans.domain.entity;

import gov.ca.cwds.data.persistence.PersistentObject;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

/** A I18n. */
@Entity
@Table(name = "i_18_n")
@Data
public class I18n implements PersistentObject {

  private static final long serialVersionUID = -6123477943320746982L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private Long id;

  @Column(name = "lang")
  private String lang;

  @Column(name = "k")
  private String k;

  @Column(name = "v")
  private String v;

  @Override
  public Serializable getPrimaryKey() {
    return id;
  }
}
