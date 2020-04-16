package no.nav.bidrag.beregn.forskudd.core.bo

import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.stream.Collectors

interface PeriodisertGrunnlag {
    fun getDatoFraTil(): Periode
}

data class Periode(
        var datoFra: LocalDate,
        val datoTil: LocalDate?
) : PeriodisertGrunnlag {
    constructor(periode: Periode) : this(PeriodeUtil.justerDato(periode.datoFra) ?: periode.datoFra, PeriodeUtil.justerDato(periode.datoTil))

    override fun getDatoFraTil(): Periode {
        return this
    }

    // Sjekker at en denne perioden overlapper med annenPeriode (intersect)
    fun overlapperMed(annenPeriode: Periode): Boolean {
        return ((annenPeriode.datoTil == null || datoFra.isBefore(annenPeriode.datoTil))
                && (datoTil == null || datoTil.isAfter(annenPeriode.datoFra)))
    }

    fun perioderOverlapper(forrigePeriode: Periode): Boolean {
        return PeriodeUtil.perioderOverlapper(forrigePeriode, this)
    }

    fun perioderHarOpphold(forrigePeriode: Periode): Boolean {
        return PeriodeUtil.perioderHarOpphold(forrigePeriode, this)
    }

    fun datoTilErEtterDatoFra(): Boolean {
        return PeriodeUtil.datoTilErEtterDatoFra(this)
    }

}

data class BostatusPeriode(
        val bostatusDatoFraTil: Periode,
        val bostatusKode: BostatusKode
) : PeriodisertGrunnlag {
    constructor(bostatusPeriode: BostatusPeriode) : this(PeriodeUtil.justerPeriode(bostatusPeriode.bostatusDatoFraTil), bostatusPeriode.bostatusKode)

    override fun getDatoFraTil(): Periode {
        return bostatusDatoFraTil
    }
}

data class InntektPeriode(
        val inntektDatoFraTil: Periode,
        val inntektBelop: BigDecimal
) : PeriodisertGrunnlag {
    constructor(inntektPeriode: InntektPeriode) : this(PeriodeUtil.justerPeriode(inntektPeriode.inntektDatoFraTil), inntektPeriode.inntektBelop)

    override fun getDatoFraTil(): Periode {
        return inntektDatoFraTil
    }
}

data class SivilstandPeriode(
        val sivilstandDatoFraTil: Periode,
        val sivilstandKode: SivilstandKode
) : PeriodisertGrunnlag {
    constructor(sivilstandPeriode: SivilstandPeriode) : this(
            PeriodeUtil.justerPeriode(sivilstandPeriode.sivilstandDatoFraTil), sivilstandPeriode.sivilstandKode
    )

    override fun getDatoFraTil(): Periode {
        return sivilstandDatoFraTil
    }
}

data class SjablonPeriode(
        val sjablonDatoFraTil: Periode,
        val sjablonType: String,
        val sjablonVerdi: Int
)

data class SjablonPeriodeVerdi(
        val sjablonDatoFraTil: Periode,
        val sjablonVerdi: Int
) : PeriodisertGrunnlag {
    constructor(sjablonPeriode: SjablonPeriode) : this(PeriodeUtil.justerPeriode(sjablonPeriode.sjablonDatoFraTil), sjablonPeriode.sjablonVerdi)

    override fun getDatoFraTil(): Periode {
        return sjablonDatoFraTil
    }
}

data class AlderPeriode(
        val alderDatoFraTil: Periode,
        val alder: Int
) : PeriodisertGrunnlag {

    override fun getDatoFraTil(): Periode {
        return alderDatoFraTil
    }
}

object PeriodeUtil {
    // Juster periode
    fun justerPeriode(periode: Periode): Periode {
        val fraDato = justerDato(periode.datoFra)
        val tilDato = justerDato(periode.datoTil)

        return Periode(fraDato ?: periode.datoFra, tilDato)
    }

    // Juster dato til den første i neste måned (hvis ikke dato er den første i inneværende måned)
    internal fun justerDato(dato: LocalDate?): LocalDate? {
        return if (dato == null || dato.dayOfMonth == 1) dato else dato.with(TemporalAdjusters.firstDayOfNextMonth())
    }

    // Sjekk om 2 perioder overlapper (datoFra i periode2 er mindre enn datoTil i periode1)
    // periode1 == null indikerer at periode2 er den første perioden. Ingen kontroll nødvendig
    // periode2 == null indikerer at periode1 er den siste perioden. Ingen kontroll nødvendig
    fun perioderOverlapper(periode1: Periode?, periode2: Periode?): Boolean {
        if (periode1 == null || periode2 == null) {
            return false
        }
        return if (periode1.datoTil == null || periode2.datoFra == null) {
            false
        } else periode2.datoFra.isBefore(periode1.datoTil)
    }

    // Sjekk om det er opphold (gap) mellom 2 perioder (datoFra i periode2 er større enn datoTil i periode1)
    // periode1 == null indikerer at periode2 er den første perioden. Ingen kontroll nødvendig
    // periode2 == null indikerer at periode1 er den siste perioden. Ingen kontroll nødvendig
    fun perioderHarOpphold(periode1: Periode?, periode2: Periode?): Boolean {
        if (periode1 == null || periode2 == null) {
            return false
        }
        return if (periode1.datoTil == null || periode2.datoFra == null) {
            false
        } else periode2.datoFra.isAfter(periode1.datoTil)
    }

    // Sjekk om datoFra >= datoTil
    fun datoTilErEtterDatoFra(periode: Periode): Boolean {
        return if (periode.datoFra == null || periode.datoTil == null) {
            true
        } else periode.datoTil.isAfter(periode.datoFra)
    }
}

class Periodiserer {
    private val bruddpunkter: MutableSet<LocalDate> = HashSet()
    private var aapenSluttdato = false
    fun addBruddpunkt(dato: LocalDate): Periodiserer {
        bruddpunkter.add(dato)
        return this
    }

    private fun addBruddpunkter(periode: Periode) {
        addBruddpunkt(periode.datoFra)
        if (periode.datoTil == null) {
            aapenSluttdato = true
        } else {
            addBruddpunkt(periode.datoTil)
        }
    }

    fun addBruddpunkter(grunnlag: PeriodisertGrunnlag): Periodiserer {
        addBruddpunkter(grunnlag.getDatoFraTil())
        return this
    }

    fun addBruddpunkter(grunnlagListe: Iterable<PeriodisertGrunnlag>): Periodiserer {
        for (grunnlag in grunnlagListe) {
            addBruddpunkter(grunnlag)
        }
        return this
    }

    // Genererer brudd når søknadsbarnet passerer 11 år og 18 år
    // 0 og 11 år justeres til den første inneværende måned, 18 år justeres til den første neste måned
    fun addBruddpunkter(fodselDato: LocalDate, beregnDatoFra: LocalDate, beregnDatoTil: LocalDate): Periodiserer {
        val barn11AarDato = fodselDato.plusYears(11).with(TemporalAdjusters.firstDayOfMonth())
        val barn18AarDato = fodselDato.plusYears(18).with(TemporalAdjusters.firstDayOfNextMonth())
        val barn11AarIPerioden = barn11AarDato.isAfter(beregnDatoFra.minusDays(1)) && barn11AarDato.isBefore(beregnDatoTil.plusDays(1))
        val barn18AarIPerioden = barn18AarDato.isAfter(beregnDatoFra.minusDays(1)) && barn18AarDato.isBefore(beregnDatoTil.plusDays(1))
        if (barn11AarIPerioden) {
            addBruddpunkt(barn11AarDato)
        }
        if (barn18AarIPerioden) {
            addBruddpunkt(barn18AarDato)
        }
        return this
    }

    // Setter perioder basert på fra- og til-dato
    fun finnPerioder(beregnDatoFra: LocalDate, beregnDatoTil: LocalDate): List<Periode> {
        val sortertBruddpunktListe = bruddpunkter.stream().filter { dato: LocalDate -> dato.isAfter(beregnDatoFra.minusDays(1)) }
                .filter { dato: LocalDate -> dato.isBefore(beregnDatoTil.plusDays(1)) }.sorted().collect(Collectors.toList())
        val perioder: MutableList<Periode> = ArrayList()
        val bruddpunktIt = sortertBruddpunktListe.iterator()
        if (bruddpunktIt.hasNext()) {
            var start: LocalDate? = bruddpunktIt.next()
            while (bruddpunktIt.hasNext()) {
                val end = bruddpunktIt.next()
                perioder.add(Periode(start!!, end))
                start = end
            }
            if (aapenSluttdato) {
                perioder.add(Periode(start!!, null))
            }
        }
        return perioder
    }
}