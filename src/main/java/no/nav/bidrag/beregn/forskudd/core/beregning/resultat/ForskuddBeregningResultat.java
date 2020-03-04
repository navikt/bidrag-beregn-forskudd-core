package no.nav.bidrag.beregn.forskudd.core.beregning.resultat;

import java.math.BigDecimal;

public class ForskuddBeregningResultat {

  private final BigDecimal belop;
  private final ResultatKode resultatKode;
  private final String resultatBeskrivelse;

  public ForskuddBeregningResultat(BigDecimal belop, ResultatKode resultatKode, String resultatBeskrivelse) {
    this.belop = belop;
    this.resultatKode = resultatKode;
    this.resultatBeskrivelse = resultatBeskrivelse;
  }

  public BigDecimal getBelop() {
    return belop;
  }

  public ResultatKode getResultatKode() {
    return resultatKode;
  }

  public String getResultatBeskrivelse() {
    return resultatBeskrivelse;
  }

  public boolean kanMergesMed(ForskuddBeregningResultat periodeResultatForrige) {
    return ((this.getResultatBeskrivelse().equals(periodeResultatForrige.getResultatBeskrivelse())) &&
        (this.getResultatKode().equals(periodeResultatForrige.getResultatKode())) &&
        (this.getBelop().equals(periodeResultatForrige.getBelop())));
  }
}
