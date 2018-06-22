package gov.ca.cwds.cans.domain.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

/** A Cft. */
@Entity
@Table(name = "cft")
@Data
@Accessors(chain = true)
public class Cft implements Persistent<Long> {

  private static final long serialVersionUID = -4586157512284740531L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "days_in_program")
  private Integer daysInProgram;

  @Column(name = "event_date")
  private LocalDate eventDate;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "cft")
  private Set<Assessment> assessments = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "cft_persons",
    joinColumns = @JoinColumn(name = "cfts_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "persons_id", referencedColumnName = "id")
  )
  private Set<Person> persons = new HashSet<>();

}
