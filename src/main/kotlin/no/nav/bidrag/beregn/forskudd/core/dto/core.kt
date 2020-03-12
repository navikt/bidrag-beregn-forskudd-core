package no.nav.bidrag.beregn.forskudd.core.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.util.Collections.emptyList

// Grunnlag
data class ForskuddPeriodeGrunnlagDto(
    var beregnDatoFra: LocalDate? = null,
    var beregnDatoTil: LocalDate? = null,
    var soknadBarn: SoknadBarnDto? = null,
    var bidragMottakerInntektPeriodeListe: List<BidragMottakerInntektPeriodeDto> = emptyList(),
    var bidragMottakerSivilstandPeriodeListe: List<BidragMottakerSivilstandPeriodeDto> = emptyList(),
    var bidragMottakerBarnPeriodeListe: List<PeriodeDto?> = emptyList(),
    var sjablontallListe: List<SjablontallDto> = emptyList()
)

data class SoknadBarnDto(
    var soknadBarnFodselsdato: LocalDate? = null,
    var bostatusPeriode: List<BostatusPeriodeDto?> = emptyList()
)

data class BostatusPeriodeDto(
    var datoFraTil: PeriodeDto?,
    var bostedStatusKode: String? = null
)

data class BidragMottakerInntektPeriodeDto(
    var datoFraTil: PeriodeDto?,
    var belop: BigDecimal? = null
)

data class BidragMottakerSivilstandPeriodeDto(
    var datoFraTil: PeriodeDto?,
    var sivilstandKode: String? = null
)

data class SjablontallDto(
    var typeSjablon: String,
    var datoFraTil: PeriodeDto,
    var verdi: BigDecimal
)


// Resultat
data class ForskuddPeriodeResultatDto(
    var periodeResultatListe: List<PeriodeResultatDto> = emptyList()
)

data class PeriodeResultatDto(
    var datoFraTil: PeriodeDto?,
    var forskuddBeregningResultat: ForskuddBeregningResultatDto
)

data class PeriodeDto(
    var datoFra: LocalDate? = null,
    var datoTil: LocalDate? = null
)

data class ForskuddBeregningResultatDto(
    var belop: BigDecimal? = null,
    var resultatKode: String? = null,
    var resultatBeskrivelse: String? = null
)