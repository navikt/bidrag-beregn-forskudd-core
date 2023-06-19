package no.nav.bidrag.beregn.forskudd.core

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.enums.BostatusKode
import no.nav.bidrag.beregn.felles.enums.InntektType
import no.nav.bidrag.beregn.felles.enums.SivilstandKode
import no.nav.bidrag.beregn.felles.periode.Periodiserer
import no.nav.bidrag.beregn.forskudd.core.bo.BarnIHusstandenPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate

@DisplayName("PeriodisererTest")
internal class PeriodisererTest {
    @Test
    fun testPeriodiseringMedGrunnlag() {
        val perioder = Periodiserer()
            .addBruddpunkter(
                InntektPeriode(
                    INNTEKT_REFERANSE_1,
                    Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-03-01")),
                    InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                    BigDecimal.valueOf(1000)
                )
            )
            .addBruddpunkter(
                SivilstandPeriode(
                    SIVILSTAND_REFERANSE_GIFT,
                    Periode(LocalDate.parse("2019-02-01"), LocalDate.parse("2019-04-01")),
                    SivilstandKode.GIFT
                )
            )
            .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"))

        assertAll(
            Executable { assertThat(perioder).isNotNull() },
            Executable { assertThat(perioder).hasSize(3) },
            Executable { assertThat(perioder[0].datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(perioder[0].datoTil).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(perioder[1].datoFom).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(perioder[1].datoTil).isEqualTo(LocalDate.parse("2019-03-01")) },
            Executable { assertThat(perioder[2].datoFom).isEqualTo(LocalDate.parse("2019-03-01")) },
            Executable { assertThat(perioder[2].datoTil).isEqualTo(LocalDate.parse("2019-04-01")) }
        )
    }

    @Test
    fun testDuplikatePerioder() {
        val perioder = Periodiserer()
            .addBruddpunkter(
                InntektPeriode(
                    INNTEKT_REFERANSE_1,
                    Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-04-01")),
                    InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                    BigDecimal.valueOf(1000)
                )
            )
            .addBruddpunkter(
                SivilstandPeriode(
                    SIVILSTAND_REFERANSE_GIFT,
                    Periode(LocalDate.parse("2019-02-01"), LocalDate.parse("2019-04-01")),
                    SivilstandKode.GIFT
                )
            )
            .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"))

        assertAll(
            Executable { assertThat(perioder).isNotNull() },
            Executable { assertThat(perioder).hasSize(2) },
            Executable { assertThat(perioder[0].datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(perioder[0].datoTil).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(perioder[1].datoFom).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(perioder[1].datoTil).isEqualTo(LocalDate.parse("2019-04-01")) }
        )
    }

    @Test
    fun testPeriodiseringMedGrunnlagOgAapenSlutt() {
        val perioder = Periodiserer()
            .addBruddpunkter(SivilstandPeriode(SIVILSTAND_REFERANSE_GIFT, Periode(LocalDate.parse("2019-02-01"), null), SivilstandKode.GIFT))
            .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"))

        assertAll(
            Executable { assertThat(perioder).isNotNull() },
            Executable { assertThat(perioder).hasSize(1) },
            Executable { assertThat(perioder[0].datoFom).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(perioder[0].datoTil).isNull() }
        )
    }

    @Test
    fun testPeriodiseringMedUtvidetGrunnlag() {
        val perioder = Periodiserer()
            .addBruddpunkter(
                InntektPeriode(
                    INNTEKT_REFERANSE_1,
                    Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
                    InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                    BigDecimal.valueOf(250000)
                )
            )
            .addBruddpunkter(
                InntektPeriode(
                    INNTEKT_REFERANSE_2,
                    Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
                    InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                    BigDecimal.valueOf(400000)
                )
            )
            .addBruddpunkter(
                InntektPeriode(
                    INNTEKT_REFERANSE_3,
                    Periode(LocalDate.parse("2019-01-01"), null),
                    InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
                    BigDecimal.valueOf(500000)
                )
            )
            .addBruddpunkter(
                SivilstandPeriode(
                    SIVILSTAND_REFERANSE_GIFT,
                    Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-04-17")),
                    SivilstandKode.GIFT
                )
            )
            .addBruddpunkter(
                SivilstandPeriode(
                    SIVILSTAND_REFERANSE_ENSLIG,
                    Periode(LocalDate.parse("2018-04-17"), null),
                    SivilstandKode.ENSLIG
                )
            )
            .addBruddpunkter(
                BarnIHusstandenPeriode(
                    BARN_REFERANSE_1,
                    Periode(LocalDate.parse("2017-01-01"), null),
                    1.0
                )
            )
            .addBruddpunkter(
                BarnIHusstandenPeriode(
                    BARN_REFERANSE_2,
                    Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-16")),
                    1.0
                )
            )
            .addBruddpunkter(
                BarnIHusstandenPeriode(
                    BARN_REFERANSE_3,
                    Periode(LocalDate.parse("2019-03-31"), null),
                    1.0
                )
            )
            .addBruddpunkter(
                BostatusPeriode(
                    BOSTATUS_REFERANSE_MED_FORELDRE_1,
                    Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-08-16")),
                    BostatusKode.BOR_MED_FORELDRE
                )
            )
            .addBruddpunkter(
                BostatusPeriode(
                    BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE,
                    Periode(LocalDate.parse("2018-08-16"), LocalDate.parse("2018-11-13")),
                    BostatusKode.BOR_IKKE_MED_FORELDRE
                )
            )
            .addBruddpunkter(
                BostatusPeriode(
                    BOSTATUS_REFERANSE_MED_FORELDRE_2,
                    Periode(LocalDate.parse("2018-11-13"), null),
                    BostatusKode.BOR_MED_FORELDRE
                )
            )
            .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"))

        assertAll(
            Executable { assertThat(perioder).isNotNull() },
            Executable { assertThat(perioder).hasSize(8) },
            Executable { assertThat(perioder[0].datoFom).isEqualTo(LocalDate.parse("2017-01-01")) },
            Executable { assertThat(perioder[0].datoTil).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { assertThat(perioder[1].datoFom).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { assertThat(perioder[1].datoTil).isEqualTo(LocalDate.parse("2018-04-17")) },
            Executable { assertThat(perioder[2].datoFom).isEqualTo(LocalDate.parse("2018-04-17")) },
            Executable { assertThat(perioder[2].datoTil).isEqualTo(LocalDate.parse("2018-06-16")) },
            Executable { assertThat(perioder[3].datoFom).isEqualTo(LocalDate.parse("2018-06-16")) },
            Executable { assertThat(perioder[3].datoTil).isEqualTo(LocalDate.parse("2018-08-16")) },
            Executable { assertThat(perioder[4].datoFom).isEqualTo(LocalDate.parse("2018-08-16")) },
            Executable { assertThat(perioder[4].datoTil).isEqualTo(LocalDate.parse("2018-11-13")) },
            Executable { assertThat(perioder[5].datoFom).isEqualTo(LocalDate.parse("2018-11-13")) },
            Executable { assertThat(perioder[5].datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(perioder[6].datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(perioder[6].datoTil).isEqualTo(LocalDate.parse("2019-03-31")) },
            Executable { assertThat(perioder[7].datoFom).isEqualTo(LocalDate.parse("2019-03-31")) },
            Executable { assertThat(perioder[7].datoTil).isNull() }
        )
    }

    companion object {
        private const val INNTEKT_REFERANSE_1 = "INNTEKT_REFERANSE_1"
        private const val INNTEKT_REFERANSE_2 = "INNTEKT_REFERANSE_2"
        private const val INNTEKT_REFERANSE_3 = "INNTEKT_REFERANSE_3"
        private const val SIVILSTAND_REFERANSE_GIFT = "SIVILSTAND_REFERANSE_GIFT"
        private const val SIVILSTAND_REFERANSE_ENSLIG = "SIVILSTAND_REFERANSE_ENSLIG"
        private const val BARN_REFERANSE_1 = "BARN_REFERANSE_1"
        private const val BARN_REFERANSE_2 = "BARN_REFERANSE_2"
        private const val BARN_REFERANSE_3 = "BARN_REFERANSE_3"
        private const val BOSTATUS_REFERANSE_MED_FORELDRE_1 = "BOSTATUS_REFERANSE_MED_FORELDRE_1"
        private const val BOSTATUS_REFERANSE_MED_FORELDRE_2 = "BOSTATUS_REFERANSE_MED_FORELDRE_2"
        private const val BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE = "BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE"
    }
}