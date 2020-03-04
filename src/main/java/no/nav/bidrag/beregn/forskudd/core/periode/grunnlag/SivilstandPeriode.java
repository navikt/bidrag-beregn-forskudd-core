package no.nav.bidrag.beregn.forskudd.core.periode.grunnlag;

import no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.SivilstandKode;

public class SivilstandPeriode implements PeriodisertGrunnlag {

  private final Periode datoFraTil;
  private final SivilstandKode sivilstandKode;

  public SivilstandPeriode(Periode datoFraTil, SivilstandKode sivilstandKode) {
    this.datoFraTil = datoFraTil;
    this.sivilstandKode = sivilstandKode;
  }

  public Periode getDatoFraTil() {
    return datoFraTil;
  }

  public SivilstandKode getSivilstandKode() {
    return sivilstandKode;
  }
}