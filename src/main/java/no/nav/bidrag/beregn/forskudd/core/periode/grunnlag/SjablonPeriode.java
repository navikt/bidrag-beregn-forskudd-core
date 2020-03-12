package no.nav.bidrag.beregn.forskudd.core.periode.grunnlag;

public class SjablonPeriode implements PeriodisertGrunnlag {

  private final String sjablonType;
  private final Periode datoFraTil;
  private final String sjablonVerdi;

  public SjablonPeriode(String sjablonType, Periode datoFraTil, String sjablonVerdi) {
    this.sjablonType = sjablonType;
    this.datoFraTil = datoFraTil;
    this.sjablonVerdi = sjablonVerdi;
  }

  public String getSjablonType() {
    return sjablonType;
  }

  public Periode getDatoFraTil() {
    return datoFraTil;
  }

  public String getSjablonVerdi() { return sjablonVerdi; }
}