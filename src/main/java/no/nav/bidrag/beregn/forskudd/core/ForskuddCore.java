package no.nav.bidrag.beregn.forskudd.core;

import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnetForskuddResultatCore;
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode;

public interface ForskuddCore {

  BeregnetForskuddResultatCore beregnForskudd(BeregnForskuddGrunnlagCore grunnlag);

  static ForskuddCore getInstance() {
    return new ForskuddCoreImpl(ForskuddPeriode.getInstance());
  }
}
