package no.nav.bidrag.beregn.forskudd.core

import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.forskudd.core.bo.Alder
import no.nav.bidrag.beregn.forskudd.core.bo.BarnIHusstanden
import no.nav.bidrag.beregn.forskudd.core.bo.BarnIHusstandenPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat
import no.nav.bidrag.beregn.forskudd.core.bo.Bostatus
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.forskudd.core.bo.Inntekt
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatBeregning
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.Sivilstand
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.SoknadBarn
import no.nav.bidrag.beregn.forskudd.core.dto.BarnIHusstandenPeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore
import no.nav.bidrag.beregn.forskudd.core.dto.BostatusPeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.InntektPeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.SivilstandPeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.SoknadBarnCore
import no.nav.bidrag.domain.enums.AvvikType
import no.nav.bidrag.domain.enums.BostatusKode
import no.nav.bidrag.domain.enums.InntektType
import no.nav.bidrag.domain.enums.SivilstandKode
import no.nav.bidrag.domain.enums.resultatkoder.ResultatKodeForskudd
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn
import java.math.BigDecimal
import java.time.LocalDate

object TestUtil {

    private const val INNTEKT_REFERANSE_1 = "INNTEKT_REFERANSE_1"
    private const val INNTEKT_REFERANSE_2 = "INNTEKT_REFERANSE_2"
    private const val INNTEKT_REFERANSE_3 = "INNTEKT_REFERANSE_3"
    private const val SIVILSTAND_REFERANSE_GIFT = "SIVILSTAND_REFERANSE_GIFT"
    private const val SIVILSTAND_REFERANSE_ENSLIG = "SIVILSTAND_REFERANSE_ENSLIG"
    private const val BARN_I_HUSSTANDEN_REFERANSE_1 = "BARN_I_HUSSTANDEN_REFERANSE_1"
    private const val BARN_I_HUSSTANDEN_REFERANSE_2 = "BARN_I_HUSSTANDEN_REFERANSE_2"
    private const val BARN_I_HUSSTANDEN_REFERANSE_3 = "BARN_I_HUSSTANDEN_REFERANSE_3"
    private const val BARN_I_HUSSTANDEN_REFERANSE_4 = "BARN_I_HUSSTANDEN_REFERANSE_4"
    private const val SOKNADBARN_REFERANSE = "SOKNADBARN_REFERANSE"
    private const val BOSTATUS_REFERANSE_MED_FORELDRE_1 = "BOSTATUS_REFERANSE_MED_FORELDRE_1"
    private const val BOSTATUS_REFERANSE_MED_FORELDRE_2 = "BOSTATUS_REFERANSE_MED_FORELDRE_2"
    private const val BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE = "BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE"

    fun byggSjablonPeriodeNavnVerdiListe() =
        // Sjablontall
        listOf(
            SjablonPeriodeNavnVerdi(
                Periode(LocalDate.parse("2017-01-01"), null),
                SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP.navn,
                BigDecimal.valueOf(1280)
            ),
            SjablonPeriodeNavnVerdi(
                Periode(LocalDate.parse("2017-01-01"), null),
                SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                BigDecimal.valueOf(1710)
            ),
            SjablonPeriodeNavnVerdi(
                Periode(LocalDate.parse("2017-01-01"), null),
                SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.navn,
                BigDecimal.valueOf(320)
            ),
            SjablonPeriodeNavnVerdi(
                Periode(LocalDate.parse("2017-01-01"), null),
                SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.navn,
                BigDecimal.valueOf(270200)
            ),
            SjablonPeriodeNavnVerdi(
                Periode(LocalDate.parse("2017-01-01"), null),
                SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.navn,
                BigDecimal.valueOf(419700)
            ),
            SjablonPeriodeNavnVerdi(
                Periode(LocalDate.parse("2017-01-01"), null),
                SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.navn,
                BigDecimal.valueOf(336500)
            ),
            SjablonPeriodeNavnVerdi(
                Periode(LocalDate.parse("2017-01-01"), null),
                SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.navn,
                BigDecimal.valueOf(61700)
            )
        )

    fun byggSjablonPeriodeListe() =

        listOf(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), null),
                Sjablon(
                    SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(1710)))
                )
            ),
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), null),
                Sjablon(
                    SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(320)))
                )
            ),
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), null),
                Sjablon(
                    SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(270200)))
                )
            ),
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), null),
                Sjablon(
                    SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(419700)))
                )
            ),
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), null),
                Sjablon(
                    SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(336500)))
                )
            ),
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), null),
                Sjablon(
                    SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(61700)))
                )
            ),
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), null),
                Sjablon(
                    SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(1280)))
                )
            )
        )

    fun byggForskuddGrunnlagCore(): BeregnForskuddGrunnlagCore {
        return byggForskuddGrunnlagCore(BostatusKode.BOR_MED_FORELDRE.toString())
    }

    fun byggForskuddGrunnlagCore(bostatus: String): BeregnForskuddGrunnlagCore {
        val soknadBarn = SoknadBarnCore(SOKNADBARN_REFERANSE, LocalDate.parse("2006-05-12"))

        val bostatusPeriodeListe = listOf(
            BostatusPeriodeCore(
                BOSTATUS_REFERANSE_MED_FORELDRE_1,
                PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
                bostatus
            )
        )

        val inntektPeriodeListe = listOf(
            InntektPeriodeCore(
                INNTEKT_REFERANSE_1,
                PeriodeCore(LocalDate.parse("2017-01-01"), null),
                InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER.toString(),
                BigDecimal.ZERO
            )
        )

        val bidragMottakerSivilstandPeriodeListe = listOf(
            SivilstandPeriodeCore(
                SIVILSTAND_REFERANSE_GIFT,
                PeriodeCore(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-01-01")),
                SivilstandKode.GIFT.toString()
            ),
            SivilstandPeriodeCore(
                SIVILSTAND_REFERANSE_ENSLIG,
                PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
                SivilstandKode.ENSLIG.toString()
            )
        )

        val bidragMottakerBarnPeriodeListe = listOf(
            BarnIHusstandenPeriodeCore(
                BARN_I_HUSSTANDEN_REFERANSE_1,
                PeriodeCore(
                    LocalDate.parse("2017-01-01"),
                    LocalDate.parse("2020-01-01")
                ),
                1.0
            )
        )

        return BeregnForskuddGrunnlagCore(
            LocalDate.parse("2017-01-01"),
            LocalDate.parse("2020-01-01"),
            soknadBarn,
            bostatusPeriodeListe,
            inntektPeriodeListe,
            bidragMottakerSivilstandPeriodeListe,
            bidragMottakerBarnPeriodeListe,
            emptyList()
        )
    }

    fun byggForskuddResultat(): BeregnForskuddResultat {
        val periodeResultatListe = listOf(
            ResultatPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
                ResultatBeregning(
                    BigDecimal.valueOf(1600),
                    ResultatKodeForskudd.FORHOYET_FORSKUDD_100_PROSENT,
                    "REGEL 1",
                    byggSjablonPeriodeNavnVerdiListe()
                ),
                GrunnlagBeregning(
                    listOf(
                        Inntekt(INNTEKT_REFERANSE_1, "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER", BigDecimal.valueOf(500000))
                    ),
                    Sivilstand(SIVILSTAND_REFERANSE_ENSLIG, SivilstandKode.ENSLIG),
                    BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 2.0),
                    Alder(SOKNADBARN_REFERANSE, 10),
                    Bostatus(BOSTATUS_REFERANSE_MED_FORELDRE_1, BostatusKode.BOR_MED_FORELDRE),
                    byggSjablonPeriodeListe()
                )
            ),
            ResultatPeriode(
                Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
                ResultatBeregning(
                    BigDecimal.valueOf(1200),
                    ResultatKodeForskudd.ORDINAERT_FORSKUDD_75_PROSENT,
                    "REGEL 2",
                    byggSjablonPeriodeNavnVerdiListe()
                ),
                GrunnlagBeregning(
                    listOf(
                        Inntekt(INNTEKT_REFERANSE_2, "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER", BigDecimal.valueOf(500000))
                    ),
                    Sivilstand(SIVILSTAND_REFERANSE_ENSLIG, SivilstandKode.ENSLIG),
                    BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 2.0),
                    Alder(SOKNADBARN_REFERANSE, 10),
                    Bostatus(BOSTATUS_REFERANSE_MED_FORELDRE_1, BostatusKode.BOR_MED_FORELDRE),
                    byggSjablonPeriodeListe()
                )
            ),
            ResultatPeriode(
                Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
                ResultatBeregning(BigDecimal.valueOf(0), ResultatKodeForskudd.AVSLAG, "REGEL 11", byggSjablonPeriodeNavnVerdiListe()),
                GrunnlagBeregning(
                    listOf(
                        Inntekt(INNTEKT_REFERANSE_3, "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER", BigDecimal.valueOf(500000))
                    ),
                    Sivilstand(SIVILSTAND_REFERANSE_ENSLIG, SivilstandKode.ENSLIG),
                    BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 2.0),
                    Alder(SOKNADBARN_REFERANSE, 10),
                    Bostatus(BOSTATUS_REFERANSE_MED_FORELDRE_1, BostatusKode.BOR_MED_FORELDRE),
                    byggSjablonPeriodeListe()
                )
            )
        )
        return BeregnForskuddResultat(periodeResultatListe)
    }

    fun byggAvvikListe(): List<Avvik> {
        return listOf(Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FOM_ETTER_DATO_TIL))
    }

    fun byggForskuddGrunnlag(): BeregnForskuddGrunnlag {
        return byggForskuddGrunnlag("2017-01-01", "2019-08-01")
    }

    fun byggForskuddGrunnlag(beregnDatoFra: String, beregnDatoTil: String): BeregnForskuddGrunnlag {
        val fodselsdato = LocalDate.parse("2006-12-19")

        val bostatusListe = listOf(
            BostatusPeriode(
                BOSTATUS_REFERANSE_MED_FORELDRE_1,
                Periode(LocalDate.parse("2006-12-19"), LocalDate.parse("2018-08-16")),
                BostatusKode.BOR_MED_FORELDRE
            ),
            BostatusPeriode(
                BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE,
                Periode(LocalDate.parse("2018-08-16"), LocalDate.parse("2018-11-13")),
                BostatusKode.BOR_IKKE_MED_FORELDRE
            ),
            BostatusPeriode(
                BOSTATUS_REFERANSE_MED_FORELDRE_2,
                Periode(LocalDate.parse("2018-11-13"), null),
                BostatusKode.BOR_MED_FORELDRE
            )
        )

        val soknadBarn = SoknadBarn(SOKNADBARN_REFERANSE, fodselsdato)

        val inntektListe = listOf(
            InntektPeriode(
                INNTEKT_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
                "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                BigDecimal.valueOf(250000)
            ),
            InntektPeriode(
                INNTEKT_REFERANSE_2,
                Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
                "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                BigDecimal.valueOf(400000)
            ),
            InntektPeriode(
                INNTEKT_REFERANSE_3,
                Periode(LocalDate.parse("2019-01-01"), null),
                "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                BigDecimal.valueOf(500000)
            )
        )

        val sivilstandListe = listOf(
            SivilstandPeriode(
                SIVILSTAND_REFERANSE_GIFT,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-04-17")),
                SivilstandKode.GIFT
            ),
            SivilstandPeriode(
                SIVILSTAND_REFERANSE_ENSLIG,
                Periode(LocalDate.parse("2018-04-17"), LocalDate.parse("2019-08-01")),
                SivilstandKode.ENSLIG
            )
        )

        val barnIHusstandenListe = listOf(
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_1,
                Periode(LocalDate.parse("2006-12-19"), LocalDate.parse("2017-01-01")),
                1.0
            ),
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-17")),
                3.0
            ),
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_2,
                Periode(LocalDate.parse("2018-06-17"), LocalDate.parse("2018-08-16")),
                2.0
            ),
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_2,
                Periode(LocalDate.parse("2018-08-16"), LocalDate.parse("2018-11-13")),
                1.0
            ),
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_3,
                Periode(LocalDate.parse("2018-11-13"), LocalDate.parse("2019-03-31")),
                2.0
            ),
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_4,
                Periode(LocalDate.parse("2019-03-31"), null),
                3.0
            )
        )

        return BeregnForskuddGrunnlag(
            LocalDate.parse(beregnDatoFra),
            LocalDate.parse(beregnDatoTil),
            soknadBarn,
            bostatusListe,
            inntektListe,
            sivilstandListe,
            barnIHusstandenListe,
            byggSjablonPeriodeListe()
        )
    }

    fun byggForskuddGrunnlagUtenSivilstand(): BeregnForskuddGrunnlag {
        val beregnDatoFra = LocalDate.parse("2017-01-01")
        val beregnDatoTil = LocalDate.parse("2017-02-01")
        val fodselsdato = LocalDate.parse("2006-12-19")

        val bostatusListe = listOf(
            BostatusPeriode(
                BOSTATUS_REFERANSE_MED_FORELDRE_1,
                Periode(LocalDate.parse("2006-12-19"), LocalDate.parse("2018-08-16")),
                BostatusKode.BOR_MED_FORELDRE
            ),
            BostatusPeriode(
                BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE,
                Periode(LocalDate.parse("2018-08-16"), LocalDate.parse("2018-11-13")),
                BostatusKode.BOR_IKKE_MED_FORELDRE
            ),
            BostatusPeriode(
                BOSTATUS_REFERANSE_MED_FORELDRE_2,
                Periode(LocalDate.parse("2018-11-13"), null),
                BostatusKode.BOR_MED_FORELDRE
            )
        )

        val soknadBarn = SoknadBarn(SOKNADBARN_REFERANSE, fodselsdato)

        val inntektListe = listOf(
            InntektPeriode(
                INNTEKT_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
                "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                BigDecimal.valueOf(250000)
            ),
            InntektPeriode(
                INNTEKT_REFERANSE_2,
                Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
                "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                BigDecimal.valueOf(400000)
            ),
            InntektPeriode(
                INNTEKT_REFERANSE_3,
                Periode(LocalDate.parse("2019-01-01"), null),
                "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                BigDecimal.valueOf(500000)
            )
        )

        val barnIHusstandenListe = listOf(
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_1,
                Periode(LocalDate.parse("2006-12-19"), LocalDate.parse("2017-01-01")),
                1.0
            ),
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-17")),
                3.0
            ),
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_2,
                Periode(LocalDate.parse("2018-06-17"), LocalDate.parse("2018-08-16")),
                2.0
            ),
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_2,
                Periode(LocalDate.parse("2018-08-16"), LocalDate.parse("2018-11-13")),
                1.0
            ),
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_3,
                Periode(LocalDate.parse("2018-11-13"), LocalDate.parse("2019-03-31")),
                2.0
            )
        )

        return BeregnForskuddGrunnlag(
            beregnDatoFra,
            beregnDatoTil,
            soknadBarn,
            bostatusListe,
            inntektListe,
            emptyList(),
            barnIHusstandenListe,
            byggSjablonPeriodeListe()
        )
    }

    fun byggForskuddGrunnlagMedAvvik(): BeregnForskuddGrunnlag {
        val beregnDatoFra = LocalDate.parse("2017-01-01")
        val beregnDatoTil = LocalDate.parse("2017-01-01")
        val fodselsdato = LocalDate.parse("2006-12-19")

        val bostatusListe = listOf(
            BostatusPeriode(
                BOSTATUS_REFERANSE_MED_FORELDRE_1,
                Periode(LocalDate.parse("2006-12-19"), LocalDate.parse("2018-08-16")),
                BostatusKode.BOR_MED_FORELDRE
            ),
            BostatusPeriode(
                BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE,
                Periode(LocalDate.parse("2018-08-16"), null),
                BostatusKode.BOR_IKKE_MED_FORELDRE
            ),
            BostatusPeriode(
                BOSTATUS_REFERANSE_MED_FORELDRE_2,
                Periode(LocalDate.parse("2018-11-13"), null),
                BostatusKode.BOR_MED_FORELDRE
            )
        )

        val soknadBarn = SoknadBarn(SOKNADBARN_REFERANSE, fodselsdato)

        val inntektListe = listOf(
            InntektPeriode(
                INNTEKT_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
                "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                BigDecimal.valueOf(250000)
            ),
            InntektPeriode(
                INNTEKT_REFERANSE_2,
                Periode(LocalDate.parse("2018-01-04"), LocalDate.parse("2019-01-01")),
                "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                BigDecimal.valueOf(400000)
            ),
            InntektPeriode(
                INNTEKT_REFERANSE_3,
                Periode(LocalDate.parse("2019-01-01"), null),
                "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                BigDecimal.valueOf(500000)
            )
        )

        val sivilstandListe = listOf(
            SivilstandPeriode(
                SIVILSTAND_REFERANSE_GIFT,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-04-01")),
                SivilstandKode.GIFT
            ),
            SivilstandPeriode(
                SIVILSTAND_REFERANSE_ENSLIG,
                Periode(LocalDate.parse("2018-03-17"), LocalDate.parse("2019-07-01")),
                SivilstandKode.ENSLIG
            )
        )

        val barnIHusstandenListe = listOf(
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), null),
                1.0
            ),
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_2,
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-17")),
                1.0
            ),
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_3,
                Periode(LocalDate.parse("2019-03-31"), LocalDate.parse("2018-06-17")),
                1.0
            )
        )

        return BeregnForskuddGrunnlag(
            beregnDatoFra,
            beregnDatoTil,
            soknadBarn,
            bostatusListe,
            inntektListe,
            sivilstandListe,
            barnIHusstandenListe,
            byggSjablonPeriodeListe()
        )
    }

    fun byggForskuddGrunnlagUtenAndreBarn(): BeregnForskuddGrunnlag {
        val beregnDatoFra = LocalDate.parse("2017-01-01")
        val beregnDatoTil = LocalDate.parse("2017-02-01")
        val fodselsdato = LocalDate.parse("2006-12-19")

        val bostatusListe = listOf(
            BostatusPeriode(
                BOSTATUS_REFERANSE_MED_FORELDRE_1,
                Periode(LocalDate.parse("2006-12-19"), null),
                BostatusKode.BOR_MED_FORELDRE
            )
        )

        val soknadBarn = SoknadBarn(SOKNADBARN_REFERANSE, fodselsdato)

        val inntektListe = listOf(
            InntektPeriode(
                INNTEKT_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), null),
                "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                BigDecimal.valueOf(250000)
            )
        )

        val sivilstandListe = listOf(
            SivilstandPeriode(
                SIVILSTAND_REFERANSE_GIFT,
                Periode(LocalDate.parse("2017-01-01"), null),
                SivilstandKode.GIFT
            )
        )

        val barnIHusstandenListe = listOf(
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), null),
                1.0
            )
        )

        return BeregnForskuddGrunnlag(
            beregnDatoFra,
            beregnDatoTil,
            soknadBarn,
            bostatusListe,
            inntektListe,
            sivilstandListe,
            barnIHusstandenListe,
            byggSjablonPeriodeListe()
        )
    }

    fun byggForskuddGrunnlagMedFlereInntekterISammePeriode(inntektListe: List<InntektPeriode>): BeregnForskuddGrunnlag {
        val beregnDatoFra = LocalDate.parse("2017-01-01")
        val beregnDatoTil = LocalDate.parse("2018-01-01")
        val fodselsdato = LocalDate.parse("2007-12-19")

        val bostatusListe = listOf(
            BostatusPeriode(
                BOSTATUS_REFERANSE_MED_FORELDRE_1,
                Periode(LocalDate.parse("2017-01-01"), null),
                BostatusKode.BOR_MED_FORELDRE
            )
        )

        val soknadBarn = SoknadBarn(SOKNADBARN_REFERANSE, fodselsdato)

        val sivilstandListe = listOf(
            SivilstandPeriode(
                SIVILSTAND_REFERANSE_ENSLIG,
                Periode(LocalDate.parse("2017-01-01"), null),
                SivilstandKode.ENSLIG
            )
        )

        val barnIHusstandenListe = listOf(
            BarnIHusstandenPeriode(
                BARN_I_HUSSTANDEN_REFERANSE_1,
                Periode(LocalDate.parse("2017-01-01"), null),
                2.0
            )
        )

        return BeregnForskuddGrunnlag(
            beregnDatoFra,
            beregnDatoTil,
            soknadBarn,
            bostatusListe,
            inntektListe,
            sivilstandListe,
            barnIHusstandenListe,
            byggSjablonPeriodeListe()
        )
    }
}
