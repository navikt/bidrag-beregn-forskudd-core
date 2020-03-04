package no.nav.bidrag.beregn.forskudd.core.beregning;

import no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.ForskuddBeregningGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ForskuddBeregningResultat;

public interface ForskuddBeregning {

  ForskuddBeregningResultat beregn(ForskuddBeregningGrunnlag grunnlag);

}
