package no.nav.bidrag.beregn.forskudd.core.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag
data class BeregnForskuddGrunnlagCore(
  val beregnDatoFra: LocalDate,
  val beregnDatoTil: LocalDate,
  val soknadBarn: SoknadBarnCore,
  val bidragMottakerInntektPeriodeListe: List<InntektPeriodeCore>,
  val bidragMottakerSivilstandPeriodeListe: List<SivilstandPeriodeCore>,
  val bidragMottakerBarnPeriodeListe: List<BarnPeriodeCore>,
  var sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class SoknadBarnCore(
  val referanse: String,
  val fodselsdato: LocalDate,
  val bostatusPeriodeListe: List<BostatusPeriodeCore>
)

data class BostatusPeriodeCore(
  val referanse: String,
  val periode: PeriodeCore,
  val kode: String
)

data class InntektPeriodeCore(
  val referanse: String,
  val periode: PeriodeCore,
  val type: String,
  val belop: BigDecimal
)

data class SivilstandPeriodeCore(
  val referanse: String,
  val periode: PeriodeCore,
  val kode: String
)

data class BarnPeriodeCore(
  val referanse: String,
  val periode: PeriodeCore
)

// Resultat
data class BeregnetForskuddResultatCore(
  val beregnetForskuddPeriodeListe: List<ResultatPeriodeCore>,
  val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
  val periode: PeriodeCore,
  val resultat: ResultatBeregningCore,
  val grunnlagReferanseListe: List<String>
)

data class ResultatBeregningCore(
  val belop: BigDecimal,
  val kode: String,
  val regel: String
)
