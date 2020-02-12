package no.nav.bidrag.beregn.forskudd.beregning;

import no.nav.bidrag.beregn.forskudd.beregning.grunnlag.ForskuddBeregningGrunnlag;
import no.nav.bidrag.beregn.forskudd.beregning.resultat.ForskuddBeregningResultat;

public interface ForskuddBeregning {

  ForskuddBeregningResultat beregn(ForskuddBeregningGrunnlag grunnlag);

}
