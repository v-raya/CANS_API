package gov.ca.cwds.cans.domain.entity;

import static gov.ca.cwds.cans.domain.entity.Person.AUTHORIZATION_FILTER;
import static gov.ca.cwds.cans.domain.entity.Person.FILTER_COUNTY;
import static gov.ca.cwds.cans.domain.entity.Person.FILTER_DOB;
import static gov.ca.cwds.cans.domain.entity.Person.FILTER_EXTERNAL_ID;
import static gov.ca.cwds.cans.domain.entity.Person.FILTER_FIRST_NAME;
import static gov.ca.cwds.cans.domain.entity.Person.FILTER_LAST_NAME;
import static gov.ca.cwds.cans.domain.entity.Person.FILTER_MIDDLE_NAME;
import static gov.ca.cwds.cans.domain.entity.Person.FILTER_PERSON_ROLE;
import static gov.ca.cwds.cans.domain.entity.Person.NQ_ALL;
import static gov.ca.cwds.cans.domain.entity.Person.NQ_COUNT_ALL;
import static gov.ca.cwds.cans.domain.entity.Person.NQ_FIND_BY_EXTERNAL_ID;
import static gov.ca.cwds.cans.domain.entity.Person.PARAM_DOB;
import static gov.ca.cwds.cans.domain.entity.Person.PARAM_EXTERNAL_ID;
import static gov.ca.cwds.cans.domain.entity.Person.PARAM_FIRST_NAME;
import static gov.ca.cwds.cans.domain.entity.Person.PARAM_LAST_NAME;
import static gov.ca.cwds.cans.domain.entity.Person.PARAM_MIDDLE_NAME;
import static gov.ca.cwds.cans.domain.entity.Person.PARAM_PERSON_ROLE;
import static gov.ca.cwds.cans.domain.entity.Person.PARAM_USERS_COUNTY_EXTERNAL_ID;

import gov.ca.cwds.cans.domain.entity.facade.Statistics;
import gov.ca.cwds.cans.domain.enumeration.Gender;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.domain.enumeration.Race;
import gov.ca.cwds.cans.domain.enumeration.SensitivityType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
import org.hibernate.annotations.Type;

/** A Person. */
@Entity
@Table(name = "person")
@Data
@Accessors(chain = true)
@NamedQuery(name = NQ_ALL, query = "FROM Person p order by p.lastName ASC, p.firstName ASC")
@NamedQuery(name = NQ_COUNT_ALL, query = "select count(p) FROM Person p")
@NamedQuery(
    name = NQ_FIND_BY_EXTERNAL_ID,
    query = "FROM Person p WHERE p.externalId =:" + PARAM_EXTERNAL_ID)
@FilterDef(
    name = FILTER_PERSON_ROLE,
    parameters = @ParamDef(name = PARAM_PERSON_ROLE, type = "string"))
@FilterDef(
    name = FILTER_EXTERNAL_ID,
    parameters = @ParamDef(name = PARAM_EXTERNAL_ID, type = "string"))
@FilterDef(
    name = FILTER_FIRST_NAME,
    parameters = @ParamDef(name = PARAM_FIRST_NAME, type = "string"))
@FilterDef(
    name = FILTER_MIDDLE_NAME,
    parameters = @ParamDef(name = PARAM_MIDDLE_NAME, type = "string"))
@FilterDef(name = FILTER_LAST_NAME, parameters = @ParamDef(name = PARAM_LAST_NAME, type = "string"))
@FilterDef(
    name = FILTER_DOB,
    parameters = @ParamDef(name = PARAM_DOB, type = "java.time.LocalDate"))
@FilterDef(
    name = FILTER_COUNTY,
    parameters = @ParamDef(name = PARAM_USERS_COUNTY_EXTERNAL_ID, type = "string"))
@FilterDef(name = AUTHORIZATION_FILTER)
@Filter(name = FILTER_PERSON_ROLE, condition = "person_role = :" + PARAM_PERSON_ROLE)
@Filter(name = FILTER_EXTERNAL_ID, condition = "external_id = :" + PARAM_EXTERNAL_ID)
@Filter(name = FILTER_FIRST_NAME, condition = "LOWER(first_name) like :" + PARAM_FIRST_NAME)
@Filter(name = FILTER_MIDDLE_NAME, condition = "LOWER(middle_name) like :" + PARAM_MIDDLE_NAME)
@Filter(name = FILTER_LAST_NAME, condition = "LOWER(last_name) like :" + PARAM_LAST_NAME)
@Filter(name = FILTER_DOB, condition = "dob = :" + PARAM_DOB)
@Filter(
    name = FILTER_COUNTY,
    condition =
        "county_id IN (SELECT county.id FROM cans.county WHERE county.external_id = :"
            + PARAM_USERS_COUNTY_EXTERNAL_ID
            + ")")
@Filter(
    name = AUTHORIZATION_FILTER,
    condition = "(sensitivity_type <> 'SEALED'  OR sensitivity_type IS NULL)")
@NamedQuery(
    name = Statistics.NQ_STAFF_ASSESSMENT_STATISTICS,
    query =
        "select NEW gov.ca.cwds.cans.domain.entity.facade.Statistics("
            + "  staff.externalId, "
            + "  count(case when a.status = 'IN_PROGRESS' then 1 else null end), "
            + "  count(case when a.status = 'SUBMITTED' then 1 else null end)) "
            + "from Assessment a left outer join a.createdBy staff "
            + "where staff.externalId in (:"
            + Statistics.NQ_PARAM_RACF_IDS
            + ") "
            + "group by staff.externalId")
public class Person implements Persistent<Long> {

  public static final String NQ_ALL = "gov.ca.cwds.cans.domain.entity.Person.findAll";
  public static final String NQ_COUNT_ALL = "gov.ca.cwds.cans.domain.entity.Person.countAll";
  public static final String NQ_FIND_BY_EXTERNAL_ID =
      "gov.ca.cwds.cans.domain.entity.Person.findByExternalId";
  public static final String FILTER_PERSON_ROLE = "personRoleFilter";
  public static final String FILTER_EXTERNAL_ID = "externalIdFilter";
  public static final String FILTER_FIRST_NAME = "firstNameFilter";
  public static final String FILTER_MIDDLE_NAME = "middleNameFilter";
  public static final String FILTER_LAST_NAME = "lastNameFilter";
  public static final String FILTER_DOB = "dobFilter";
  public static final String FILTER_COUNTY = "countyFilter";
  public static final String PARAM_PERSON_ROLE = "personRole";
  public static final String PARAM_EXTERNAL_ID = "externalId";
  public static final String PARAM_FIRST_NAME = "firstName";
  public static final String PARAM_MIDDLE_NAME = "middleName";
  public static final String PARAM_LAST_NAME = "lastName";
  public static final String PARAM_DOB = "dob";
  public static final String PARAM_USERS_COUNTY_EXTERNAL_ID = "usersCountyExternalId";
  public static final String AUTHORIZATION_FILTER = "authorizationFilter";
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

  @Column(name = "middle_name")
  private String middleName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "suffix")
  private String suffix;

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

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "person_cases",
      joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "case_id", referencedColumnName = "id"))
  private List<Case> cases = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  @Type(type = "PostgreSqlEnum")
  @Column(name = "sensitivity_type")
  private SensitivityType sensitivityType;
}
