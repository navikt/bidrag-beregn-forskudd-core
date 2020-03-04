package no.nav.bidrag.beregn.forskudd.core.periode.grunnlag;

public class AlderPeriode implements PeriodisertGrunnlag {

  private final Periode datoFraTil;
  private final Integer alder;

  public AlderPeriode(Periode datoFraTil, Integer alder) {
    this.datoFraTil = datoFraTil;
    this.alder = alder;
  }

  public Periode getDatoFraTil() {
    return datoFraTil;
  }

  public Integer getAlder() {
    return alder;
  }
}
