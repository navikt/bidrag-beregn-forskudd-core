package no.nav.bidrag.beregn.forskudd.core.beregning

import no.nav.bidrag.beregn.felles.SjablonUtil
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import no.nav.bidrag.beregn.felles.enums.BostatusKode
import no.nav.bidrag.beregn.felles.enums.SivilstandKode
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.forskudd.core.bo.Inntekt
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatBeregning
import no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

open class ForskuddBeregning {

    fun beregn(grunnlag: GrunnlagBeregning): ResultatBeregning {

        val sjablonverdier = hentSjablonVerdier(grunnlag.sjablonListe)

        val maksInntektsgrense = sjablonverdier.forskuddssats100ProsentBelop.multiply(sjablonverdier.maksInntektForskuddMottakerMultiplikator)
        //Inntektsintervall regnes ut med antall barn utover ett
        var inntektsIntervallTotal = sjablonverdier.inntektsintervallForskuddBelop.multiply(BigDecimal.valueOf(grunnlag.barnIHusstanden.antall - 1))
        if (inntektsIntervallTotal.compareTo(BigDecimal.ZERO) < 0) {
            inntektsIntervallTotal = BigDecimal.ZERO
        }

        val resultatKode: ResultatKode
        val regel: String

        // Legger sammen inntektene
        val bidragMottakerInntekt = grunnlag.inntektListe.stream().map(Inntekt::belop)
            .reduce(BigDecimal.ZERO, BigDecimal::add)

        // Søknadsbarn er over 18 år (REGEL 1)
        if (grunnlag.soknadBarnAlder.alder >= 18) {
            resultatKode = ResultatKode.AVSLAG
            regel = "REGEL 1"

            // Søknadsbarn bor alene eller ikke med foreldre (REGEL 2/3)
        } else if (grunnlag.soknadBarnBostatus.kode != BostatusKode.BOR_MED_FORELDRE) {
            resultatKode =
                if (grunnlag.soknadBarnAlder.alder >= 11) ResultatKode.FORHOYET_FORSKUDD_11_AAR_125_PROSENT else ResultatKode.FORHOYET_FORSKUDD_100_PROSENT
            regel = if (resultatKode == ResultatKode.FORHOYET_FORSKUDD_11_AAR_125_PROSENT) "REGEL 2" else "REGEL 3"

            // Over maks inntektsgrense for forskudd (REGEL 4)
        } else if (!erUnderInntektsGrense(maksInntektsgrense, bidragMottakerInntekt)) {
            resultatKode = ResultatKode.AVSLAG
            regel = "REGEL 4"

            // Under maks inntektsgrense for fullt forskudd (REGEL 5/6)
        } else if (erUnderInntektsGrense(sjablonverdier.inntektsgrense100ProsentForskuddBelop, bidragMottakerInntekt)) {
            resultatKode =
                if (grunnlag.soknadBarnAlder.alder >= 11) ResultatKode.FORHOYET_FORSKUDD_11_AAR_125_PROSENT else ResultatKode.FORHOYET_FORSKUDD_100_PROSENT
            regel = if (resultatKode == ResultatKode.FORHOYET_FORSKUDD_11_AAR_125_PROSENT) "REGEL 5" else "REGEL 6"

            // Resterende regler (gift/enslig) (REGEL 7/8/9/10/11/12/13/14)
        } else {
            resultatKode = if (erUnderInntektsGrense(
                    finnInntektsgrense(
                        grunnlag.sivilstand.kode, sjablonverdier.inntektsgrenseEnslig75ProsentForskuddBelop,
                        sjablonverdier.inntektsgrenseGiftSamboer75ProsentForskuddBelop
                    ).add(inntektsIntervallTotal), bidragMottakerInntekt
                )
            ) ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT else ResultatKode.REDUSERT_FORSKUDD_50_PROSENT

            regel = if (grunnlag.sivilstand.kode == SivilstandKode.ENSLIG) {
                if (grunnlag.barnIHusstanden.antall == 1.0) {
                    if (resultatKode == ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT) "REGEL 7" else "REGEL 8"
                } else {
                    if (resultatKode == ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT) "REGEL 9" else "REGEL 10"
                }
            } else {
                if (grunnlag.barnIHusstanden.antall == 1.0) {
                    if (resultatKode == ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT) "REGEL 11" else "REGEL 12"
                } else {
                    if (resultatKode == ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT) "REGEL 13" else "REGEL 14"
                }
            }
        }
        return ResultatBeregning(
            beregnForskudd(resultatKode, sjablonverdier.forskuddssats75ProsentBelop), resultatKode, regel,
            byggSjablonListe(grunnlag.sjablonListe, sjablonverdier)
        )
    }


    // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
    private fun byggSjablonListe(sjablonPeriodeListe: List<SjablonPeriode>, sjablonverdier: Sjablonverdier): List<SjablonPeriodeNavnVerdi> {
        val sjablonListe = ArrayList<SjablonPeriodeNavnVerdi>()
        sjablonListe.add(
            SjablonPeriodeNavnVerdi(
                hentPeriode(sjablonPeriodeListe, SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP.navn),
                SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP.navn, sjablonverdier.forskuddssats75ProsentBelop
            )
        )
        sjablonListe.add(
            SjablonPeriodeNavnVerdi(
                hentPeriode(sjablonPeriodeListe, SjablonTallNavn.FORSKUDDSSATS_BELOP.navn),
                SjablonTallNavn.FORSKUDDSSATS_BELOP.navn, sjablonverdier.forskuddssats100ProsentBelop
            )
        )
        sjablonListe.add(
            SjablonPeriodeNavnVerdi(
                hentPeriode(
                    sjablonPeriodeListe,
                    SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.navn
                ),
                SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.navn, sjablonverdier.maksInntektForskuddMottakerMultiplikator
            )
        )
        sjablonListe.add(
            SjablonPeriodeNavnVerdi(
                hentPeriode(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.navn),
                SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.navn, sjablonverdier.inntektsgrense100ProsentForskuddBelop
            )
        )
        sjablonListe.add(
            SjablonPeriodeNavnVerdi(
                hentPeriode(
                    sjablonPeriodeListe,
                    SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.navn
                ),
                SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.navn, sjablonverdier.inntektsgrenseEnslig75ProsentForskuddBelop
            )
        )
        sjablonListe.add(
            SjablonPeriodeNavnVerdi(
                hentPeriode(
                    sjablonPeriodeListe,
                    SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.navn
                ),
                SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.navn, sjablonverdier.inntektsgrenseGiftSamboer75ProsentForskuddBelop
            )
        )
        sjablonListe.add(
            SjablonPeriodeNavnVerdi(
                hentPeriode(sjablonPeriodeListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.navn),
                SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.navn, sjablonverdier.inntektsintervallForskuddBelop
            )
        )
        return sjablonListe
    }

    private fun hentPeriode(sjablonPeriodeListe: List<SjablonPeriode>, sjablonNavn: String): Periode {
        return sjablonPeriodeListe.stream()
            .filter { it.sjablon.navn == sjablonNavn }
            .map(SjablonPeriode::getPeriode)
            .findFirst()
            .orElse(Periode(LocalDate.MIN, LocalDate.MAX))
    }

    companion object {
        // Beregner forskuddsbeløp basert på resultatkode
        // Forskudd 50%  = Sjablon 0038 * 2/3
        // Forskudd 75%  = Sjablon 0038
        // Forskudd 100% = Sjablon 0038 * 4/3
        // Forskudd 125% = Sjablon 0038 * 5/3
        private fun beregnForskudd(resultatKode: ResultatKode, forskuddssats75ProsentBelop: BigDecimal): BigDecimal {
            return when (resultatKode) {
                ResultatKode.REDUSERT_FORSKUDD_50_PROSENT -> forskuddssats75ProsentBelop.multiply(BigDecimal.valueOf(2))
                    .divide(BigDecimal.valueOf(3), -1, RoundingMode.HALF_UP)

                ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT -> forskuddssats75ProsentBelop
                ResultatKode.FORHOYET_FORSKUDD_100_PROSENT -> forskuddssats75ProsentBelop.multiply(BigDecimal.valueOf(4))
                    .divide(BigDecimal.valueOf(3), -1, RoundingMode.HALF_UP)

                ResultatKode.FORHOYET_FORSKUDD_11_AAR_125_PROSENT -> forskuddssats75ProsentBelop.multiply(BigDecimal.valueOf(5))
                    .divide(BigDecimal.valueOf(3), -1, RoundingMode.HALF_UP)

                else -> BigDecimal.ZERO
            }
        }

        private fun erUnderInntektsGrense(inntektsgrense: BigDecimal, inntekt: BigDecimal): Boolean {
            return inntekt.compareTo(inntektsgrense) < 1
        }

        private fun finnInntektsgrense(
            sivilstandKode: SivilstandKode, inntektsgrenseEnslig75Prosent: BigDecimal, inntektsgrenseGift75Prosent: BigDecimal
        ): BigDecimal {
            return if (sivilstandKode == SivilstandKode.ENSLIG) inntektsgrenseEnslig75Prosent else inntektsgrenseGift75Prosent
        }

        // Henter sjablonverdier
        private fun hentSjablonVerdier(sjablonPeriodeListe: List<SjablonPeriode>): Sjablonverdier {
            val sjablonListe = sjablonPeriodeListe.stream()
                .map(SjablonPeriode::sjablon)
                .toList()
            val forskuddssats75ProsentBelop = SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP)
            val forskuddssats100ProsentBelop = SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.FORSKUDDSSATS_BELOP)
            val maksInntektForskuddMottakerMultiplikator =
                SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR)
            val inntektsgrense100ProsentForskuddBelop =
                SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP)
            val inntektsgrenseEnslig75ProsentForskuddBelop =
                SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP)
            val inntektsgrenseGiftSamboer75ProsentForskuddBelop =
                SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP)
            val inntektsintervallForskuddBelop = SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP)
            return Sjablonverdier(
                forskuddssats75ProsentBelop = forskuddssats75ProsentBelop,
                forskuddssats100ProsentBelop = forskuddssats100ProsentBelop,
                maksInntektForskuddMottakerMultiplikator = maksInntektForskuddMottakerMultiplikator,
                inntektsgrense100ProsentForskuddBelop = inntektsgrense100ProsentForskuddBelop,
                inntektsgrenseEnslig75ProsentForskuddBelop = inntektsgrenseEnslig75ProsentForskuddBelop,
                inntektsgrenseGiftSamboer75ProsentForskuddBelop = inntektsgrenseGiftSamboer75ProsentForskuddBelop,
                inntektsintervallForskuddBelop = inntektsintervallForskuddBelop
            )
        }
    }
}

data class Sjablonverdier(
    var maksInntektForskuddMottakerMultiplikator: BigDecimal = BigDecimal.ZERO,
    var inntektsintervallForskuddBelop: BigDecimal = BigDecimal.ZERO,
    var forskuddssats75ProsentBelop: BigDecimal = BigDecimal.ZERO,
    var forskuddssats100ProsentBelop: BigDecimal = BigDecimal.ZERO,
    var inntektsgrense100ProsentForskuddBelop: BigDecimal = BigDecimal.ZERO,
    var inntektsgrenseEnslig75ProsentForskuddBelop: BigDecimal = BigDecimal.ZERO,
    var inntektsgrenseGiftSamboer75ProsentForskuddBelop: BigDecimal = BigDecimal.ZERO
)
