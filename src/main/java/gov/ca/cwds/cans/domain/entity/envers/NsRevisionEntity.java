package gov.ca.cwds.cans.domain.entity.envers;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
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

  // Getters, setters, equals, hashCode ...
  public long getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Transient
  public Date getRevisionDate() {
    return new Date(timestamp);
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NsRevisionEntity that = (NsRevisionEntity) o;
    return id == that.id && timestamp == that.timestamp && Objects.equals(userId, that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, timestamp, userId);
  }

  @Override
  public String toString() {
    return "NsRevisionEntity(id = "
        + id
        + ", revisionDate = "
        + DateFormat.getDateTimeInstance().format(getRevisionDate())
        + ", userId = "
        + userId
        + ")";
  }
}
