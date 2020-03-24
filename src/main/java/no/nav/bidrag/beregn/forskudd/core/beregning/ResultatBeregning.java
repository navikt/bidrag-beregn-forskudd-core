package no.nav.bidrag.beregn.forskudd.core.beregning;

import java.math.BigDecimal;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode;

public class ResultatBeregning {

  private final BigDecimal resultatBelop;
  private final ResultatKode resultatKode;
  private final String resultatBeskrivelse;

  public ResultatBeregning(BigDecimal resultatBelop, ResultatKode resultatKode, String resultatBeskrivelse) {
    this.resultatBelop = resultatBelop;
    this.resultatKode = resultatKode;
    this.resultatBeskrivelse = resultatBeskrivelse;
  }

  public BigDecimal getResultatBelop() {
    return resultatBelop;
  }

  public ResultatKode getResultatKode() {
    return resultatKode;
  }

  public String getResultatBeskrivelse() {
    return resultatBeskrivelse;
  }

  // Sjekker om 2 tilgrensende perioder kan merges fordi resultatet er det samme
  public boolean kanMergesMed(ResultatBeregning periodeResultatForrige) {
    return ((this.getResultatBeskrivelse().equals(periodeResultatForrige.getResultatBeskrivelse())) &&
        (this.getResultatKode().equals(periodeResultatForrige.getResultatKode())) &&
        (this.getResultatBelop().equals(periodeResultatForrige.getResultatBelop())));
  }
}
