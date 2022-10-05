package no.nav.bidrag.beregn.forskudd.core;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.AVSLAG;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.FORHOYET_FORSKUDD_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.FORHOYET_FORSKUDD_11_AAR_125_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.FORSKUDD_ENSLIG_ASYLANT_11_AAR_250_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.FORSKUDD_ENSLIG_ASYLANT_200_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.REDUSERT_FORSKUDD_50_PROSENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SivilstandKode;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.forskudd.core.beregning.ForskuddBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.Alder;
import no.nav.bidrag.beregn.forskudd.core.bo.BarnIHusstanden;
import no.nav.bidrag.beregn.forskudd.core.bo.Bostatus;
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.Inntekt;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.Sivilstand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ForskuddBeregningTest")
class ForskuddBeregningTest {

  private GrunnlagBeregning grunnlag;

  private final ForskuddBeregning forskuddBeregning = ForskuddBeregning.getInstance();
  private final List<SjablonPeriode> sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

  private final BigDecimal forventetResultatBelop50Prosent = BigDecimal.valueOf(850);
  private final BigDecimal forventetResultatBelop75Prosent = BigDecimal.valueOf(1280);
  private final BigDecimal forventetResultatBelop100Prosent = BigDecimal.valueOf(1710);
  private final BigDecimal forventetResultatBelop125Prosent = BigDecimal.valueOf(2140);
  private final BigDecimal forventetResultatBelop200Prosent = BigDecimal.valueOf(3420);
  private final BigDecimal forventetResultatBelop250Prosent = BigDecimal.valueOf(4280);

  private static final String INNTEKT_REFERANSE_1 = "INNTEKT_REFERANSE_1";
  private static final String INNTEKT_REFERANSE_2 = "INNTEKT_REFERANSE_2";
  private static final String SIVILSTAND_REFERANSE = "SIVILSTAND_REFERANSE";
  private static final String BARN_I_HUSSTANDEN_REFERANSE_1 = "BARN_I_HUSSTANDEN_REFERANSE_1";
  private static final String SOKNADBARN_REFERANSE = "SOKNADBARN_REFERANSE";
  private static final String BOSTATUS_REFERANSE = "BOSTATUS_REFERANSE";

  @Test
  @Order(1)
  @DisplayName("Beregning feiler hvis grunnlag er null")
  void beregningFeilerVedNullGrunnlag() {
    assertThrows(NullPointerException.class, () -> forskuddBeregning.beregn(null));
  }

  @Test
  @Order(2)
  @DisplayName("Regel 1: Søknadsbarn alder er høyere enn eller lik 18 år")
  void skalGiAvslagBarnOver18Aar() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.ZERO));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG);
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 18);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_FORELDRE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.ZERO),
        () -> assertThat(resultat.getKode()).isEqualTo(AVSLAG),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 1"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "   * ");
  }

  @Test
  @Order(3)
  @DisplayName("Regel 2: Søknadsbarn alder er høyere enn eller lik 11 år og type barn er ENSLIG ASYLANT")
  void skalGi250ProsentEnsligAsylant() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.ZERO));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG);
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 11);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ENSLIG_ASYLANT);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop250Prosent)).isZero(),
        () -> assertThat(resultat.getKode()).isEqualTo(FORSKUDD_ENSLIG_ASYLANT_11_AAR_250_PROSENT),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 2"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "   **");
  }

  @Test
  @Order(4)
  @DisplayName("Regel 3: Søknadsbarn alder er lavere enn 11 år og type barn er ENSLIG ASYLANT")
  void skalGi100ProsentEnsligAsylant() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.ZERO));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG);
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 10);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ENSLIG_ASYLANT);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop200Prosent)).isZero(),
        () -> assertThat(resultat.getKode()).isEqualTo(FORSKUDD_ENSLIG_ASYLANT_200_PROSENT),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 3"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "   **");
  }

  @Test
  @Order(5)
  @DisplayName("Regel 4: Søknadsbarn alder er høyere enn eller lik 11 år og bostedsstatus er ikke MED FORELDRE")
  void skalGi125ProsentBorIkkeMedForeldre() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.ZERO));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG);
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 11);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop125Prosent)).isZero(),
        () -> assertThat(resultat.getKode()).isEqualTo(FORHOYET_FORSKUDD_11_AAR_125_PROSENT),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 4"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "   **");
  }

  @Test
  @Order(6)
  @DisplayName("Regel 5: Søknadsbarn alder er lavere enn 11 år og bostedsstatus er ikke MED FORELDRE")
  void skalGi100ProsentBorIkkeMedForeldre() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.ZERO));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG);
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 10);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop100Prosent)).isZero(),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 5"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "   **");
  }

  @Test
  @Order(7)
  @DisplayName("Regel 6: BM inntekt er over maks grense")
  void skalGiAvslagOverMaksGrense() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.FORSKUDDSSATS_BELOP).multiply(finnSjablonVerdi(sjablonPeriodeListe,
            SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR)).add(BigDecimal.ONE)));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG);
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 11);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_FORELDRE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop()).isEqualTo(BigDecimal.ZERO),
        () -> assertThat(resultat.getKode()).isEqualTo(AVSLAG),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 6"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "*    ");
  }

  @Test
  @Order(8)
  @DisplayName("Regel 7: BM inntekt er lavere eller lik sats for fullt forskudd og søknadsbarn alder er høyere enn eller lik 11 år")
  void skalGi125ProsentLavInntekt() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP)));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG);
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 11);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_FORELDRE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop125Prosent)).isZero(),
        () -> assertThat(resultat.getKode()).isEqualTo(FORHOYET_FORSKUDD_11_AAR_125_PROSENT),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 7"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "*  * ");
  }

  @Test
  @Order(9)
  @DisplayName("Regel 8: BM inntekt er lavere eller lik sats for fullt forskudd og søknadsbarn alder er lavere enn 11 år")
  void skalGi100ProsentLavInntekt() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP)));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG);
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 10);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_FORELDRE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop100Prosent)).isZero(),
        () -> assertThat(resultat.getKode()).isEqualTo(FORHOYET_FORSKUDD_100_PROSENT),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 8"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "*  * ");
  }

  @Test
  @Order(10)
  @DisplayName("Regel 9: BM inntekt er lavere eller lik sats for 75% forskudd enslig og antall barn i husstand er 1")
  void skalGi75ProsentEnsligEttBarn() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP)));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG);
    // Setter 0 i antall barn i husstanden siden søknadsbarnet regnes med ved status "MED_FORELDRE"
    var barnIHusstanden = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 0d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 11);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_FORELDRE);
    lagGrunnlag(inntektListe, sivilstand, barnIHusstanden, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop75Prosent)).isZero(),
        () -> assertThat(resultat.getKode()).isEqualTo(ORDINAERT_FORSKUDD_75_PROSENT),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 9"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(11)
  @DisplayName("Regel 10: BM inntekt er høyere enn sats for 75% forskudd enslig og antall barn i husstand er 1")
  void skalGi50ProsentEnsligEttBarn() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP).add(BigDecimal.ONE)));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG);
    // Setter 0 i antall barn i husstanden siden søknadsbarnet regnes med ved status "MED_FORELDRE"
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 0d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 11);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_FORELDRE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop50Prosent)).isZero(),
        () -> assertThat(resultat.getKode()).isEqualTo(REDUSERT_FORSKUDD_50_PROSENT),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 10"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(12)
  @DisplayName("Regel 11: BM inntekt er lavere eller lik sats for 75% forskudd enslig ++ og antall barn i husstand er mer enn 1")
  void skalGi75ProsentEnsligMerEnnEttBarn() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP).add(
            finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP))));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG);
    // Setter 1 i antall barn i husstanden siden søknadsbarnet regnes med ved status "MED_FORELDRE"
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 11);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_FORELDRE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop75Prosent)).isZero(),
        () -> assertThat(resultat.getKode()).isEqualTo(ORDINAERT_FORSKUDD_75_PROSENT),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(13)
  @DisplayName("Regel 12: BM inntekt er høyere enn sats for 75% forskudd enslig ++ og antall barn i husstand er mer enn 1")
  void skalGi50ProsentEnsligMerEnnEttBarn() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP).add(
            finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP)).add(BigDecimal.ONE)));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG);
    // Setter 1 i antall barn i husstanden siden søknadsbarnet regnes med ved status "MED_FORELDRE"
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 11);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_FORELDRE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop50Prosent)).isZero(),
        () -> assertThat(resultat.getKode()).isEqualTo(REDUSERT_FORSKUDD_50_PROSENT),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 12"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(14)
  @DisplayName("Regel 13: BM inntekt er lavere eller lik sats for 75% forskudd gift og antall barn i husstand er 1")
  void skalGi75ProsentGiftEttBarn() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP)));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.GIFT);
    // Setter 0 i antall barn i husstanden siden søknadsbarnet regnes med ved status "MED_FORELDRE"
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 0d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 11);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_FORELDRE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop75Prosent)).isZero(),
        () -> assertThat(resultat.getKode()).isEqualTo(ORDINAERT_FORSKUDD_75_PROSENT),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 13"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(15)
  @DisplayName("Regel 14: BM inntekt er høyere enn sats for 75% forskudd gift og antall barn i husstand er 1")
  void skalGi50ProsentGiftEttBarn() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP).add(BigDecimal.ONE)));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.GIFT);
    // Setter 0 i antall barn i husstanden siden søknadsbarnet regnes med ved status "MED_FORELDRE"
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 0d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 11);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_FORELDRE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop50Prosent)).isZero(),
        () -> assertThat(resultat.getKode()).isEqualTo(REDUSERT_FORSKUDD_50_PROSENT),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 14"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(16)
  @DisplayName("Regel 15: BM inntekt er lavere eller lik sats for 75% forskudd gift ++ og antall barn i husstand er mer enn 1")
  void skalGi75ProsentGiftMerEnnEttBarn() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP).add(
            finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP))));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.GIFT);
    // Setter 1 i antall barn i husstanden siden søknadsbarnet regnes med ved status "MED_FORELDRE"
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 11);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_FORELDRE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop75Prosent)).isZero(),
        () -> assertThat(resultat.getKode()).isEqualTo(ORDINAERT_FORSKUDD_75_PROSENT),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 15"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(17)
  @DisplayName("Regel 16: BM inntekt er høyere enn sats for 75% forskudd gift ++ og antall barn i husstand er mer enn 1 (1 inntekt)")
  void skalGi50ProsentGiftMerEnnEttBarn_EnInntekt() {
    var inntektListe = singletonList(new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP).add(
            finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP).add(BigDecimal.ONE))));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.GIFT);
    // Setter 1 i antall barn i husstanden siden søknadsbarnet regnes med ved status "MED_FORELDRE"
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 11);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_FORELDRE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getKode()).isEqualTo(REDUSERT_FORSKUDD_50_PROSENT),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop50Prosent)).isZero(),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 16"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  @Test
  @Order(18)
  @DisplayName("Regel 16: BM inntekt er høyere enn sats for 75% forskudd gift ++ og antall barn i husstand er mer enn 1 (2 inntekter)")
  void skalGi50ProsentGiftMerEnnEttBarn_ToInntekter() {
    var inntektListe = Arrays.asList(
        new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
            finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP)),
        new Inntekt(INNTEKT_REFERANSE_2, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
            finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP).add(BigDecimal.ONE)));
    var sivilstand = new Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.GIFT);
    // Setter 1 i antall barn i husstanden siden søknadsbarnet regnes med ved status "MED_FORELDRE"
    var barn = new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1d);
    var alder = new Alder(SOKNADBARN_REFERANSE, 11);
    var bostatus = new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_FORELDRE);
    lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus);

    var resultat = forskuddBeregning.beregn(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getKode()).isEqualTo(REDUSERT_FORSKUDD_50_PROSENT),
        () -> assertThat(resultat.getBelop().compareTo(forventetResultatBelop50Prosent)).isZero(),
        () -> assertThat(resultat.getRegel()).isEqualTo("REGEL 16"),
        () -> assertThat(resultat.getSjablonListe()).isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe())
    );
    printGrunnlagResultat(resultat, "***  ");
  }

  private void lagGrunnlag(List<Inntekt> inntekt, Sivilstand sivilstand, BarnIHusstanden barnIHusstanden, Alder alder, Bostatus bostatus) {
    grunnlag = new GrunnlagBeregning(inntekt, sivilstand, barnIHusstanden, alder, bostatus, sjablonPeriodeListe);
  }

  private BigDecimal finnSjablonVerdi(List<SjablonPeriode> sjablonPeriodeListe, SjablonTallNavn sjablonTallNavn) {
    var sjablonListe = sjablonPeriodeListe.stream()
        .map(SjablonPeriode::getSjablon)
        .collect(toList());
    return SjablonUtil.hentSjablonverdi(sjablonListe, sjablonTallNavn);
  }

  private void printGrunnlagResultat(ResultatBeregning resultat, String betydning) {
    System.out.println();
    System.out.println();
    System.out.println("SJABLONVERDIER:");
    System.out.println("---------------");
    System.out.println("0005 Forskuddssats 100%:                             " + finnSjablonVerdi(sjablonPeriodeListe,
        SjablonTallNavn.FORSKUDDSSATS_BELOP));
    System.out.println("0013 Multiplikator:                                  " + finnSjablonVerdi(sjablonPeriodeListe,
        SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR));
    System.out.println("0033 Inntektsgrense 100%:                            " + finnSjablonVerdi(sjablonPeriodeListe,
        SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP));
    System.out.println("0034 Inntektsgrense 75% enslig:                      " + finnSjablonVerdi(sjablonPeriodeListe,
        SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP));
    System.out.println("0035 Inntektsgrense 75% gift:                        " + finnSjablonVerdi(sjablonPeriodeListe,
        SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP));
    System.out.println("0038 Forskuddssats 75%:                              " + finnSjablonVerdi(sjablonPeriodeListe,
        SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP));
    System.out.println("0036 Inntektsintervall:                              " + finnSjablonVerdi(sjablonPeriodeListe,
        SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP));
    System.out.println("0005x0013 Maks inntektsgrense:                       " + (finnSjablonVerdi(sjablonPeriodeListe,
        SjablonTallNavn.FORSKUDDSSATS_BELOP).multiply(
            finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR))));
    System.out.println();
    System.out.println("GRUNNLAG:");
    System.out.println("---------");
    System.out.println(
        "BM inntekt:                                        " + betydning.charAt(0) + " " + grunnlag.getInntektListe());
    System.out.println("BM sivilstand:                                     " + betydning.charAt(1) + " " +
        grunnlag.getSivilstand().getKode().name());
    System.out.println("Antall barn i husstand:                            " + betydning.charAt(2) + " " + grunnlag.getBarnIHusstanden());
    System.out.println("Alder på søknadsbarn:                              " + betydning.charAt(3) + " " + grunnlag.getSoknadBarnAlder());
    System.out.println(
        "Bostedsstatus søknadsbarn:                         " + betydning.charAt(4) + " " + grunnlag.getSoknadBarnBostatus().getKode().name());
    System.out.println();
    System.out.println("Inntektsintervall totalt (0036 x (antall barn - 1)): " + (finnSjablonVerdi(sjablonPeriodeListe,
        SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP).multiply(BigDecimal.valueOf(grunnlag.getBarnIHusstanden().getAntall() - 1))));
    System.out.println();
    System.out.println("RESULTAT:");
    System.out.println("---------");
    System.out.println("Beregnet beløp:                                      " + (resultat == null ? "null" : resultat.getBelop().intValue()));
    System.out.println("Resultatkode:                                        " + (resultat == null ? "null" : resultat.getKode().name()));
    System.out.println("Regel brukt i beregning:                             " + (resultat == null ? "null" : resultat.getRegel()));
  }
}
