package gov.ca.cwds.cans.domain.entity.envers;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

/**
 * NS revision entity.
 *
 * @author CWDS API Team
 */
@Entity
@RevisionEntity(NsRevisionListener.class)
@Table(name = "REVINFO")
@Data
@Accessors(chain = true)
public class NsRevisionEntity implements Serializable {

  private static final long serialVersionUID = 707148398853634667L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @RevisionNumber
  @Column(name = "REV")
  private long id;

  @RevisionTimestamp
  @Column(name = "REVTSTMP")
  private long timestamp;

  @Column(name = "USER_ID")
  private String userId;

  @Transient
  public LocalDateTime getRevisionDate() {
    return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }
}
