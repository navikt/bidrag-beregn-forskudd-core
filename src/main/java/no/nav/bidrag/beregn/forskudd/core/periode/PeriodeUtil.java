package no.nav.bidrag.beregn.forskudd.core.periode;

import static java.time.temporal.TemporalAdjusters.firstDayOfNextMonth;

import java.time.LocalDate;

class PeriodeUtil {

  // Juster periode
  static Periode justerPeriode(Periode periode) {
    return new Periode(justerDato(periode.getDatoFra()), justerDato(periode.getDatoTil()));
  }

  // Juster dato til den første i neste måned (hvis ikke dato er den første i inneværende måned)
  private static LocalDate justerDato(LocalDate dato) {
    return (dato == null || dato.getDayOfMonth() == 1) ? dato : dato.with(firstDayOfNextMonth());
  }

  // Sjekk om 2 perioder overlapper (datoFra i periode2 er mindre enn datoTil i periode1)
  // periode1 == null indikerer at periode2 er den første perioden. Ingen kontroll nødvendig
  // periode2 == null indikerer at periode1 er den siste perioden. Ingen kontroll nødvendig
  static boolean perioderOverlapper(Periode periode1, Periode periode2) {
    if (periode1 == null || periode2 == null) {
      return false;
    }
    if ((periode1.getDatoTil() == null) || (periode2.getDatoFra() == null)) {
      return false;
    }
    return (periode2.getDatoFra().isBefore(periode1.getDatoTil()));
  }

  // Sjekk om det er opphold (gap) mellom 2 perioder (datoFra i periode2 er større enn datoTil i periode1)
  // periode1 == null indikerer at periode2 er den første perioden. Ingen kontroll nødvendig
  // periode2 == null indikerer at periode1 er den siste perioden. Ingen kontroll nødvendig
  static boolean perioderHarOpphold(Periode periode1, Periode periode2) {
    if (periode1 == null || periode2 == null) {
      return false;
    }
    if ((periode1.getDatoTil() == null) || (periode2.getDatoFra() == null)) {
      return false;
    }
    return (periode2.getDatoFra().isAfter(periode1.getDatoTil()));
  }

  // Sjekk om datoFra >= datoTil
  static boolean datoTilErEtterDatoFra(Periode periode) {
    if (periode.getDatoFra() == null || periode.getDatoTil() == null) {
      return true;
    }
    return (periode.getDatoTil().isAfter(periode.getDatoFra()));
  }
}
