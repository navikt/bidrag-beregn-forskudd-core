package no.nav.bidrag.beregn.forskudd.core.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.domain.enums.BostatusKode
import no.nav.bidrag.domain.enums.SivilstandKode
import java.math.BigDecimal

data class BostatusPeriode(
    val referanse: String,
    val bostatusPeriode: Periode,
    val kode: BostatusKode
) : PeriodisertGrunnlag {

    constructor(bostatusPeriode: BostatusPeriode) : this(
        bostatusPeriode.referanse,
        bostatusPeriode.bostatusPeriode.justerDatoer(),
        bostatusPeriode.kode
    )

    override fun getPeriode(): Periode {
        return bostatusPeriode
    }
}

data class InntektPeriode(
    val referanse: String,
    val inntektPeriode: Periode,
    val type: String,
    val belop: BigDecimal
) : PeriodisertGrunnlag {

    constructor(inntektPeriode: InntektPeriode) : this(
        inntektPeriode.referanse,
        inntektPeriode.inntektPeriode.justerDatoer(),
        inntektPeriode.type,
        inntektPeriode.belop
    )

    override fun getPeriode(): Periode {
        return inntektPeriode
    }
}

data class SivilstandPeriode(
    val referanse: String,
    val sivilstandPeriode: Periode,
    val kode: SivilstandKode
) : PeriodisertGrunnlag {

    constructor(sivilstandPeriode: SivilstandPeriode) : this(
        sivilstandPeriode.referanse,
        sivilstandPeriode.sivilstandPeriode.justerDatoer(),
        sivilstandPeriode.kode
    )

    override fun getPeriode(): Periode {
        return sivilstandPeriode
    }
}

data class AlderPeriode(
    val referanse: String,
    val alderPeriode: Periode,
    val alder: Int
) : PeriodisertGrunnlag {

    override fun getPeriode(): Periode {
        return alderPeriode
    }
}

data class BarnIHusstandenPeriode(
    val referanse: String,
    val barnIHusstandenPeriode: Periode,
    val antall: Double
) : PeriodisertGrunnlag {

    constructor(barnIHusstandenPeriode: BarnIHusstandenPeriode) : this(
        barnIHusstandenPeriode.referanse,
        barnIHusstandenPeriode.barnIHusstandenPeriode.justerDatoer(),
        barnIHusstandenPeriode.antall
    )

    override fun getPeriode(): Periode {
        return barnIHusstandenPeriode
    }
}
