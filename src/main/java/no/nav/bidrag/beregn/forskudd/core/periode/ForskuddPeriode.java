package no.nav.bidrag.beregn.forskudd.core.periode;

import java.util.List;
import no.nav.bidrag.beregn.forskudd.core.beregning.ForskuddBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.Avvik;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;

public interface ForskuddPeriode {

  BeregnForskuddResultat beregnPerioder(BeregnForskuddGrunnlag grunnlag);
  List<Avvik> validerInput(BeregnForskuddGrunnlag grunnlag);

  static ForskuddPeriode getInstance() {
    return new ForskuddPeriodeImpl(ForskuddBeregning.getInstance());
  }
}
