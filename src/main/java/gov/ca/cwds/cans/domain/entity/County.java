package gov.ca.cwds.cans.domain.entity;

import static gov.ca.cwds.cans.domain.entity.County.NQ_ALL;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.NaturalId;

/** A County. */
@Entity
@Table(name = "county")
@Cacheable
@Data
@Accessors(chain = true)
@NamedQuery(name = NQ_ALL, query = "FROM County")
public class County implements Persistent<Long> {

  private static final long serialVersionUID = -4591007112034454956L;

  public static final String NQ_ALL = "gov.ca.cwds.cans.domain.entity.County.findAll";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private Long id;

  @Column(name = "name")
  private String name;

  @NaturalId
  @Column(name = "external_id")
  private String externalId;

  @Column(name = "export_id")
  private String exportId;
}
