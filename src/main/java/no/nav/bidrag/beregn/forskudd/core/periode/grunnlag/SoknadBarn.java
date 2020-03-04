package no.nav.bidrag.beregn.forskudd.core.periode.grunnlag;

import java.time.LocalDate;
import java.util.List;

public class SoknadBarn {

  private LocalDate fodselDato;
  private List<BostatusPeriode> soknadBarnBostatusPeriodeListe;

  public LocalDate getFodselDato() {
    return fodselDato;
  }

  public void setFodselDato(LocalDate fodselDato) {
    this.fodselDato = fodselDato;
  }

  public List<BostatusPeriode> getSoknadBarnBostatusPeriodeListe() {
    return soknadBarnBostatusPeriodeListe;
  }

  public void setSoknadBarnBostatusPeriodeListe(
      List<BostatusPeriode> soknadBarnBostatusPeriodeListe) {
    this.soknadBarnBostatusPeriodeListe = soknadBarnBostatusPeriodeListe;
  }
}
