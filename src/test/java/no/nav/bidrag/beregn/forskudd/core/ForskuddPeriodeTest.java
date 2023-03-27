package no.nav.bidrag.beregn.forskudd.core;

import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.FORHOYET_FORSKUDD_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.FORHOYET_FORSKUDD_11_AAR_125_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.REDUSERT_FORSKUDD_50_PROSENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SivilstandKode;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ForskuddPeriodeTest")
class ForskuddPeriodeTest {

  private static final String INNTEKT_REFERANSE_1 = "INNTEKT_REFERANSE_1";
  private static final String INNTEKT_REFERANSE_2 = "INNTEKT_REFERANSE_2";
  private static final String INNTEKT_REFERANSE_3 = "INNTEKT_REFERANSE_3";
  private static final String SIVILSTAND_REFERANSE_GIFT = "SIVILSTAND_REFERANSE_GIFT";
  private static final String BARN_I_HUSSTANDEN_REFERANSE_1 = "BARN_I_HUSSTANDEN_REFERANSE_1";
  private static final String SOKNADBARN_REFERANSE = "SOKNADBARN_REFERANSE";
  private static final String BOSTATUS_REFERANSE_MED_FORELDRE_1 = "BOSTATUS_REFERANSE_MED_FORELDRE_1";

  private final ForskuddPeriode forskuddPeriode = ForskuddPeriode.getInstance();

  private final BeregnForskuddGrunnlag beregnForskuddGrunnlag = TestUtil.byggForskuddGrunnlag();
  private final BeregnForskuddGrunnlag beregnForskuddGrunnlagMedAvvik = TestUtil.byggForskuddGrunnlagMedAvvik();
  private final BeregnForskuddGrunnlag beregnForskuddGrunnlagUtenBarn = TestUtil.byggForskuddGrunnlagUtenAndreBarn();
  private final BeregnForskuddGrunnlag beregnForskuddGrunnlagUtenSivilstand = TestUtil.byggForskuddGrunnlagUtenSivilstand();

  @Test
  @DisplayName("Test utvidet grunnlag")
  void testUtvidetGrunnlag() {

    var resultat = forskuddPeriode.beregnPerioder(beregnForskuddGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe()).hasSize(9),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2017-12-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getResultat().getKode()).isEqualTo(FORHOYET_FORSKUDD_100_PROSENT),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getResultat().getRegel()).isEqualTo("REGEL 6"),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getResultat().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe()),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getInntektListe()).hasSize(1),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getInntektListe().get(0).getType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getInntektListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(250000)),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getInntektListe().get(0).getReferanse())
            .isEqualTo(INNTEKT_REFERANSE_1),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getSivilstand().getKode())
            .isEqualTo(SivilstandKode.GIFT),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getSivilstand().getReferanse())
            .isEqualTo(SIVILSTAND_REFERANSE_GIFT),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getBarnIHusstanden().getAntall()).isEqualTo(3),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getBarnIHusstanden().getReferanse())
            .isEqualTo(BARN_I_HUSSTANDEN_REFERANSE_1),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getSoknadBarnAlder().getAlder()).isZero(),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getSoknadBarnAlder().getReferanse())
            .isEqualTo(SOKNADBARN_REFERANSE),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getSoknadBarnBostatus().getKode())
            .isEqualTo(BostatusKode.BOR_MED_FORELDRE),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getSoknadBarnBostatus().getReferanse())
            .isEqualTo(BOSTATUS_REFERANSE_MED_FORELDRE_1),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonPeriodeListe()),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(1).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2017-12-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(1).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(1).getResultat().getKode()).isEqualTo(
            FORHOYET_FORSKUDD_11_AAR_125_PROSENT),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(1).getResultat().getRegel()).isEqualTo("REGEL 5"),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(2).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(2).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2018-05-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(2).getResultat().getKode()).isEqualTo(ORDINAERT_FORSKUDD_75_PROSENT),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(2).getResultat().getRegel()).isEqualTo("REGEL 13"),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(3).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2018-05-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(3).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(3).getResultat().getKode()).isEqualTo(ORDINAERT_FORSKUDD_75_PROSENT),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(3).getResultat().getRegel()).isEqualTo("REGEL 9"),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(4).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(4).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2018-09-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(4).getResultat().getKode()).isEqualTo(ORDINAERT_FORSKUDD_75_PROSENT),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(4).getResultat().getRegel()).isEqualTo("REGEL 9"),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(5).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2018-09-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(5).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2018-12-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(5).getResultat().getKode()).isEqualTo(
            FORHOYET_FORSKUDD_11_AAR_125_PROSENT),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(5).getResultat().getRegel()).isEqualTo("REGEL 2"),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(6).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2018-12-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(6).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(6).getResultat().getKode()).isEqualTo(ORDINAERT_FORSKUDD_75_PROSENT),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(6).getResultat().getRegel()).isEqualTo("REGEL 9"),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(7).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(7).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(7).getResultat().getKode()).isEqualTo(REDUSERT_FORSKUDD_50_PROSENT),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(7).getResultat().getRegel()).isEqualTo("REGEL 10"),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(8).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(8).getPeriode().getDatoTil()).isNull(),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(8).getResultat().getKode()).isEqualTo(ORDINAERT_FORSKUDD_75_PROSENT),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(8).getResultat().getRegel()).isEqualTo("REGEL 9")
    );
    printGrunnlagResultat(resultat);
  }


  @Test
  @DisplayName("Test utvidet grunnlag med avvik")
  void testUtvidetGrunnlagMedAvvik() {

    var avvikListe = forskuddPeriode.validerInput(beregnForskuddGrunnlagMedAvvik);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(5),

        () -> assertThat(avvikListe.get(0).getAvvikTekst()).isEqualTo("beregnDatoTil må være etter beregnDatoFra"),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.DATO_FOM_ETTER_DATO_TIL),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Opphold mellom perioder i bidragMottakerInntektPeriodeListe: datoTil=2018-01-01, datoFom=2018-01-04"),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.PERIODER_HAR_OPPHOLD),

        () -> assertThat(avvikListe.get(2).getAvvikTekst())
            .isEqualTo("Overlappende perioder i bidragMottakerSivilstandPeriodeListe: datoTil=2018-04-01, datoFom=2018-03-17"),
        () -> assertThat(avvikListe.get(2).getAvvikType()).isEqualTo(AvvikType.PERIODER_OVERLAPPER),

        () -> assertThat(avvikListe.get(3).getAvvikTekst())
            .isEqualTo("datoTil kan ikke være null i soknadBarnBostatusPeriodeListe: datoFom=2018-08-16, datoTil=null"),
        () -> assertThat(avvikListe.get(3).getAvvikType()).isEqualTo(AvvikType.NULL_VERDI_I_DATO),

        () -> assertThat(avvikListe.get(4).getAvvikTekst())
            .isEqualTo("datoTil må være etter datoFom i bidragMottakerBarnPeriodeListe: datoFom=2019-03-31, datoTil=2018-06-17"),
        () -> assertThat(avvikListe.get(4).getAvvikType()).isEqualTo(AvvikType.DATO_FOM_ETTER_DATO_TIL)
    );
    printAvvikListe(avvikListe);
  }

  @Test
  @DisplayName("Test utvidet grunnlag med avvik periode mangler data")
  void testUtvidetGrunnlagMedAvvikPeriodeManglerData() {

    var grunnlag = TestUtil.byggForskuddGrunnlag("2016-01-01", "2020-01-01");
    var avvikListe = forskuddPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(3),

        () -> assertThat(avvikListe.get(0).getAvvikTekst())
            .isEqualTo("Første dato i bidragMottakerInntektPeriodeListe (2017-01-01) er etter beregnDatoFra (2016-01-01)"),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Første dato i bidragMottakerSivilstandPeriodeListe (2017-01-01) er etter beregnDatoFra (2016-01-01)"),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(2).getAvvikTekst())
            .isEqualTo("Siste dato i bidragMottakerSivilstandPeriodeListe (2019-08-01) er før beregnDatoTil (2020-01-01)"),
        () -> assertThat(avvikListe.get(2).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA)
    );
    printAvvikListe(avvikListe);
  }

  @Test
  @DisplayName("Test grunnlag uten andre barn")
  void testGrunnlagUtenBarn() {

    var resultat = forskuddPeriode.beregnPerioder(beregnForskuddGrunnlagUtenBarn);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe()).hasSize(1),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getPeriode().getDatoTil()).isNull(),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getResultat().getKode()).isEqualTo(FORHOYET_FORSKUDD_100_PROSENT),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getResultat().getRegel()).isEqualTo("REGEL 6"),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getResultat().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonPeriodeNavnVerdiListe()),

        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getInntektListe()).hasSize(1),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getInntektListe().get(0).getType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getInntektListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(250000)),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getSivilstand().getKode())
            .isEqualTo(SivilstandKode.GIFT),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getBarnIHusstanden().getAntall()).isEqualTo(1d),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getSoknadBarnAlder().getAlder()).isZero(),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getSoknadBarnBostatus().getKode())
            .isEqualTo(BostatusKode.BOR_MED_FORELDRE),
        () -> assertThat(resultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlag().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonPeriodeListe())
    );
  }

  @Test
  @DisplayName("Test grunnlag uten sivilstandperioder")
  void testGrunnlagUtenSivilstandperioder() {

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> forskuddPeriode.beregnPerioder(beregnForskuddGrunnlagUtenSivilstand))
        .withMessageContaining("Grunnlagsobjekt SIVILSTAND mangler data for periode");
  }


  @Test
  @DisplayName("Test valider input - grunnlag med flere inntekter i samme periode - test 1")
  void testValiderInputGrunnlagMedFlereInntekterISammePeriodeTest1() {

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), null),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_2, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-06-01")),
        InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER, BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_3, new Periode(LocalDate.parse("2017-04-01"), LocalDate.parse("2017-09-01")),
        InntektType.UTVIDET_BARNETRYGD, BigDecimal.valueOf(15000)));
    var grunnlag = TestUtil.byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe);

    var avvikListe = forskuddPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isEmpty()
    );
  }

  @Test
  @DisplayName("Test valider input - grunnlag med flere inntekter i samme periode - test 2")
  void testValiderInputGrunnlagMedFlereInntekterISammePeriodeTest2() {

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_2, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-06-01")),
        InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER, BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_3, new Periode(LocalDate.parse("2017-04-01"), LocalDate.parse("2017-09-01")),
        InntektType.UTVIDET_BARNETRYGD, BigDecimal.valueOf(15000)));
    var grunnlag = TestUtil.byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe);

    var avvikListe = forskuddPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isEmpty()
    );
  }

  @Test
  @DisplayName("Test valider input - grunnlag med flere inntekter i samme periode - test 4")
  void testValiderInputGrunnlagMedFlereInntekterISammePeriodeTest4() {

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), null),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_2, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER, BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_3, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        InntektType.UTVIDET_BARNETRYGD, BigDecimal.valueOf(15000)));
    var grunnlag = TestUtil.byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe);

    var avvikListe = forskuddPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isEmpty()
    );
  }

  @Test
  @DisplayName("Test valider input - grunnlag med flere inntekter i samme periode - test 5")
  void testValiderInputGrunnlagMedFlereInntekterISammePeriodeTest5() {

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), null),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_2, new Periode(LocalDate.parse("2017-03-01"), LocalDate.parse("2017-06-01")),
        InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER, BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_3, new Periode(LocalDate.parse("2017-03-01"), LocalDate.parse("2017-06-01")),
        InntektType.UTVIDET_BARNETRYGD, BigDecimal.valueOf(15000)));
    var grunnlag = TestUtil.byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe);

    var avvikListe = forskuddPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isEmpty()
    );
  }

  @Test
  @DisplayName("Test valider input - grunnlag med flere inntekter i samme periode - test 6")
  void testValiderInputGrunnlagMedFlereInntekterISammePeriodeTest6() {

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-06-01")),
        InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER, BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_2, new Periode(LocalDate.parse("2017-01-01"), null),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_3, new Periode(LocalDate.parse("2017-04-01"), LocalDate.parse("2017-09-01")),
        InntektType.UTVIDET_BARNETRYGD, BigDecimal.valueOf(15000)));
    var grunnlag = TestUtil.byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe);

    var avvikListe = forskuddPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isEmpty()
    );
  }


  private void printGrunnlagResultat(BeregnForskuddResultat resultat) {
    resultat.getBeregnetForskuddPeriodeListe().stream().sorted(Comparator.comparing(pR -> pR.getPeriode().getDatoFom()))
        .forEach(sortedPR -> System.out.println(
            "Dato fom: " + sortedPR.getPeriode().getDatoFom() + "; " + "Dato til: " + sortedPR.getPeriode().getDatoTil()
                + "; " + "Beløp: " + sortedPR.getResultat().getBelop().intValue() + "; " + "Resultatkode: " + sortedPR
                .getResultat().getKode()
                + "; " + "Regel: " + sortedPR.getResultat().getRegel()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
