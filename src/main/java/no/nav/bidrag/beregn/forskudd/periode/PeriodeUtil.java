package no.nav.bidrag.beregn.forskudd.periode;

import static java.time.temporal.TemporalAdjusters.firstDayOfNextMonth;

import java.time.LocalDate;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.Periode;

class PeriodeUtil {

  // Juster periode
  static Periode justerPeriode(Periode periode) {
    return new Periode(justerDato(periode.getDatoFra()), justerDato(periode.getDatoTil()));
  }

  // Juster dato til den første i neste måned (hvis ikke dato er den første i inneværende måned)
  private static LocalDate justerDato(LocalDate dato) {
    return (dato == null || dato.getDayOfMonth() == 1) ? dato : dato.with(firstDayOfNextMonth());
  }
}
