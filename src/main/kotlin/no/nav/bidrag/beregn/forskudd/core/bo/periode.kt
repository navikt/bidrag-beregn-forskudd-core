package no.nav.bidrag.beregn.forskudd.core.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.beregn.felles.enums.BostatusKode
import no.nav.bidrag.beregn.felles.enums.SivilstandKode
import java.math.BigDecimal


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
    val inntektType: InntektType,
    val inntektBelop: BigDecimal
) : PeriodisertGrunnlag {
  constructor(inntektPeriode: InntektPeriode) : this(inntektPeriode.inntektDatoFraTil.justerDatoer(), inntektPeriode.inntektType,
      inntektPeriode.inntektBelop)

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

enum class InntektType {
  LØNNSINNTEKT,
  KAPITALINNTEKT,
  BARNETRYGD,
  KONTANTSTØTTE
}
