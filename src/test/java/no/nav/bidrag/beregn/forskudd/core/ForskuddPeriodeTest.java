package no.nav.bidrag.beregn.forskudd.core;

import static org.assertj.core.api.Assertions.assertThat;

import no.nav.bidrag.beregn.forskudd.core.dto.ForskuddPeriodeGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ForskuddPeriode (dto test)")
public class ForskuddPeriodeTest {

  private ForskuddPeriode forskuddPeriode = ForskuddPeriode.getInstance();

  @Test
  @DisplayName("skal beregne forrskuddsperiode")
  void skalBeregneForskuddPeriode() {
    var forskuddPeriodeGrunnlag = new ForskuddPeriodeGrunnlag();
    var resultat = forskuddPeriode.beregnPerioder(forskuddPeriodeGrunnlag);

    assertThat(resultat).isNotNull();
  }
}
