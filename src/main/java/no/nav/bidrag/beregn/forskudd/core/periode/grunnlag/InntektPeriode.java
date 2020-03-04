package no.nav.bidrag.beregn.forskudd.core.periode.grunnlag;

import java.math.BigDecimal;

public class InntektPeriode implements PeriodisertGrunnlag {

  private final Periode datoFraTil;
  private final BigDecimal belop;

  public InntektPeriode(Periode datoFraTil, BigDecimal belop) {
    this.datoFraTil = datoFraTil;
    this.belop = belop;
  }

  public Periode getDatoFraTil() {
    return datoFraTil;
  }

  public BigDecimal getBelop() {
    return belop;
  }
}
