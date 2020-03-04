package no.nav.bidrag.beregn.forskudd.core.periode.grunnlag;

public class BarnPeriode implements PeriodisertGrunnlag {

  private final Periode datoFraTil;
  private final Integer antallBarn;

  public BarnPeriode(Periode datoFraTil, Integer antallBarn) {
    this.datoFraTil = datoFraTil;
    this.antallBarn = antallBarn;
  }

  public Periode getDatoFraTil() {
    return datoFraTil;
  }
}
