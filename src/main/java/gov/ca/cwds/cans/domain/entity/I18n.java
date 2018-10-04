package gov.ca.cwds.cans.domain.entity;

import static gov.ca.cwds.cans.domain.entity.I18n.NQ_FIND_BY_KEY_PREFIX_AND_LANG;
import static gov.ca.cwds.cans.domain.entity.I18n.NQ_PARAM_KEY_PREFIX;
import static gov.ca.cwds.cans.domain.entity.I18n.NQ_PARAM_LANG;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/** A I18n. */
@Entity
@Table(name = "i_18_n")
@IdClass(I18n.PrimaryKey.class)
@NamedQuery(
    name = NQ_FIND_BY_KEY_PREFIX_AND_LANG,
    query =
        "select i from I18n i "
            + " where i.k like :"
            + NQ_PARAM_KEY_PREFIX
            + " and i.lang = :"
            + NQ_PARAM_LANG
            + " order by i.k")
@Data
@Accessors(chain = true)
public class I18n implements Persistent<I18n.PrimaryKey> {

  public static final String NQ_FIND_BY_KEY_PREFIX_AND_LANG = "I18n.findByKeyPrefixAndLang";
  public static final String NQ_PARAM_KEY_PREFIX = "keyPrefix";
  public static final String NQ_PARAM_LANG = "lang";
  private static final long serialVersionUID = -6123477943320746982L;

  @Column(name = "lang")
  @Id
  private String lang;

  @Column(name = "k")
  @Id
  private String k;

  @Column(name = "v")
  private String v;

  @Override
  public I18n.PrimaryKey getId() {
    return new I18n.PrimaryKey(lang, k);
  }

  @Override
  public I18n setId(I18n.PrimaryKey pk) {
    if (pk != null) {
      this.lang = pk.lang;
      this.k = pk.k;
    }
    return this;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PrimaryKey implements Serializable {

    private static final long serialVersionUID = -6836958514931631389L;

    private String lang;
    private String k;
  }
}
