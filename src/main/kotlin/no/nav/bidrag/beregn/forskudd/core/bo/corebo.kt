package no.nav.bidrag.beregn.forskudd.core.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.enums.BostatusKode
import no.nav.bidrag.beregn.felles.enums.InntektType
import no.nav.bidrag.beregn.felles.enums.SivilstandKode
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

// Resultat periode
data class BeregnForskuddResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregning,
    val resultatGrunnlag: GrunnlagBeregning)

// Grunnlag beregning
data class GrunnlagBeregning(
    val bidragMottakerInntektListe: List<Inntekt>,
    val bidragMottakerSivilstandKode: SivilstandKode,
    val antallBarnIHusstand: Int,
    val soknadBarnAlder: Int,
    val soknadBarnBostatusKode: BostatusKode,
    val sjablonListe: List<Sjablon>)

data class Inntekt(
    val inntektType: InntektType,
    val inntektBelop: BigDecimal
)

// Resultat beregning
data class ResultatBeregning(
    val resultatBelop: BigDecimal,
    val resultatKode: ResultatKode,
    val resultatBeskrivelse: String,
    val sjablonListe: List<SjablonNavnVerdi>
)


// Hjelpeklasser
data class BeregnForskuddListeGrunnlag(
    val periodeResultatListe: MutableList<ResultatPeriode> = mutableListOf(),
    var justertInntektPeriodeListe: List<InntektPeriode> = emptyList(),
    var justertBidragMottakerInntektPeriodeListe: List<InntektPeriode> = emptyList(),
    var justertSivilstandPeriodeListe: List<SivilstandPeriode> = emptyList(),
    var justertBarnPeriodeListe: List<Periode> = emptyList(),
    var justertBostatusPeriodeListe: List<BostatusPeriode> = emptyList(),
    var justertAlderPeriodeListe: List<AlderPeriode> =emptyList(),
    var justertSjablonPeriodeListe: List<SjablonPeriode> = emptyList(),
    var bruddPeriodeListe: List<Periode>? = emptyList()
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
