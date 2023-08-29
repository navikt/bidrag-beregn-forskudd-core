package no.nav.bidrag.beregn.forskudd.core

import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.enums.AvvikType
import no.nav.bidrag.beregn.felles.enums.BostatusKode
import no.nav.bidrag.beregn.felles.enums.InntektType
import no.nav.bidrag.beregn.felles.enums.SivilstandKode
import no.nav.bidrag.beregn.forskudd.core.TestUtil.byggForskuddGrunnlag
import no.nav.bidrag.beregn.forskudd.core.TestUtil.byggForskuddGrunnlagMedAvvik
import no.nav.bidrag.beregn.forskudd.core.TestUtil.byggForskuddGrunnlagMedFlereInntekterISammePeriode
import no.nav.bidrag.beregn.forskudd.core.TestUtil.byggForskuddGrunnlagUtenAndreBarn
import no.nav.bidrag.beregn.forskudd.core.TestUtil.byggForskuddGrunnlagUtenSivilstand
import no.nav.bidrag.beregn.forskudd.core.TestUtil.byggSjablonPeriodeListe
import no.nav.bidrag.beregn.forskudd.core.TestUtil.byggSjablonPeriodeNavnVerdiListe
import no.nav.bidrag.beregn.forskudd.core.beregning.ForskuddBeregningImpl
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode
import no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriodeImpl
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate
import java.util.function.Consumer

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("ForskuddPeriodeTest")
internal class ForskuddPeriodeTest {
    private val forskuddBeregning = ForskuddBeregningImpl()
    private val forskuddPeriode = ForskuddPeriodeImpl(forskuddBeregning)
    private val beregnForskuddGrunnlag = byggForskuddGrunnlag()
    private val beregnForskuddGrunnlagMedAvvik = byggForskuddGrunnlagMedAvvik()
    private val beregnForskuddGrunnlagUtenBarn = byggForskuddGrunnlagUtenAndreBarn()
    private val beregnForskuddGrunnlagUtenSivilstand = byggForskuddGrunnlagUtenSivilstand()

    @Test
    @DisplayName("Test utvidet grunnlag")
    fun testUtvidetGrunnlag() {
        val resultat = forskuddPeriode.beregnPerioder(beregnForskuddGrunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe).hasSize(9) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2017-01-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2017-12-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].resultat.kode).isEqualTo(ResultatKode.FORHOYET_FORSKUDD_100_PROSENT) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].resultat.regel).isEqualTo("REGEL 6") },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].resultat.sjablonListe).isEqualTo(byggSjablonPeriodeNavnVerdiListe()) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.inntektListe).hasSize(1) },
            Executable {
                assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.inntektListe[0].type)
                    .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER)
            },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.inntektListe[0].belop).isEqualTo(BigDecimal.valueOf(250000)) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.inntektListe[0].referanse).isEqualTo(INNTEKT_REFERANSE_1) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.sivilstand.kode).isEqualTo(SivilstandKode.GIFT) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.sivilstand.referanse).isEqualTo(SIVILSTAND_REFERANSE_GIFT) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.barnIHusstanden.antall).isEqualTo(3.0) },
            Executable {
                assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.barnIHusstanden.referanse)
                    .isEqualTo(BARN_I_HUSSTANDEN_REFERANSE_1)
            },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.soknadBarnAlder.alder).isZero() },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.soknadBarnAlder.referanse).isEqualTo(SOKNADBARN_REFERANSE) },
            Executable {
                assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.soknadBarnBostatus.kode)
                    .isEqualTo(BostatusKode.BOR_MED_FORELDRE)
            },
            Executable {
                assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.soknadBarnBostatus.referanse)
                    .isEqualTo(BOSTATUS_REFERANSE_MED_FORELDRE_1)
            },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.sjablonListe).isEqualTo(byggSjablonPeriodeListe()) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2017-12-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable {
                assertThat(resultat.beregnetForskuddPeriodeListe[1].resultat.kode)
                    .isEqualTo(ResultatKode.FORHOYET_FORSKUDD_11_AAR_125_PROSENT)
            },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[1].resultat.regel).isEqualTo("REGEL 5") },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[2].periode.datoFom).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[2].periode.datoTil).isEqualTo(LocalDate.parse("2018-05-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[2].resultat.kode).isEqualTo(ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[2].resultat.regel).isEqualTo("REGEL 13") },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[3].periode.datoFom).isEqualTo(LocalDate.parse("2018-05-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[3].periode.datoTil).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[3].resultat.kode).isEqualTo(ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[3].resultat.regel).isEqualTo("REGEL 9") },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[4].periode.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[4].periode.datoTil).isEqualTo(LocalDate.parse("2018-09-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[4].resultat.kode).isEqualTo(ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[4].resultat.regel).isEqualTo("REGEL 9") },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[5].periode.datoFom).isEqualTo(LocalDate.parse("2018-09-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[5].periode.datoTil).isEqualTo(LocalDate.parse("2018-12-01")) },
            Executable {
                assertThat(resultat.beregnetForskuddPeriodeListe[5].resultat.kode)
                    .isEqualTo(ResultatKode.FORHOYET_FORSKUDD_11_AAR_125_PROSENT)
            },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[5].resultat.regel).isEqualTo("REGEL 2") },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[6].periode.datoFom).isEqualTo(LocalDate.parse("2018-12-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[6].periode.datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[6].resultat.kode).isEqualTo(ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[6].resultat.regel).isEqualTo("REGEL 9") },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[7].periode.datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[7].periode.datoTil).isEqualTo(LocalDate.parse("2019-04-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[7].resultat.kode).isEqualTo(ResultatKode.REDUSERT_FORSKUDD_50_PROSENT) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[7].resultat.regel).isEqualTo("REGEL 10") },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[8].periode.datoFom).isEqualTo(LocalDate.parse("2019-04-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[8].periode.datoTil).isNull() },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[8].resultat.kode).isEqualTo(ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[8].resultat.regel).isEqualTo("REGEL 9") }
        )
        printGrunnlagResultat(resultat)
    }

    @Test
    @DisplayName("Test utvidet grunnlag med avvik")
    fun testUtvidetGrunnlagMedAvvik() {
        val avvikListe = forskuddPeriode.validerInput(beregnForskuddGrunnlagMedAvvik)
        assertAll(
            Executable { assertThat(avvikListe).isNotEmpty() },
            Executable { assertThat(avvikListe).hasSize(5) },
            Executable { assertThat(avvikListe[0].avvikTekst).isEqualTo("beregnDatoTil må være etter beregnDatoFra") },
            Executable { assertThat(avvikListe[0].avvikType).isEqualTo(AvvikType.DATO_FOM_ETTER_DATO_TIL) },
            Executable {
                assertThat(avvikListe[1].avvikTekst)
                    .isEqualTo("Opphold mellom perioder i bidragMottakerInntektPeriodeListe: datoTil=2018-01-01, datoFom=2018-01-04")
            },
            Executable { assertThat(avvikListe[1].avvikType).isEqualTo(AvvikType.PERIODER_HAR_OPPHOLD) },
            Executable {
                assertThat(avvikListe[2].avvikTekst)
                    .isEqualTo("Overlappende perioder i bidragMottakerSivilstandPeriodeListe: datoTil=2018-04-01, datoFom=2018-03-17")
            },
            Executable { assertThat(avvikListe[2].avvikType).isEqualTo(AvvikType.PERIODER_OVERLAPPER) },
            Executable {
                assertThat(avvikListe[3].avvikTekst).isEqualTo("datoTil kan ikke være null i soknadBarnBostatusPeriodeListe: datoFom=2018-08-16, datoTil=null")
            },
            Executable { assertThat(avvikListe[3].avvikType).isEqualTo(AvvikType.NULL_VERDI_I_DATO) },
            Executable {
                assertThat(avvikListe[4].avvikTekst).isEqualTo("datoTil må være etter datoFom i bidragMottakerBarnPeriodeListe: datoFom=2019-03-31, datoTil=2018-06-17")
            },
            Executable { assertThat(avvikListe[4].avvikType).isEqualTo(AvvikType.DATO_FOM_ETTER_DATO_TIL) }
        )
        printAvvikListe(avvikListe)
    }

    @Test
    @DisplayName("Test utvidet grunnlag med avvik periode mangler data")
    fun testUtvidetGrunnlagMedAvvikPeriodeManglerData() {
        val grunnlag = byggForskuddGrunnlag("2016-01-01", "2020-01-01")
        val avvikListe = forskuddPeriode.validerInput(grunnlag)
        assertAll(
            Executable { assertThat(avvikListe).isNotEmpty() },
            Executable { assertThat(avvikListe).hasSize(3) },
            Executable {
                assertThat(avvikListe[0].avvikTekst).isEqualTo("Første dato i bidragMottakerInntektPeriodeListe (2017-01-01) er etter beregnDatoFra (2016-01-01)")
            },
            Executable { assertThat(avvikListe[0].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) },
            Executable {
                assertThat(avvikListe[1].avvikTekst).isEqualTo("Første dato i bidragMottakerSivilstandPeriodeListe (2017-01-01) er etter beregnDatoFra (2016-01-01)")
            },
            Executable { assertThat(avvikListe[1].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) },
            Executable {
                assertThat(avvikListe[2].avvikTekst).isEqualTo("Siste dato i bidragMottakerSivilstandPeriodeListe (2019-08-01) er før beregnDatoTil (2020-01-01)")
            },
            Executable { assertThat(avvikListe[2].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) }
        )
        printAvvikListe(avvikListe)
    }

    @Test
    @DisplayName("Test grunnlag uten andre barn")
    fun testGrunnlagUtenBarn() {
        val resultat = forskuddPeriode.beregnPerioder(beregnForskuddGrunnlagUtenBarn)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe).hasSize(1) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2017-01-01")) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].periode.datoTil).isNull() },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].resultat.kode).isEqualTo(ResultatKode.FORHOYET_FORSKUDD_100_PROSENT) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].resultat.regel).isEqualTo("REGEL 6") },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].resultat.sjablonListe).isEqualTo(byggSjablonPeriodeNavnVerdiListe()) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.inntektListe).hasSize(1) },
            Executable {
                assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.inntektListe[0].type)
                    .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER)
            },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.inntektListe[0].belop).isEqualTo(BigDecimal.valueOf(250000)) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.sivilstand.kode).isEqualTo(SivilstandKode.GIFT) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.barnIHusstanden.antall).isEqualTo(1.0) },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.soknadBarnAlder.alder).isZero() },
            Executable {
                assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.soknadBarnBostatus.kode)
                    .isEqualTo(BostatusKode.BOR_MED_FORELDRE)
            },
            Executable { assertThat(resultat.beregnetForskuddPeriodeListe[0].grunnlag.sjablonListe).isEqualTo(byggSjablonPeriodeListe()) }
        )
    }

    @Test
    @DisplayName("Test grunnlag uten sivilstandperioder")
    fun testGrunnlagUtenSivilstandperioder() {
        assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            forskuddPeriode.beregnPerioder(beregnForskuddGrunnlagUtenSivilstand)
        }.withMessageContaining("Grunnlagsobjekt SIVILSTAND mangler data for periode")
    }

    @Test
    @DisplayName("Test valider input - grunnlag med flere inntekter i samme periode - test 1")
    fun testValiderInputGrunnlagMedFlereInntekterISammePeriodeTest1() {
        val bmInntektListe = ArrayList<InntektPeriode>()
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), null),
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                BigDecimal.valueOf(400000)
            )
        )
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_2,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-06-01")),
                InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER,
                BigDecimal.valueOf(10000)
            )
        )
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_3,
                Periode(LocalDate.parse("2017-04-01"), LocalDate.parse("2017-09-01")),
                InntektType.UTVIDET_BARNETRYGD,
                BigDecimal.valueOf(15000)
            )
        )
        val grunnlag = byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe)
        val avvikListe = forskuddPeriode.validerInput(grunnlag)
        assertAll(Executable { assertThat(avvikListe).isEmpty() })
    }

    @Test
    @DisplayName("Test valider input - grunnlag med flere inntekter i samme periode - test 2")
    fun testValiderInputGrunnlagMedFlereInntekterISammePeriodeTest2() {
        val bmInntektListe = ArrayList<InntektPeriode>()
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                BigDecimal.valueOf(400000)
            )
        )
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_2,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-06-01")),
                InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER,
                BigDecimal.valueOf(10000)
            )
        )
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_3,
                Periode(LocalDate.parse("2017-04-01"), LocalDate.parse("2017-09-01")),
                InntektType.UTVIDET_BARNETRYGD,
                BigDecimal.valueOf(15000)
            )
        )
        val grunnlag = byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe)
        val avvikListe = forskuddPeriode.validerInput(grunnlag)
        assertAll(Executable { assertThat(avvikListe).isEmpty() })
    }

    @Test
    @DisplayName("Test valider input - grunnlag med flere inntekter i samme periode - test 4")
    fun testValiderInputGrunnlagMedFlereInntekterISammePeriodeTest4() {
        val bmInntektListe = ArrayList<InntektPeriode>()
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), null),
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                BigDecimal.valueOf(400000)
            )
        )
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_2,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
                InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER,
                BigDecimal.valueOf(10000)
            )
        )
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_3,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
                InntektType.UTVIDET_BARNETRYGD,
                BigDecimal.valueOf(15000)
            )
        )
        val grunnlag = byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe)
        val avvikListe = forskuddPeriode.validerInput(grunnlag)
        assertAll(Executable { assertThat(avvikListe).isEmpty() })
    }

    @Test
    @DisplayName("Test valider input - grunnlag med flere inntekter i samme periode - test 5")
    fun testValiderInputGrunnlagMedFlereInntekterISammePeriodeTest5() {
        val bmInntektListe = ArrayList<InntektPeriode>()
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), null),
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                BigDecimal.valueOf(400000)
            )
        )
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_2,
                Periode(LocalDate.parse("2017-03-01"), LocalDate.parse("2017-06-01")),
                InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER,
                BigDecimal.valueOf(10000)
            )
        )
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_3,
                Periode(LocalDate.parse("2017-03-01"), LocalDate.parse("2017-06-01")),
                InntektType.UTVIDET_BARNETRYGD,
                BigDecimal.valueOf(15000)
            )
        )
        val grunnlag = byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe)
        val avvikListe = forskuddPeriode.validerInput(grunnlag)
        assertAll(Executable { assertThat(avvikListe).isEmpty() })
    }

    @Test
    @DisplayName("Test valider input - grunnlag med flere inntekter i samme periode - test 6")
    fun testValiderInputGrunnlagMedFlereInntekterISammePeriodeTest6() {
        val bmInntektListe = ArrayList<InntektPeriode>()
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-06-01")),
                InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER,
                BigDecimal.valueOf(10000)
            )
        )
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_2,
                Periode(LocalDate.parse("2017-01-01"), null),
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                BigDecimal.valueOf(400000)
            )
        )
        bmInntektListe.add(
            InntektPeriode(
                INNTEKT_REFERANSE_3,
                Periode(LocalDate.parse("2017-04-01"), LocalDate.parse("2017-09-01")),
                InntektType.UTVIDET_BARNETRYGD,
                BigDecimal.valueOf(15000)
            )
        )
        val grunnlag = byggForskuddGrunnlagMedFlereInntekterISammePeriode(bmInntektListe)
        val avvikListe = forskuddPeriode.validerInput(grunnlag)
        assertAll(Executable { assertThat(avvikListe).isEmpty() })
    }

    private fun printGrunnlagResultat(resultat: BeregnForskuddResultat) {
        resultat.beregnetForskuddPeriodeListe.stream().sorted(Comparator.comparing { (periode): ResultatPeriode -> periode.datoFom })
            .forEach { (periode, resultat1): ResultatPeriode ->
                println(
                    "Dato fom: " + periode.datoFom + "; " + "Dato til: " + periode.datoTil + "; " + "Beløp: " + resultat1.belop.toInt() + "; " + "Resultatkode: " + resultat1.kode + "; " + "Regel: " + resultat1.regel
                )
            }
    }

    private fun printAvvikListe(avvikListe: List<Avvik>) {
        avvikListe.forEach(Consumer { (avvikTekst, avvikType): Avvik -> println("Avvik tekst: $avvikTekst; Avvik type: $avvikType") })
    }

    companion object {
        private const val INNTEKT_REFERANSE_1 = "INNTEKT_REFERANSE_1"
        private const val INNTEKT_REFERANSE_2 = "INNTEKT_REFERANSE_2"
        private const val INNTEKT_REFERANSE_3 = "INNTEKT_REFERANSE_3"
        private const val SIVILSTAND_REFERANSE_GIFT = "SIVILSTAND_REFERANSE_GIFT"
        private const val BARN_I_HUSSTANDEN_REFERANSE_1 = "BARN_I_HUSSTANDEN_REFERANSE_1"
        private const val SOKNADBARN_REFERANSE = "SOKNADBARN_REFERANSE"
        private const val BOSTATUS_REFERANSE_MED_FORELDRE_1 = "BOSTATUS_REFERANSE_MED_FORELDRE_1"
    }
}
