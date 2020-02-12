package no.nav.bidrag.beregn.forskudd.periode.grunnlag;

import no.nav.bidrag.beregn.forskudd.beregning.grunnlag.BostedStatusKode;

public class BostatusPeriode implements PeriodisertGrunnlag {

  private final Periode datoFraTil;
  private final BostedStatusKode bostedStatusKode;

  public BostatusPeriode(Periode datoFraTil, BostedStatusKode bostedStatusKode) {
    this.datoFraTil = datoFraTil;
    this.bostedStatusKode = bostedStatusKode;
  }

  public Periode getDatoFraTil() {
    return datoFraTil;
  }

  public BostedStatusKode getBostedStatusKode() {
    return bostedStatusKode;
  }
}
