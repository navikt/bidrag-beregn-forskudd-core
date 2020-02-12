package no.nav.bidrag.beregn.forskudd.periode.resultat;

import no.nav.bidrag.beregn.forskudd.beregning.resultat.ForskuddBeregningResultat;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.Periode;

public class PeriodeResultat {

  private final Periode datoFraTil;
  private final ForskuddBeregningResultat forskuddBeregningResultat;

  public PeriodeResultat(Periode datoFraTil, ForskuddBeregningResultat forskuddBeregningResultat) {
    this.datoFraTil = datoFraTil;
    this.forskuddBeregningResultat = forskuddBeregningResultat;
  }

  public Periode getDatoFraTil() {
    return datoFraTil;
  }

  public ForskuddBeregningResultat getForskuddBeregningResultat() {
    return forskuddBeregningResultat;
  }
}
