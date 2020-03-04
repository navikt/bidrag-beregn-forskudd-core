package no.nav.bidrag.beregn.forskudd.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ForskuddBeregningResultat;
import no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ResultatKode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ForskuddBeregningResultatTest")
class ForskuddBeregningResultatTest {

  @Test
  void testResultatKanMerges() {
    Boolean resultatKanMerges = new ForskuddBeregningResultat(BigDecimal.valueOf(100), ResultatKode.INNVILGET_75_PROSENT, "REGEL 1")
        .kanMergesMed(new ForskuddBeregningResultat(BigDecimal.valueOf(100), ResultatKode.INNVILGET_75_PROSENT, "REGEL 1"));
    assertThat(resultatKanMerges).isTrue();
  }

  @Test
  void testResultatKanIkkeMergesForskjellIBeloep() {
    Boolean resultatKanMerges = new ForskuddBeregningResultat(BigDecimal.valueOf(100), ResultatKode.INNVILGET_75_PROSENT, "REGEL 1")
        .kanMergesMed(new ForskuddBeregningResultat(BigDecimal.valueOf(200), ResultatKode.INNVILGET_75_PROSENT, "REGEL 1"));
    assertThat(resultatKanMerges).isFalse();
  }

  @Test
  void testResultatKanIkkeMergesForskjellIResultatkode() {
    Boolean resultatKanMerges = new ForskuddBeregningResultat(BigDecimal.valueOf(100), ResultatKode.INNVILGET_75_PROSENT, "REGEL 1")
        .kanMergesMed(new ForskuddBeregningResultat(BigDecimal.valueOf(100), ResultatKode.INNVILGET_50_PROSENT, "REGEL 1"));
    assertThat(resultatKanMerges).isFalse();
  }

  @Test
  void testResultatKanIkkeMergesForskjellIResultatbeskrivelse() {
    Boolean resultatKanMerges = new ForskuddBeregningResultat(BigDecimal.valueOf(100), ResultatKode.INNVILGET_75_PROSENT, "REGEL 1")
        .kanMergesMed(new ForskuddBeregningResultat(BigDecimal.valueOf(100), ResultatKode.INNVILGET_75_PROSENT, "REGEL 2"));
    assertThat(resultatKanMerges).isFalse();
  }
}