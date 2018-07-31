package gov.ca.cwds.cans.domain.entity;

import static gov.ca.cwds.cans.domain.entity.Person.FILTER_EXTERNAL_ID;
import static gov.ca.cwds.cans.domain.entity.Person.FILTER_PERSON_ROLE;
import static gov.ca.cwds.cans.domain.entity.Person.NQ_ALL;
import static gov.ca.cwds.cans.domain.entity.Person.PARAM_EXTERNAL_ID;
import static gov.ca.cwds.cans.domain.entity.Person.PARAM_PERSON_ROLE;

import gov.ca.cwds.cans.domain.enumeration.Gender;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.domain.enumeration.Race;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.ParamDef;

/** A Person. */
@Entity
@Table(name = "person")
@Data
@Accessors(chain = true)
@NamedQuery(name = NQ_ALL, query = "FROM Person")
@FilterDef(
  name = FILTER_PERSON_ROLE,
  parameters = @ParamDef(name = PARAM_PERSON_ROLE, type = "string")
)
@FilterDef(
  name = FILTER_EXTERNAL_ID,
  parameters = @ParamDef(name = PARAM_EXTERNAL_ID, type = "string")
)
@Filter(name = FILTER_PERSON_ROLE, condition = "person_role = :" + PARAM_PERSON_ROLE)
@Filter(name = FILTER_EXTERNAL_ID, condition = "external_id = :" + PARAM_EXTERNAL_ID)
public class Person implements Persistent<Long> {

  public static final String NQ_ALL = "gov.ca.cwds.cans.domain.entity.Person.findAll";
  public static final String FILTER_PERSON_ROLE = "personRoleFilter";
  public static final String FILTER_EXTERNAL_ID = "externalIdFilter";
  public static final String PARAM_PERSON_ROLE = "personRole";
  public static final String PARAM_EXTERNAL_ID = "externalId";
  private static final long serialVersionUID = 8541617675397448400L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "person_role")
  private PersonRole personRole;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "external_id")
  private String externalId;

  @Column(name = "dob")
  private LocalDate dob;

  @Column(name = "estimated_dob")
  private Boolean estimatedDob;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender")
  private Gender gender;

  @Enumerated(EnumType.STRING)
  @Column(name = "race")
  private Race race;

  @Column(name = "county_client_number")
  private String countyClientNumber;

  @Column(name = "client_index_number")
  private String clientIndexNumber;

  @ManyToOne private County county;

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "persons")
  private Set<Cft> cfts = new HashSet<>();
}
