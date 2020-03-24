package no.nav.bidrag.beregn.forskudd.core.periode;

import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;

public interface ForskuddPeriode {

  BeregnForskuddResultat beregnPerioder(BeregnForskuddGrunnlag grunnlag);

  static ForskuddPeriode getInstance() {
    return new ForskuddPeriodeImpl();
  }
}
