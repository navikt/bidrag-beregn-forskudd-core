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
    companion object {
        // Juster dato til den første i neste måned (hvis ikke dato er den første i inneværende måned)
        internal fun justerDato(dato: LocalDate?): LocalDate? {
            return if (dato == null || dato.dayOfMonth == 1) dato else dato.with(TemporalAdjusters.firstDayOfNextMonth())
        }
    }

    constructor(periode: Periode) : this(justerDato(periode.datoFra) ?: periode.datoFra, justerDato(periode.datoTil))

    override fun getDatoFraTil(): Periode {
        return this
    }

    // Sjekker at en denne perioden overlapper med annenPeriode (intersect)
    fun overlapperMed(annenPeriode: Periode): Boolean {
        return ((annenPeriode.datoTil == null || datoFra.isBefore(annenPeriode.datoTil))
                && (datoTil == null || datoTil.isAfter(annenPeriode.datoFra)))
    }

    // Sjekk om perioden overlapper (datoFra i denne perioden er yngre enn datoTil i forrige periode)
    // Hvis forrige periode er null, er dette den første perioden. Ingen kontroll nødvendig
    fun overlapper(forrigePeriode: Periode?): Boolean {
        if (forrigePeriode?.datoTil == null) return false

        return datoFra.isBefore(forrigePeriode.datoTil)
    }

    // Sjekk om det er opphold (gap) mellom periodene (datoFra i denne perioden er eldre enn datoTil i forrige periode)
    // Hvis forrige periode er null, er dette den første perioden. Ingen kontroll nødvendig
    fun harOpphold(forrigePeriode: Periode?): Boolean {
        if (forrigePeriode == null) return false

        return forrigePeriode.datoTil != null && datoFra.isAfter(forrigePeriode.datoTil)
    }

    fun datoTilErEtterDatoFra(): Boolean {
        return PeriodeUtil.datoTilErEtterDatoFra(this)
    }

    // Juster datoer i perioden
    internal fun justerDatoer(): Periode {
        val fraDato = justerDato(datoFra)
        val tilDato = justerDato(datoTil)

        return Periode(fraDato ?: datoFra, tilDato)
    }
}

data class BostatusPeriode(
        val bostatusDatoFraTil: Periode,
        val bostatusKode: BostatusKode
) : PeriodisertGrunnlag {
    constructor(bostatusPeriode: BostatusPeriode) : this(bostatusPeriode.bostatusDatoFraTil.justerDatoer(), bostatusPeriode.bostatusKode)

    override fun getDatoFraTil(): Periode {
        return bostatusDatoFraTil
    }
}

data class InntektPeriode(
        val inntektDatoFraTil: Periode,
        val inntektBelop: BigDecimal
) : PeriodisertGrunnlag {
    constructor(inntektPeriode: InntektPeriode) : this(inntektPeriode.inntektDatoFraTil.justerDatoer(), inntektPeriode.inntektBelop)

    override fun getDatoFraTil(): Periode {
        return inntektDatoFraTil
    }
}

data class SivilstandPeriode(
        val sivilstandDatoFraTil: Periode,
        val sivilstandKode: SivilstandKode
) : PeriodisertGrunnlag {
    constructor(sivilstandPeriode: SivilstandPeriode) : this(sivilstandPeriode.sivilstandDatoFraTil.justerDatoer(), sivilstandPeriode.sivilstandKode)

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
    constructor(sjablonPeriode: SjablonPeriode) : this(sjablonPeriode.sjablonDatoFraTil.justerDatoer(), sjablonPeriode.sjablonVerdi)

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
