package no.nav.bidrag.beregn.forskudd.core.dto

import java.math.BigDecimal
import java.time.LocalDate

data class BeregnForskuddGrunnlagDto(var beregnDatoFra: LocalDate? = null,
                                     var beregnDatoTil: LocalDate? = null,
                                     var soknadBarn: List<SoknadBarn> = emptyList(),
                                     var bidragMottakerInntektPeriodeListe: List<BidragMottakerInntektPeriodeListe> = emptyList(),
                                     var bidragMottakerSivilstandPeriodeListe: List<BidragMottakerSivilstandPeriodeListe> = emptyList(),
                                     var bidragMottakerBarnPeriodeListe: List<BidragMottakerBarnPeriodeListe?> = emptyList()
)

data class SoknadBarn(
        var soknadBarnFodselsdato: LocalDate? = null,
        var bostatusPeriode: List<BostatusPeriode?> = emptyList()
)

data class BostatusPeriode(
        var datoFra: LocalDate? = null,
        var datoTil: LocalDate? = null,
        var bostedStatusKode: String? = null
)

data class BidragMottakerInntektPeriodeListe(
        var datoFra: LocalDate? = null,
        var datoTil: LocalDate? = null,
        var belop: BigDecimal? = null
)

data class BidragMottakerSivilstandPeriodeListe(
        var datoFra: LocalDate? = null,
        var datoTil: LocalDate? = null,
        var sivilstandKode: String? = null
)

data class BidragMottakerBarnPeriodeListe(
        var datoFra: LocalDate? = null,
        var datoTil: LocalDate? = null
)


data class BeregnForskuddResultatDto(
        var periodeResultatListe: List<PeriodeResultat> = emptyList()
)

data class PeriodeResultat(
        var datoFraTil: Periode?,
        var forskuddBeregningResultat: ForskuddBeregningResultat
)

data class Periode(
        var datoFra: LocalDate? = null,
        var datoTil: LocalDate? = null) : PeriodisertGrunnlag {
    override fun getDatoFraTil(): Periode? {
        return this
    }

    // Sjekker at en denne perioden overlapper med annenPeriode (intersect)
    fun overlapperMed(annenPeriode: no.nav.bidrag.beregn.forskudd.periode.grunnlag.Periode): Boolean {
        return ((annenPeriode.datoTil == null || datoFra!!.isBefore(annenPeriode.datoTil))
                && (datoTil == null || datoTil!!.isAfter(annenPeriode.datoFra)))
    }
}

interface PeriodisertGrunnlag {
    fun getDatoFraTil(): Periode?
}

data class ForskuddBeregningResultat(
        var belop: BigDecimal? = null,
        var resultatKode: String? = null,
        var resultatBeskrivelse: String? = null
)