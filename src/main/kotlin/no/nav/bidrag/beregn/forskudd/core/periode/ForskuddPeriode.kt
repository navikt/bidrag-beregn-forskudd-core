package no.nav.bidrag.beregn.forskudd.core.periode

import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.forskudd.core.beregning.ForskuddBeregning
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat

interface ForskuddPeriode {
    fun beregnPerioder(grunnlag: BeregnForskuddGrunnlag): BeregnForskuddResultat
    fun validerInput(grunnlag: BeregnForskuddGrunnlag): List<Avvik>

    companion object {
        fun getInstance(): ForskuddPeriode {
            return ForskuddPeriodeImpl(ForskuddBeregning.getInstance())
        }
    }
}
