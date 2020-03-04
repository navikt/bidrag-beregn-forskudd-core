package no.nav.bidrag.beregn.forskudd.core.periode.resultat;

import java.util.List;

public class ForskuddPeriodeResultat {

  private final List<PeriodeResultat> periodeResultatListe;

  public ForskuddPeriodeResultat(List<PeriodeResultat> periodeResultatListe) {
    this.periodeResultatListe = periodeResultatListe;
  }

  public List<PeriodeResultat> getPeriodeResultatListe() {
    return periodeResultatListe;
  }
}
