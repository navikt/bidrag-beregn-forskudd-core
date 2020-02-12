package no.nav.bidrag.beregn.forskudd;

import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.BostedStatusKode.ALENE;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.BostedStatusKode.ENSLIG_ASYLANT;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.BostedStatusKode.MED_FORELDRE;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.SivilstandKode.ENSLIG;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.SivilstandKode.GIFT;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.Sjablonverdi.FORSKUDDSSATS_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.Sjablonverdi.FORSKUDDSSATS_125_PROSENT;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.Sjablonverdi.FORSKUDDSSATS_200_PROSENT;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.Sjablonverdi.FORSKUDDSSATS_250_PROSENT;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.Sjablonverdi.FORSKUDDSSATS_50_PROSENT;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.Sjablonverdi.FORSKUDDSSATS_75_PROSENT;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.Sjablonverdi.INNTEKTSGRENSE_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.Sjablonverdi.INNTEKTSGRENSE_75_PROSENT_ENSLIG;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.Sjablonverdi.INNTEKTSGRENSE_75_PROSENT_GIFT;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.Sjablonverdi.INNTEKTSINTERVALL_FORSKUDD;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.Sjablonverdi.MULTIPLIKATOR_MAKS_INNTEKTSGRENSE;
import static no.nav.bidrag.beregn.forskudd.beregning.resultat.ResultatKode.AVSLAG;
import static no.nav.bidrag.beregn.forskudd.beregning.resultat.ResultatKode.INNVILGET_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.beregning.resultat.ResultatKode.INNVILGET_125_PROSENT;
import static no.nav.bidrag.beregn.forskudd.beregning.resultat.ResultatKode.INNVILGET_200_PROSENT;
import static no.nav.bidrag.beregn.forskudd.beregning.resultat.ResultatKode.INNVILGET_250_PROSENT;
import static no.nav.bidrag.beregn.forskudd.beregning.resultat.ResultatKode.INNVILGET_50_PROSENT;
import static no.nav.bidrag.beregn.forskudd.beregning.resultat.ResultatKode.INNVILGET_75_PROSENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import no.nav.bidrag.beregn.forskudd.beregning.ForskuddBeregning;
import no.nav.bidrag.beregn.forskudd.beregning.ForskuddBeregningImpl;
import no.nav.bidrag.beregn.forskudd.beregning.grunnlag.BostedStatusKode;
import no.nav.bidrag.beregn.forskudd.beregning.grunnlag.ForskuddBeregningGrunnlag;
import no.nav.bidrag.beregn.forskudd.beregning.grunnlag.SivilstandKode;
import no.nav.bidrag.beregn.forskudd.beregning.resultat.ForskuddBeregningResultat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ForskuddBeregningTest")
class ForskuddBeregningTest {

  private ForskuddBeregningGrunnlag grunnlag = new ForskuddBeregningGrunnlag();

  @Test
  @Order(1)
  @DisplayName("Beregning feiler hvis grunnlag er null")
  void beregningFeilerVedNullGrunnlag() {
    assertThrows(NullPointerException.class, () -> forskuddsberegning().beregn(null));
  }

  @Test
  @Order(2)
  @DisplayName("Regel 1: Søknadsbarn alder er høyere enn eller lik 18 år")
  void skalGiAvslagBarnOver18Aar() {
    lagGrunnlag(BigDecimal.ZERO, ENSLIG, 1, 18, MED_FORELDRE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.ZERO),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(AVSLAG),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 1")
    );
    printGrunnlagResultat(resultat, "   * ");
  }

  @Test
  @Order(3)
  @DisplayName("Regel 2: Søknadsbarn alder er høyere enn eller lik 11 år og type barn er ENSLIG ASYLANT")
  void skalGi250ProsentEnsligAsylant() {
    lagGrunnlag(BigDecimal.ZERO, ENSLIG, 1, 11, ENSLIG_ASYLANT);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_250_PROSENT)),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_250_PROSENT),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 2")
    );
    printGrunnlagResultat(resultat, "   **");
  }

  @Test
  @Order(4)
  @DisplayName("Regel 3: Søknadsbarn alder er lavere enn 11 år og type barn er ENSLIG ASYLANT")
  void skalGi100ProsentEnsligAsylant() {
    lagGrunnlag(BigDecimal.ZERO, ENSLIG, 1, 10, ENSLIG_ASYLANT);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_200_PROSENT)),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_200_PROSENT),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 3")
    );
    printGrunnlagResultat(resultat, "   **");
  }

  @Test
  @Order(5)
  @DisplayName("Regel 4: Søknadsbarn alder er høyere enn eller lik 11 år og bostedsstatus er ikke MED FORELDRE")
  void skalGi125ProsentBorIkkeMedForeldre() {
    lagGrunnlag(BigDecimal.ZERO, ENSLIG, 1, 11, ALENE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_125_PROSENT)),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_125_PROSENT),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 4")
    );
    printGrunnlagResultat(resultat, "   **");
  }

  @Test
  @Order(6)
  @DisplayName("Regel 5: Søknadsbarn alder er lavere enn 11 år og bostedsstatus er ikke MED FORELDRE")
  void skalGi100ProsentBorIkkeMedForeldre() {
    lagGrunnlag(BigDecimal.ZERO, ENSLIG, 1, 10, ALENE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_100_PROSENT)),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_100_PROSENT),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 5")
    );
    printGrunnlagResultat(resultat, "   **");
  }

  @Test
  @Order(7)
  @DisplayName("Regel 6: BM inntekt er over maks grense")
  void skalGiAvslagOverMaksGrense() {
    lagGrunnlag(BigDecimal.valueOf((FORSKUDDSSATS_100_PROSENT * MULTIPLIKATOR_MAKS_INNTEKTSGRENSE) + 1), ENSLIG, 1, 11, MED_FORELDRE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.ZERO),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(AVSLAG),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 6")
    );
    printGrunnlagResultat(resultat, "*    ");
  }

  @Test
  @Order(8)
  @DisplayName("Regel 7: BM inntekt er lavere eller lik sats for fullt forskudd og søknadsbarn alder er høyere enn eller lik 11 år")
  void skalGi125ProsentLavInntekt() {
    lagGrunnlag(BigDecimal.valueOf(INNTEKTSGRENSE_100_PROSENT), ENSLIG, 1, 11, MED_FORELDRE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_125_PROSENT)),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_125_PROSENT),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 7")
    );
    printGrunnlagResultat(resultat, "*  * ");
  }

  @Test
  @Order(9)
  @DisplayName("Regel 8: BM inntekt er lavere eller lik sats for fullt forskudd og søknadsbarn alder er lavere enn 11 år")
  void skalGi100ProsentLavInntekt() {
    lagGrunnlag(BigDecimal.valueOf(INNTEKTSGRENSE_100_PROSENT), ENSLIG, 1, 10, MED_FORELDRE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_100_PROSENT)),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_100_PROSENT),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 8")
    );
    printGrunnlagResultat(resultat, "*  * ");
  }

  @Test
  @Order(10)
  @DisplayName("Regel 9: BM inntekt er lavere eller lik sats for 75% forskudd enslig og antall barn i husstand er 1")
  void skalGi75ProsentEnsligEttBarn() {
    lagGrunnlag(BigDecimal.valueOf(INNTEKTSGRENSE_75_PROSENT_ENSLIG), ENSLIG, 1, 11, MED_FORELDRE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_75_PROSENT)),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 9")
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(11)
  @DisplayName("Regel 10: BM inntekt er høyere enn sats for 75% forskudd enslig og antall barn i husstand er 1")
  void skalGi50ProsentEnsligEttBarn() {
    lagGrunnlag(BigDecimal.valueOf(INNTEKTSGRENSE_75_PROSENT_ENSLIG + 1), ENSLIG, 1, 11, MED_FORELDRE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_50_PROSENT)),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_50_PROSENT),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 10")
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(12)
  @DisplayName("Regel 11: BM inntekt er lavere eller lik sats for 75% forskudd enslig ++ og antall barn i husstand er mer enn 1")
  void skalGi75ProsentEnsligMerEnnEttBarn() {
    lagGrunnlag(BigDecimal.valueOf(INNTEKTSGRENSE_75_PROSENT_ENSLIG + INNTEKTSINTERVALL_FORSKUDD), ENSLIG, 2, 11, MED_FORELDRE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_75_PROSENT)),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 11")
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(13)
  @DisplayName("Regel 12: BM inntekt er høyere enn sats for 75% forskudd enslig ++ og antall barn i husstand er mer enn 1")
  void skalGi50ProsentEnsligMerEnnEttBarn() {
    lagGrunnlag(BigDecimal.valueOf(INNTEKTSGRENSE_75_PROSENT_ENSLIG + INNTEKTSINTERVALL_FORSKUDD + 1), ENSLIG, 2, 11, MED_FORELDRE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_50_PROSENT)),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_50_PROSENT),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 12")
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(14)
  @DisplayName("Regel 13: BM inntekt er lavere eller lik sats for 75% forskudd gift og antall barn i husstand er 1")
  void skalGi75ProsentGiftEttBarn() {
    lagGrunnlag(BigDecimal.valueOf(INNTEKTSGRENSE_75_PROSENT_GIFT), GIFT, 1, 11, MED_FORELDRE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_75_PROSENT)),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 13")
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(15)
  @DisplayName("Regel 14: BM inntekt er høyere enn sats for 75% forskudd gift og antall barn i husstand er 1")
  void skalGi50ProsentGiftEttBarn() {
    lagGrunnlag(BigDecimal.valueOf(INNTEKTSGRENSE_75_PROSENT_GIFT + 1), GIFT, 1, 11, MED_FORELDRE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_50_PROSENT)),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_50_PROSENT),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 14")
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(16)
  @DisplayName("Regel 15: BM inntekt er lavere eller lik sats for 75% forskudd gift ++ og antall barn i husstand er mer enn 1")
  void skalGi75ProsentGiftMerEnnEttBarn() {
    lagGrunnlag(BigDecimal.valueOf(INNTEKTSGRENSE_75_PROSENT_GIFT + INNTEKTSINTERVALL_FORSKUDD), GIFT, 2, 11, MED_FORELDRE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_75_PROSENT)),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 15")
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(17)
  @DisplayName("Regel 16: BM inntekt er høyere enn sats for 75% forskudd gift ++ og antall barn i husstand er mer enn 1")
  void skalGi50ProsentGiftMerEnnEttBarn() {
    lagGrunnlag(BigDecimal.valueOf(INNTEKTSGRENSE_75_PROSENT_GIFT + INNTEKTSINTERVALL_FORSKUDD + 1), GIFT, 2, 11, MED_FORELDRE);
    var resultat = forskuddsberegning().beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatKode()).isEqualTo(INNVILGET_50_PROSENT),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.valueOf(FORSKUDDSSATS_50_PROSENT)),
        () -> assertThat(resultat.getResultatBeskrivelse()).isEqualTo("REGEL 16")
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  private ForskuddBeregning forskuddsberegning() {
    return new ForskuddBeregningImpl();
  }

  private void lagGrunnlag(BigDecimal inntekt, SivilstandKode sivilstandKode, Integer antallBarnHjemme, Integer alderBarn,
      BostedStatusKode bostedStatusKode) {
    grunnlag.setBidragMottakerInntekt(inntekt);
    grunnlag.setBidragMottakerSivilstandKode(sivilstandKode);
    grunnlag.setAntallBarnIHusstand(antallBarnHjemme);
    grunnlag.setSoknadBarnAlder(alderBarn);
    grunnlag.setSoknadBarnBostedStatusKode(bostedStatusKode);
  }

  private void printGrunnlagResultat(ForskuddBeregningResultat resultat, String betydning) {
    System.out.println("SJABLONVERDIER:");
    System.out.println("---------------");
    System.out.println("0005 Forskuddssats 100%:                             " + FORSKUDDSSATS_100_PROSENT);
    System.out.println("0013 Multiplikator:                                  " + MULTIPLIKATOR_MAKS_INNTEKTSGRENSE);
    System.out.println("0033 Inntektsgrense 100%:                            " + INNTEKTSGRENSE_100_PROSENT);
    System.out.println("0034 Inntektsgrense 75% enslig:                      " + INNTEKTSGRENSE_75_PROSENT_ENSLIG);
    System.out.println("0035 Inntektsgrense 75% gift:                        " + INNTEKTSGRENSE_75_PROSENT_GIFT);
    System.out.println("0036 Inntektsintervall:                              " + INNTEKTSINTERVALL_FORSKUDD);
    System.out.println("0005x0013 Maks inntekstgrense:                       " + (FORSKUDDSSATS_100_PROSENT * MULTIPLIKATOR_MAKS_INNTEKTSGRENSE));
    System.out.println();
    System.out.println("GRUNNLAG:");
    System.out.println("---------");
    System.out.println(
        "BM inntekt:                                        " + betydning.substring(0, 1) + " " + grunnlag.getBidragMottakerInntekt().intValue());
    System.out.println(
        "BM sivilstand:                                     " + betydning.substring(1, 2) + " " + grunnlag.getBidragMottakerSivilstandKode().name());
    System.out.println("Antall barn i husstand:                            " + betydning.substring(2, 3) + " " + grunnlag.getAntallBarnIHusstand());
    System.out.println("Alder på søknadsbarn:                              " + betydning.substring(3, 4) + " " + grunnlag.getSoknadBarnAlder());
    System.out.println(
        "Bostedsstatus søknadsbarn:                         " + betydning.substring(4, 5) + " " + grunnlag.getSoknadBarnBostedStatusKode().name());
    System.out.println();
    System.out
        .println("Inntektsintervall totalt (0036 x (antall barn - 1)): " + ((grunnlag.getAntallBarnIHusstand() - 1) * INNTEKTSINTERVALL_FORSKUDD));
    System.out.println();
    System.out.println("RESULTAT:");
    System.out.println("---------");
    System.out.println("Beregnet beløp:                                      " + (resultat == null ? "null" : resultat.getBelop().intValue()));
    System.out.println("Resultatkode:                                        " + (resultat == null ? "null" : resultat.getResultatKode().name()));
    System.out.println("Regel brukt i beregning:                             " + (resultat == null ? "null" : resultat.getResultatBeskrivelse()));
  }
}
