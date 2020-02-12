package no.nav.bidrag.beregn.forskudd.periode.grunnlag;

import no.nav.bidrag.beregn.forskudd.beregning.grunnlag.SivilstandKode;

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