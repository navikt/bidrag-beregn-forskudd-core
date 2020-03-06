package no.nav.bidrag.beregn.forskudd.core.periode;

import no.nav.bidrag.beregn.forskudd.core.dto.ForskuddPeriodeGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.dto.ForskuddPeriodeResultat;

public interface ForskuddPeriode {

  ForskuddPeriodeResultat beregnPerioder(ForskuddPeriodeGrunnlag grunnlag);

  static ForskuddPeriode getInstance() {
    return new ForskuddPeriodeImpl();
  }
}
