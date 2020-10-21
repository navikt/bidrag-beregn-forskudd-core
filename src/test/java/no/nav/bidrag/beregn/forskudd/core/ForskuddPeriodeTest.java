package no.nav.bidrag.beregn.forskudd.core;

import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_125_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_50_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_75_PROSENT;
import static org.assertj.core.api.Assertions.assertThat;
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

  private final ForskuddPeriode forskuddPeriode = ForskuddPeriode.getInstance();

  private final BeregnForskuddGrunnlag beregnForskuddGrunnlag = TestUtil.byggForskuddGrunnlag();
  private final BeregnForskuddGrunnlag beregnForskuddGrunnlagMedAvvik = TestUtil.byggForskuddGrunnlagMedAvvik();
  private final BeregnForskuddGrunnlag beregnForskuddGrunnlagUtenBarn = TestUtil.byggForskuddGrunnlagUtenBarn();

  @Test
  @DisplayName("Test utvidet grunnlag")
  void testUtvidetGrunnlag() {

    var resultat = forskuddPeriode.beregnPerioder(beregnForskuddGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(9),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_100_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 8"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(250000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerSivilstandKode())
            .isEqualTo(SivilstandKode.GIFT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getAntallBarnIHusstand() == 2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSoknadBarnAlder() == 10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSoknadBarnBostatusKode())
            .isEqualTo(BostatusKode.MED_FORELDRE),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSjablonListe()).isEqualTo(TestUtil.byggSjablonListe()),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_125_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 7"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-05-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 15"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-05-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_125_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 4"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(6).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(6).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(6).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(6).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(7).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(7).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(7).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_50_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(7).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 12"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(8).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(8).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(8).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(8).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11")
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
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.DATO_FRA_ETTER_DATO_TIL),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Opphold mellom perioder i bidragMottakerInntektPeriodeListe: periodeDatoTil=2018-01-01, periodeDatoFra=2018-01-04"),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.PERIODER_HAR_OPPHOLD),

        () -> assertThat(avvikListe.get(2).getAvvikTekst())
            .isEqualTo("Overlappende perioder i bidragMottakerSivilstandPeriodeListe: periodeDatoTil=2018-04-01, periodeDatoFra=2018-03-17"),
        () -> assertThat(avvikListe.get(2).getAvvikType()).isEqualTo(AvvikType.PERIODER_OVERLAPPER),

        () -> assertThat(avvikListe.get(3).getAvvikTekst())
            .isEqualTo("periodeDatoTil kan ikke være null i soknadBarnBostatusPeriodeListe: periodeDatoFra=2018-08-16, periodeDatoTil=null"),
        () -> assertThat(avvikListe.get(3).getAvvikType()).isEqualTo(AvvikType.NULL_VERDI_I_DATO),

        () -> assertThat(avvikListe.get(4).getAvvikTekst()).isEqualTo(
            "periodeDatoTil må være etter periodeDatoFra i bidragMottakerBarnPeriodeListe: periodeDatoFra=2019-03-31, periodeDatoTil=2018-06-17"),
        () -> assertThat(avvikListe.get(4).getAvvikType()).isEqualTo(AvvikType.DATO_FRA_ETTER_DATO_TIL)
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
  @DisplayName("Test grunnlag uten barn")
  void testGrunnlagUtenBarn() {

    var resultat = forskuddPeriode.beregnPerioder(beregnForskuddGrunnlagUtenBarn);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_100_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 8"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(250000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerSivilstandKode())
            .isEqualTo(SivilstandKode.GIFT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getAntallBarnIHusstand() == 2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSoknadBarnAlder() == 10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSoknadBarnBostatusKode())
            .isEqualTo(BostatusKode.MED_FORELDRE),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSjablonListe()).isEqualTo(TestUtil.byggSjablonListe())
    );
  }

  @Test
  @DisplayName("Test beregn perioder - grunnlag med flere inntekter i samme periode - test 1")
  void testBeregnPerioderGrunnlagMedFlereInntekterISammePeriodeTest1() {

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-06-01")), InntektType.KAPITALINNTEKT_EGNE_OPPL,
            BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2017-04-01"), LocalDate.parse("2017-09-01")), InntektType.UTVIDET_BARNETRYGD,
        BigDecimal.valueOf(15000)));
    var grunnlag = TestUtil.byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe);

    var resultat = forskuddPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(4),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(10000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(3),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(10000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(2).getInntektType())
            .isEqualTo(InntektType.UTVIDET_BARNETRYGD),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(2).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(15000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektType())
            .isEqualTo(InntektType.UTVIDET_BARNETRYGD),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(15000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000))
    );
  }

  @Test
  @DisplayName("Test beregn perioder - grunnlag med flere inntekter i samme periode - test 2")
  void testBeregnPerioderGrunnlagMedFlereInntekterISammePeriodeTest2() {

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), InntektType.INNTEKTSOPPL_ARBEIDSGIVER,
            BigDecimal.valueOf(400000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-06-01")), InntektType.KAPITALINNTEKT_EGNE_OPPL,
            BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2017-04-01"), LocalDate.parse("2017-09-01")), InntektType.UTVIDET_BARNETRYGD,
        BigDecimal.valueOf(15000)));
    var grunnlag = TestUtil.byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe);

    var resultat = forskuddPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(4),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(10000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(3),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(10000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(2).getInntektType())
            .isEqualTo(InntektType.UTVIDET_BARNETRYGD),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(2).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(15000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektType())
            .isEqualTo(InntektType.UTVIDET_BARNETRYGD),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(15000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000))
    );
  }

  @Test
  @DisplayName("Test beregn perioder - grunnlag med flere inntekter i samme periode - test 3")
  void testBeregnPerioderGrunnlagMedFlereInntekterISammePeriodeTest3() {

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-03-01"), LocalDate.parse("2017-06-01")), InntektType.KAPITALINNTEKT_EGNE_OPPL,
            BigDecimal.valueOf(10000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-09-01"), LocalDate.parse("2017-12-01")), InntektType.UTVIDET_BARNETRYGD,
            BigDecimal.valueOf(15000)));
    var grunnlag = TestUtil.byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe);

    var resultat = forskuddPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(5),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-03-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-03-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(10000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektType())
            .isEqualTo(InntektType.UTVIDET_BARNETRYGD),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(15000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000))
    );
  }

  @Test
  @DisplayName("Test beregn perioder - grunnlag med flere inntekter i samme periode - test 4")
  void testBeregnPerioderGrunnlagMedFlereInntekterISammePeriodeTest4() {

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), InntektType.KAPITALINNTEKT_EGNE_OPPL,
            BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), InntektType.UTVIDET_BARNETRYGD,
        BigDecimal.valueOf(15000)));
    var grunnlag = TestUtil.byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe);

    var resultat = forskuddPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(3),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(10000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(2).getInntektType())
            .isEqualTo(InntektType.UTVIDET_BARNETRYGD),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(2).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(15000))
    );
  }

  @Test
  @DisplayName("Test beregn perioder - grunnlag med flere inntekter i samme periode - test 5")
  void testBeregnPerioderGrunnlagMedFlereInntekterISammePeriodeTest5() {

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-03-01"), LocalDate.parse("2017-06-01")), InntektType.KAPITALINNTEKT_EGNE_OPPL,
            BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2017-03-01"), LocalDate.parse("2017-06-01")), InntektType.UTVIDET_BARNETRYGD,
        BigDecimal.valueOf(15000)));
    var grunnlag = TestUtil.byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe);

    var resultat = forskuddPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-03-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-03-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(3),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(10000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(2).getInntektType())
            .isEqualTo(InntektType.UTVIDET_BARNETRYGD),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(2).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(15000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000))
    );
  }

  @Test
  @DisplayName("Test beregn perioder - grunnlag med flere inntekter i samme periode - test 6")
  void testBeregnPerioderGrunnlagMedFlereInntekterISammePeriodeTest6() {

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-06-01")), InntektType.KAPITALINNTEKT_EGNE_OPPL,
            BigDecimal.valueOf(10000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2017-04-01"), LocalDate.parse("2017-09-01")), InntektType.UTVIDET_BARNETRYGD,
        BigDecimal.valueOf(15000)));
    var grunnlag = TestUtil.byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe);

    var resultat = forskuddPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(4),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(10000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(3),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(10000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(2).getInntektType())
            .isEqualTo(InntektType.UTVIDET_BARNETRYGD),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBidragMottakerInntektListe().get(2).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(15000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektType())
            .isEqualTo(InntektType.UTVIDET_BARNETRYGD),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getBidragMottakerInntektListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(15000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatKode()).isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonNavnVerdiListe()),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000))
    );
  }

  @Test
  @DisplayName("Test valider input - grunnlag med flere inntekter i samme periode - test 1")
  void testValiderInputGrunnlagMedFlereInntekterISammePeriodeTest1() {

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-06-01")), InntektType.KAPITALINNTEKT_EGNE_OPPL,
            BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2017-04-01"), LocalDate.parse("2017-09-01")), InntektType.UTVIDET_BARNETRYGD,
        BigDecimal.valueOf(15000)));
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
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), InntektType.INNTEKTSOPPL_ARBEIDSGIVER,
            BigDecimal.valueOf(400000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-06-01")), InntektType.KAPITALINNTEKT_EGNE_OPPL,
            BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2017-04-01"), LocalDate.parse("2017-09-01")), InntektType.UTVIDET_BARNETRYGD,
        BigDecimal.valueOf(15000)));
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
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), InntektType.KAPITALINNTEKT_EGNE_OPPL,
            BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), InntektType.UTVIDET_BARNETRYGD,
        BigDecimal.valueOf(15000)));
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
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-03-01"), LocalDate.parse("2017-06-01")), InntektType.KAPITALINNTEKT_EGNE_OPPL,
            BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2017-03-01"), LocalDate.parse("2017-06-01")), InntektType.UTVIDET_BARNETRYGD,
        BigDecimal.valueOf(15000)));
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
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-06-01")), InntektType.KAPITALINNTEKT_EGNE_OPPL,
            BigDecimal.valueOf(10000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER,
        BigDecimal.valueOf(400000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2017-04-01"), LocalDate.parse("2017-09-01")), InntektType.UTVIDET_BARNETRYGD,
        BigDecimal.valueOf(15000)));
    var grunnlag = TestUtil.byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe);

    var avvikListe = forskuddPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isEmpty()
    );
  }


  private void printGrunnlagResultat(BeregnForskuddResultat resultat) {
    resultat.getResultatPeriodeListe().stream().sorted(Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFra()))
        .forEach(sortedPR -> System.out.println(
            "Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFra() + "; " + "Dato til: " + sortedPR.getResultatDatoFraTil().getDatoTil()
                + "; " + "Beløp: " + sortedPR.getResultatBeregning().getResultatBelop().intValue() + "; " + "Resultatkode: " + sortedPR
                .getResultatBeregning().getResultatKode()
                + "; " + "Regel: " + sortedPR.getResultatBeregning().getResultatBeskrivelse()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
