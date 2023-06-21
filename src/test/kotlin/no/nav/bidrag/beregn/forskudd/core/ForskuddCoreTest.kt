package no.nav.bidrag.beregn.forskudd.core

import no.nav.bidrag.beregn.felles.enums.AvvikType
import no.nav.bidrag.beregn.forskudd.core.TestUtil.byggAvvikListe
import no.nav.bidrag.beregn.forskudd.core.TestUtil.byggForskuddGrunnlagCore
import no.nav.bidrag.beregn.forskudd.core.TestUtil.byggForskuddResultat
import no.nav.bidrag.beregn.forskudd.core.TestUtil.byggSjablonPeriodeListe
import no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
@DisplayName("ForskuddCoreTest")
internal class ForskuddCoreTest {

    @InjectMocks
    private lateinit var forskuddCore: ForskuddCore

    @Mock
    private lateinit var forskuddPeriode: ForskuddPeriode

    private val beregnForskuddGrunnlagCore = byggForskuddGrunnlagCore()
    private val beregnForskuddResultat = byggForskuddResultat()
    private val avvikListe = byggAvvikListe()

    @Test
    @DisplayName("Skal beregne forskudd")
    fun skalBeregneForskudd() {
        Mockito.`when`(forskuddPeriode.beregnPerioder(MockitoHelper.any())).thenReturn(beregnForskuddResultat)
        val beregnForskuddResultatCore = forskuddCore.beregnForskudd(beregnForskuddGrunnlagCore)
        assertAll(
            Executable { assertThat(beregnForskuddResultatCore).isNotNull() },
            Executable { assertThat(beregnForskuddResultatCore.avvikListe).isEmpty() },
            Executable { assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe).isNotEmpty() },
            Executable { assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe).hasSize(3) },
            Executable { assertThat(beregnForskuddResultatCore.sjablonListe).isNotEmpty() },
            Executable { assertThat(beregnForskuddResultatCore.sjablonListe).hasSameSizeAs(byggSjablonPeriodeListe()) },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[0].periode.datoFom)
                    .isEqualTo(LocalDate.parse("2017-01-01"))
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[0].periode.datoTil)
                    .isEqualTo(LocalDate.parse("2018-01-01"))
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[0].resultat.belop)
                    .isEqualTo(BigDecimal.valueOf(1600))
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[0].resultat.kode)
                    .isEqualTo(ResultatKode.FORHOYET_FORSKUDD_100_PROSENT.toString())
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[0].resultat.regel)
                    .isEqualTo("REGEL 1")
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[0].grunnlagReferanseListe[0])
                    .isEqualTo(INNTEKT_REFERANSE_1)
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[0].grunnlagReferanseListe[1])
                    .isEqualTo(SIVILSTAND_REFERANSE_ENSLIG)
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[0].grunnlagReferanseListe[2])
                    .isEqualTo(BARN_I_HUSSTANDEN_REFERANSE_1)
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[0].grunnlagReferanseListe[3])
                    .isEqualTo(SOKNADBARN_REFERANSE)
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[0].grunnlagReferanseListe[4])
                    .isEqualTo(BOSTATUS_REFERANSE_MED_FORELDRE_1)
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[1].periode.datoFom)
                    .isEqualTo(LocalDate.parse("2018-01-01"))
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[1].periode.datoTil)
                    .isEqualTo(LocalDate.parse("2019-01-01"))
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[1].resultat.belop)
                    .isEqualTo(BigDecimal.valueOf(1200))
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[1].resultat.kode)
                    .isEqualTo(ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT.toString())
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[1].resultat.regel)
                    .isEqualTo("REGEL 2")
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[2].periode.datoFom)
                    .isEqualTo(LocalDate.parse("2019-01-01"))
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[2].periode.datoTil)
                    .isEqualTo(LocalDate.parse("2020-01-01"))
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[2].resultat.belop)
                    .isEqualTo(BigDecimal.valueOf(0))
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[2].resultat.kode)
                    .isEqualTo(ResultatKode.AVSLAG.toString())
            },
            Executable {
                assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe[2].resultat.regel)
                    .isEqualTo("REGEL 11")
            }
        )
    }

    @Test
    @DisplayName("Skal ikke beregne forskudd ved avvik")
    fun skalIkkeBeregneForskuddVedAvvik() {
        Mockito.`when`(forskuddPeriode.validerInput(MockitoHelper.any())).thenReturn(avvikListe)
        val beregnForskuddResultatCore = forskuddCore.beregnForskudd(beregnForskuddGrunnlagCore)
        assertAll(
            Executable { assertThat(beregnForskuddResultatCore).isNotNull() },
            Executable { assertThat(beregnForskuddResultatCore.avvikListe).isNotEmpty() },
            Executable { assertThat(beregnForskuddResultatCore.avvikListe).hasSize(1) },
            Executable {
                assertThat(beregnForskuddResultatCore.avvikListe[0].avvikTekst).isEqualTo("beregnDatoTil må være etter beregnDatoFra")
            },
            Executable {
                assertThat(beregnForskuddResultatCore.avvikListe[0].avvikType).isEqualTo(AvvikType.DATO_FOM_ETTER_DATO_TIL.toString())
            },
            Executable { assertThat(beregnForskuddResultatCore.beregnetForskuddPeriodeListe).isEmpty() }
        )
    }

    @Test
    @DisplayName("Skal kaste IllegalArgumentException ved ugyldig enum")
    fun skalKasteIllegalArgumentExceptionVedUgyldigEnum() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { forskuddCore.beregnForskudd(byggForskuddGrunnlagCore("BOR_HELT_ALENE")) }
            .withMessage("No enum constant no.nav.bidrag.beregn.felles.enums.BostatusKode.BOR_HELT_ALENE")
    }

    companion object {
        private const val INNTEKT_REFERANSE_1 = "INNTEKT_REFERANSE_1"
        private const val SIVILSTAND_REFERANSE_ENSLIG = "SIVILSTAND_REFERANSE_ENSLIG"
        private const val BARN_I_HUSSTANDEN_REFERANSE_1 = "BARN_I_HUSSTANDEN_REFERANSE_1"
        private const val SOKNADBARN_REFERANSE = "SOKNADBARN_REFERANSE"
        private const val BOSTATUS_REFERANSE_MED_FORELDRE_1 = "BOSTATUS_REFERANSE_MED_FORELDRE_1"
    }

    object MockitoHelper {
        fun <T> any(type: Class<T>): T = Mockito.any(type)
        fun <T> any(): T = Mockito.any()
    }
}