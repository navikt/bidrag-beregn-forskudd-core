package no.nav.bidrag.beregn.forskudd.core;

import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddResultatCore;
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode;

public interface ForskuddCore {

  BeregnForskuddResultatCore beregnForskudd(BeregnForskuddGrunnlagCore grunnlag);

  static ForskuddCore getInstance() {
    return new ForskuddCoreImpl(ForskuddPeriode.getInstance());
  }
}
