package no.nav.bidrag.beregn.forskudd.core.periode.grunnlag;

public class SjablonPeriodeVerdi implements PeriodisertGrunnlag {

  private final Periode datoFraTil;
  private final Integer sjablonVerdi;

  public SjablonPeriodeVerdi(Periode datoFraTil, Integer sjablonVerdi) {
    this.datoFraTil = datoFraTil;
    this.sjablonVerdi = sjablonVerdi;
  }

  public Periode getDatoFraTil() {
    return datoFraTil;
  }

  public Integer getSjablonVerdi() { return sjablonVerdi; }
}