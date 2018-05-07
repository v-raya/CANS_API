package gov.ca.cwds.cans.domain.entity;


import gov.ca.cwds.cans.domain.json.AssessmentJson;
import gov.ca.cwds.data.persistence.PersistentObject;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Type;

/**
 * A Template.
 */
@Entity
@Table(name = "template")
@Data
public class Template implements PersistentObject {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "version")
    private Integer version;

    @ManyToOne
    private County county;

    @Column(name = "prototype")
    @Type(type = "AssessmentJsonType")
    private AssessmentJson prototype;

    @Override
    public Serializable getPrimaryKey() {
        return id;
    }
}
