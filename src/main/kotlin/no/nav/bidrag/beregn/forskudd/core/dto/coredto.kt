package no.nav.bidrag.beregn.forskudd.core.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonNavnVerdiCore
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
    val bidragMottakerBarnPeriodeListe: List<PeriodeCore>,
    var sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class SoknadBarnCore(
    val soknadBarnFodselsdato: LocalDate,
    val soknadBarnBostatusPeriodeListe: List<BostatusPeriodeCore>
)

data class BostatusPeriodeCore(
    val bostatusDatoFraTil: PeriodeCore,
    val bostatusKode: String
)

data class InntektPeriodeCore(
    val inntektDatoFraTil: PeriodeCore,
    val inntektType: String,
    val inntektBelop: BigDecimal
)

data class SivilstandPeriodeCore(
    val sivilstandDatoFraTil: PeriodeCore,
    val sivilstandKode: String
)


// Resultat
data class BeregnForskuddResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregning: ResultatBeregningCore,
    val resultatGrunnlag: ResultatGrunnlagCore)

data class ResultatBeregningCore(
    val resultatBelop: BigDecimal,
    val resultatKode: String,
    val resultatBeskrivelse: String
)

data class ResultatGrunnlagCore(
    val bidragMottakerInntektListe: List<InntektCore>,
    val bidragMottakerSivilstandKode: String,
    val antallBarnIHusstand: Int,
    val soknadBarnAlder: Int,
    val soknadBarnBostatusKode: String,
    val sjablonListe: List<SjablonNavnVerdiCore>
)

data class InntektCore(
    val inntektType: String,
    val inntektBelop: BigDecimal
)
