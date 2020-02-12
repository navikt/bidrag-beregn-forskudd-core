package no.nav.bidrag.beregn.forskudd.periode;

import no.nav.bidrag.beregn.forskudd.periode.grunnlag.ForskuddPeriodeGrunnlag;
import no.nav.bidrag.beregn.forskudd.periode.resultat.ForskuddPeriodeResultat;

public interface ForskuddPeriode {

  ForskuddPeriodeResultat beregnPerioder(ForskuddPeriodeGrunnlag grunnlag);
}
