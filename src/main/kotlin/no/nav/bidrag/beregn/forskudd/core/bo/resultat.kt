package no.nav.bidrag.beregn.forskudd.core.bo

import java.math.BigDecimal

data class ResultatBeregning(
        val resultatBelop: BigDecimal,
        val resultatKode: ResultatKode,
        val resultatBeskrivelse: String
) {
    // Sjekker om 2 tilgrensende perioder kan merges fordi resultatet er det samme
    fun kanMergesMed(periodeResultatForrige: ResultatBeregning): Boolean {
        return resultatBeskrivelse == periodeResultatForrige.resultatBeskrivelse &&
                resultatKode == periodeResultatForrige.resultatKode &&
                resultatBelop == periodeResultatForrige.resultatBelop
    }
}
