package no.nav.bidrag.beregn.forskudd.core;

import no.nav.bidrag.beregn.forskudd.core.dto.ForskuddPeriodeGrunnlagDto;
import no.nav.bidrag.beregn.forskudd.core.dto.ForskuddPeriodeResultatDto;

public interface ForskuddCore {

  ForskuddPeriodeResultatDto beregnForskudd(ForskuddPeriodeGrunnlagDto grunnlag);

  static ForskuddCore getInstance() {
    return new ForskuddCoreImpl();
  }
}
