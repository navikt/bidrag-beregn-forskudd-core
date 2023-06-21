package no.nav.bidrag.beregn.forskudd.core

import no.nav.bidrag.beregn.felles.SjablonUtil
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.enums.BostatusKode
import no.nav.bidrag.beregn.felles.enums.InntektType
import no.nav.bidrag.beregn.felles.enums.SivilstandKode
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn
import no.nav.bidrag.beregn.forskudd.core.TestUtil.byggSjablonPeriodeListe
import no.nav.bidrag.beregn.forskudd.core.TestUtil.byggSjablonPeriodeNavnVerdiListe
import no.nav.bidrag.beregn.forskudd.core.beregning.ForskuddBeregning
import no.nav.bidrag.beregn.forskudd.core.bo.Alder
import no.nav.bidrag.beregn.forskudd.core.bo.BarnIHusstanden
import no.nav.bidrag.beregn.forskudd.core.bo.Bostatus
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.forskudd.core.bo.Inntekt
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatBeregning
import no.nav.bidrag.beregn.forskudd.core.bo.Sivilstand
import no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("ForskuddBeregningTest")
internal class ForskuddBeregningTest {
    private var grunnlag: GrunnlagBeregning? = null
    private val forskuddBeregning = ForskuddBeregning()
    private val sjablonPeriodeListe = byggSjablonPeriodeListe()
    private val sjablonPeriodeNavnVerdiListe = byggSjablonPeriodeNavnVerdiListe()
    private val forventetResultatBelop50Prosent = BigDecimal.valueOf(850)
    private val forventetResultatBelop75Prosent = BigDecimal.valueOf(1280)
    private val forventetResultatBelop100Prosent = BigDecimal.valueOf(1710)
    private val forventetResultatBelop125Prosent = BigDecimal.valueOf(2130)

    @Test
    @Order(2)
    @DisplayName("Regel 1: Søknadsbarn alder er høyere enn eller lik 18 år")
    fun skalGiAvslagBarnOver18Aar() {
        val inntektListe = listOf(Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.ZERO))
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG)
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 18)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.belop).isZero() },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.AVSLAG) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 1") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "   * ")
    }

    @Test
    @Order(3)
    @DisplayName("Regel 2: Søknadsbarn alder er høyere enn eller lik 11 år og bostedsstatus er ikke MED FORELDRE")
    fun skalGi125ProsentBorIkkeMedForeldre() {
        val inntektListe = listOf(Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.ZERO))
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG)
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 11)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_IKKE_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.belop).isEqualByComparingTo(forventetResultatBelop125Prosent) },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.FORHOYET_FORSKUDD_11_AAR_125_PROSENT) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 2") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "   **")
    }

    @Test
    @Order(4)
    @DisplayName("Regel 3: Søknadsbarn alder er lavere enn 11 år og bostedsstatus er ikke MED FORELDRE")
    fun skalGi100ProsentBorIkkeMedForeldre() {
        val inntektListe = listOf(Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.ZERO))
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG)
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 10)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_IKKE_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.belop).isEqualByComparingTo(forventetResultatBelop100Prosent) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 3") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "   **")
    }

    @Test
    @Order(5)
    @DisplayName("Regel 4: BM inntekt er over maksgrense")
    fun skalGiAvslagOverMaksGrense() {
        val inntektListe = listOf(
            Inntekt(
                INNTEKT_REFERANSE_1,
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.FORSKUDDSSATS_BELOP).multiply(
                    finnSjablonVerdi(
                        sjablonPeriodeListe,
                        SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR
                    )
                ).add(BigDecimal.ONE)
            )
        )
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG)
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 11)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.belop).isZero() },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.AVSLAG) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 4") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "*    ")
    }

    @Test
    @Order(6)
    @DisplayName("Regel 5: BM inntekt er lavere eller lik sats for fullt forskudd og søknadsbarn alder er høyere enn eller lik 11 år")
    fun skalGi125ProsentLavInntekt() {
        val inntektListe = listOf(
            Inntekt(
                INNTEKT_REFERANSE_1,
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP)
            )
        )
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG)
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 11)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.belop).isEqualByComparingTo(forventetResultatBelop125Prosent) },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.FORHOYET_FORSKUDD_11_AAR_125_PROSENT) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 5") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "*  * ")
    }

    @Test
    @Order(7)
    @DisplayName("Regel 6: BM inntekt er lavere eller lik sats for fullt forskudd og søknadsbarn alder er lavere enn 11 år")
    fun skalGi100ProsentLavInntekt() {
        val inntektListe = listOf(
            Inntekt(
                INNTEKT_REFERANSE_1,
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP)
            )
        )
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG)
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 10)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.belop).isEqualByComparingTo(forventetResultatBelop100Prosent) },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.FORHOYET_FORSKUDD_100_PROSENT) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 6") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "*  * ")
    }

    @Test
    @Order(8)
    @DisplayName("Regel 7: BM inntekt er lavere eller lik sats for 75% forskudd enslig og antall barn i husstand er 1")
    fun skalGi75ProsentEnsligEttBarn() {
        val inntektListe = listOf(
            Inntekt(
                INNTEKT_REFERANSE_1,
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP)
            )
        )
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG)
        // Søknadsbarnet er med i grunnlag antall barn i husstanden
        val barnIHusstanden = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 11)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barnIHusstanden, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.belop).isEqualByComparingTo(forventetResultatBelop75Prosent) },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 7") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "***  ")
    }

    @Test
    @Order(9)
    @DisplayName("Regel 8: BM inntekt er høyere enn sats for 75% forskudd enslig og antall barn i husstand er 1")
    fun skalGi50ProsentEnsligEttBarn() {
        val inntektListe = listOf(
            Inntekt(
                INNTEKT_REFERANSE_1,
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP).add(BigDecimal.ONE)
            )
        )
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG)
        // Søknadsbarnet er med i grunnlag antall barn i husstanden
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 11)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.belop).isEqualByComparingTo(forventetResultatBelop50Prosent) },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.REDUSERT_FORSKUDD_50_PROSENT) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 8") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "***  ")
    }

    @Test
    @Order(10)
    @DisplayName("Regel 9: BM inntekt er lavere eller lik sats for 75% forskudd enslig ++ og antall barn i husstand er mer enn 1")
    fun skalGi75ProsentEnsligMerEnnEttBarn() {
        val inntektListe = listOf(
            Inntekt(
                INNTEKT_REFERANSE_1,
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP).add(
                    finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP)
                )
            )
        )
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG)
        // Søknadsbarnet er med i grunnlag antall barn i husstanden
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 2.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 11)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.belop).isEqualByComparingTo(forventetResultatBelop75Prosent) },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 9") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "***  ")
    }

    @Test
    @Order(11)
    @DisplayName("Regel 10: BM inntekt er høyere enn sats for 75% forskudd enslig ++ og antall barn i husstand er mer enn 1")
    fun skalGi50ProsentEnsligMerEnnEttBarn() {
        val inntektListe = listOf(
            Inntekt(
                INNTEKT_REFERANSE_1,
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP).add(
                    finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP)
                ).add(BigDecimal.ONE)
            )
        )
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.ENSLIG)
        // Søknadsbarnet er med i grunnlag antall barn i husstanden
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 2.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 11)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.belop).isEqualByComparingTo(forventetResultatBelop50Prosent) },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.REDUSERT_FORSKUDD_50_PROSENT) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 10") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "***  ")
    }

    @Test
    @Order(12)
    @DisplayName("Regel 11: BM inntekt er lavere eller lik sats for 75% forskudd gift og antall barn i husstand er 1")
    fun skalGi75ProsentGiftEttBarn() {
        val inntektListe = listOf(
            Inntekt(
                INNTEKT_REFERANSE_1,
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP)
            )
        )
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.GIFT)
        // Søknadsbarnet er med i grunnlag antall barn i husstanden
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 11)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.belop).isEqualByComparingTo(forventetResultatBelop75Prosent) },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 11") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "***  ")
    }

    @Test
    @Order(13)
    @DisplayName("Regel 12: BM inntekt er høyere enn sats for 75% forskudd gift og antall barn i husstand er 1")
    fun skalGi50ProsentGiftEttBarn() {
        val inntektListe = listOf(
            Inntekt(
                INNTEKT_REFERANSE_1,
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP).add(BigDecimal.ONE)
            )
        )
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.GIFT)
        // Søknadsbarnet er med i grunnlag antall barn i husstanden
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 1.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 11)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.belop).isEqualByComparingTo(forventetResultatBelop50Prosent) },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.REDUSERT_FORSKUDD_50_PROSENT) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 12") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "***  ")
    }

    @Test
    @Order(14)
    @DisplayName("Regel 13: BM inntekt er lavere eller lik sats for 75% forskudd gift ++ og antall barn i husstand er mer enn 1")
    fun skalGi75ProsentGiftMerEnnEttBarn() {
        val inntektListe = listOf(
            Inntekt(
                INNTEKT_REFERANSE_1,
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP).add(
                    finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP)
                )
            )
        )
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.GIFT)
        // Søknadsbarnet er med i grunnlag antall barn i husstanden
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 2.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 11)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.belop).isEqualByComparingTo(forventetResultatBelop75Prosent) },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 13") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "***  ")
    }

    @Test
    @Order(15)
    @DisplayName("Regel 14: BM inntekt er høyere enn sats for 75% forskudd gift ++ og antall barn i husstand er mer enn 1 (1 inntekt)")
    fun skalGi50ProsentGiftMerEnnEttBarn_EnInntekt() {
        val inntektListe = listOf(
            Inntekt(
                INNTEKT_REFERANSE_1,
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP).add(
                    finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP).add(BigDecimal.ONE)
                )
            )
        )
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.GIFT)
        // Søknadsbarnet er med i grunnlag antall barn i husstanden
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 2.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 11)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.REDUSERT_FORSKUDD_50_PROSENT) },
            Executable { assertThat(resultat.belop).isEqualByComparingTo(forventetResultatBelop50Prosent) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 14") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "***  ")
    }

    @Test
    @Order(16)
    @DisplayName("Regel 14: BM inntekt er høyere enn sats for 75% forskudd gift ++ og antall barn i husstand er mer enn 1 (2 inntekter)")
    fun skalGi50ProsentGiftMerEnnEttBarn_ToInntekter() {
        val inntektListe = listOf(
            Inntekt(
                INNTEKT_REFERANSE_1,
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP)
            ),
            Inntekt(
                INNTEKT_REFERANSE_2,
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP).add(BigDecimal.ONE)
            )
        )
        val sivilstand = Sivilstand(SIVILSTAND_REFERANSE, SivilstandKode.GIFT)
        // Søknadsbarnet er med i grunnlag antall barn i husstanden
        val barn = BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 2.0)
        val alder = Alder(SOKNADBARN_REFERANSE, 11)
        val bostatus = Bostatus(BOSTATUS_REFERANSE, BostatusKode.BOR_MED_FORELDRE)
        lagGrunnlag(inntektListe, sivilstand, barn, alder, bostatus)
        val resultat = forskuddBeregning.beregn(grunnlag!!)
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.kode).isEqualTo(ResultatKode.REDUSERT_FORSKUDD_50_PROSENT) },
            Executable { assertThat(resultat.belop).isEqualByComparingTo(forventetResultatBelop50Prosent) },
            Executable { assertThat(resultat.regel).isEqualTo("REGEL 14") },
            Executable { assertThat(resultat.sjablonListe).isEqualTo(sjablonPeriodeNavnVerdiListe) }
        )
        printGrunnlagResultat(resultat, "***  ")
    }

    private fun lagGrunnlag(inntekt: List<Inntekt>, sivilstand: Sivilstand, barnIHusstanden: BarnIHusstanden, alder: Alder, bostatus: Bostatus) {
        grunnlag = GrunnlagBeregning(inntekt, sivilstand, barnIHusstanden, alder, bostatus, sjablonPeriodeListe)
    }

    private fun finnSjablonVerdi(sjablonPeriodeListe: List<SjablonPeriode>, sjablonTallNavn: SjablonTallNavn): BigDecimal {
        val sjablonListe = sjablonPeriodeListe.stream()
            .map(SjablonPeriode::sjablon)
            .collect(Collectors.toList())
        return SjablonUtil.hentSjablonverdi(sjablonListe, sjablonTallNavn)
    }

    private fun printGrunnlagResultat(resultat: ResultatBeregning?, betydning: String) {
        println()
        println()
        println("SJABLONVERDIER:")
        println("---------------")
        println("0005 Forskuddssats 100%:                             " + finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.FORSKUDDSSATS_BELOP))
        println(
            "0013 Multiplikator:                                  " + finnSjablonVerdi(
                sjablonPeriodeListe,
                SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR
            )
        )
        println(
            "0033 Inntektsgrense 100%:                            " + finnSjablonVerdi(
                sjablonPeriodeListe,
                SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP
            )
        )
        println(
            "0034 Inntektsgrense 75% enslig:                      " + finnSjablonVerdi(
                sjablonPeriodeListe,
                SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP
            )
        )
        println(
            "0035 Inntektsgrense 75% gift:                        " + finnSjablonVerdi(
                sjablonPeriodeListe,
                SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP
            )
        )
        println(
            "0036 Inntektsintervall:                              " + finnSjablonVerdi(
                sjablonPeriodeListe,
                SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP
            )
        )
        println(
            "0038 Forskuddssats 75%:                              " + finnSjablonVerdi(
                sjablonPeriodeListe,
                SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP
            )
        )
        println(
            "0005x0013 Maks inntektsgrense:                       " + finnSjablonVerdi(
                sjablonPeriodeListe,
                SjablonTallNavn.FORSKUDDSSATS_BELOP
            ).multiply(
                finnSjablonVerdi(sjablonPeriodeListe, SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR)
            )
        )
        println()
        println("GRUNNLAG:")
        println("---------")
        println(
            "BM inntekt:                                        " + betydning[0] + " " + grunnlag!!.inntektListe
        )
        println(
            "BM sivilstand:                                     " + betydning[1] + " " +
                grunnlag!!.sivilstand.kode.name
        )
        println("Antall barn i husstand:                            " + betydning[2] + " " + grunnlag!!.barnIHusstanden)
        println("Alder på søknadsbarn:                              " + betydning[3] + " " + grunnlag!!.soknadBarnAlder)
        println(
            "Bostedsstatus søknadsbarn:                         " + betydning[4] + " " + grunnlag!!.soknadBarnBostatus.kode.name
        )
        println()
        println(
            "Inntektsintervall totalt (0036 x (antall barn - 1)): " + finnSjablonVerdi(
                sjablonPeriodeListe,
                SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP
            ).multiply(
                BigDecimal.valueOf(
                    grunnlag!!.barnIHusstanden.antall - 1
                )
            )
        )
        println()
        println("RESULTAT:")
        println("---------")
        println("Beregnet beløp:                                      " + (resultat?.belop?.toInt() ?: "null"))
        println("Resultatkode:                                        " + (resultat?.kode?.name ?: "null"))
        println("Regel brukt i beregning:                             " + (resultat?.regel ?: "null"))
    }

    companion object {
        private const val INNTEKT_REFERANSE_1 = "INNTEKT_REFERANSE_1"
        private const val INNTEKT_REFERANSE_2 = "INNTEKT_REFERANSE_2"
        private const val SIVILSTAND_REFERANSE = "SIVILSTAND_REFERANSE"
        private const val BARN_I_HUSSTANDEN_REFERANSE_1 = "BARN_I_HUSSTANDEN_REFERANSE_1"
        private const val SOKNADBARN_REFERANSE = "SOKNADBARN_REFERANSE"
        private const val BOSTATUS_REFERANSE = "BOSTATUS_REFERANSE"
    }
}
