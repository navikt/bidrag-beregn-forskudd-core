package no.nav.bidrag.beregn.forskudd.core.beregning;

import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatBeregning;

public interface ForskuddBeregning {

  ResultatBeregning beregn(GrunnlagBeregning grunnlag);

  static ForskuddBeregning getInstance() {
    return new ForskuddBeregningImpl();
  }
}
