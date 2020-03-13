package no.nav.bidrag.beregn.forskudd.core.periode.grunnlag;

public class SjablonPeriode {

  private final Periode datoFraTil;
  private final String sjablonType;
  private final Integer sjablonVerdi;

  public SjablonPeriode(Periode datoFraTil, String sjablonType, Integer sjablonVerdi) {
    this.datoFraTil = datoFraTil;
    this.sjablonType = sjablonType;
    this.sjablonVerdi = sjablonVerdi;
  }

  public Periode getDatoFraTil() {
    return datoFraTil;
  }

  public String getSjablonType() {
    return sjablonType;
  }

  public Integer getSjablonVerdi() { return sjablonVerdi; }
}