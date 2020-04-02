package no.nav.bidrag.beregn.forskudd.core.bo

import no.nav.bidrag.beregn.forskudd.core.beregning.ResultatBeregning
import no.nav.bidrag.beregn.forskudd.core.periode.Periode
import no.nav.bidrag.beregn.forskudd.core.periode.PeriodisertGrunnlag
import java.math.BigDecimal
import java.time.LocalDate


// Grunnlag periode
data class BeregnForskuddGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadBarn: SoknadBarn,
    val bidragMottakerInntektPeriodeListe: List<InntektPeriode>,
    val bidragMottakerSivilstandPeriodeListe: List<SivilstandPeriode>,
    val bidragMottakerBarnPeriodeListe: List<Periode>? = emptyList(),
    val sjablonPeriodeListe: List<SjablonPeriode>
)

data class SoknadBarn(
    val soknadBarnFodselsdato: LocalDate,
    val soknadBarnBostatusPeriodeListe: List<BostatusPeriode>
)

data class BostatusPeriode(
    val bostatusDatoFraTil: Periode,
    val bostatusKode: BostatusKode) : PeriodisertGrunnlag {

  override fun getDatoFraTil(): Periode {
    return bostatusDatoFraTil
  }
}

data class InntektPeriode(
    val inntektDatoFraTil: Periode,
    val inntektBelop: BigDecimal) : PeriodisertGrunnlag {

  override fun getDatoFraTil(): Periode {
    return inntektDatoFraTil
  }
}

data class SivilstandPeriode(
    val sivilstandDatoFraTil: Periode,
    val sivilstandKode: SivilstandKode) : PeriodisertGrunnlag {

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
    val sjablonVerdi: Int) : PeriodisertGrunnlag {

  override fun getDatoFraTil(): Periode {
    return sjablonDatoFraTil
  }
}

data class AlderPeriode(
    val alderDatoFraTil: Periode,
    val alder: Int) : PeriodisertGrunnlag {

  override fun getDatoFraTil(): Periode {
    return alderDatoFraTil
  }
}


// Resultat periode
data class BeregnForskuddResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregning
)


// Avvik periode
data class Avvik(
    val avvikTekst: String,
    val avvikType: AvvikType
)


// Grunnlag beregning
data class GrunnlagBeregning(
    val bidragMottakerInntekt: BigDecimal,
    val bidragMottakerSivilstandKode: SivilstandKode,
    val antallBarnIHusstand: Int,
    val soknadBarnAlder: Int,
    val soknadBarnBostatusKode: BostatusKode,
    val forskuddssats100Prosent: Int,
    val multiplikatorMaksInntektsgrense: Int,
    val inntektsgrense100ProsentForskudd: Int,
    val inntektsgrenseEnslig75ProsentForskudd: Int,
    val inntektsgrenseGift75ProsentForskudd: Int,
    val inntektsintervallForskudd: Int
)


// ENUMs
enum class ResultatKode {
  AVSLAG,
  INNVILGET_50_PROSENT,
  INNVILGET_75_PROSENT,
  INNVILGET_100_PROSENT,
  INNVILGET_125_PROSENT,
  INNVILGET_200_PROSENT,
  INNVILGET_250_PROSENT
}

enum class BostatusKode {
  ALENE,
  MED_FORELDRE,
  MED_ANDRE_ENN_FORELDRE,
  ENSLIG_ASYLANT
}

enum class SivilstandKode {
  GIFT,
  ENSLIG
}

enum class AvvikType {
  PERIODER_OVERLAPPER,
  PERIODER_HAR_OPPHOLD,
  NULL_VERDI_I_DATO,
  DATO_FRA_ETTER_DATO_TIL,
  INPUT_DATA_MANGLER
}
