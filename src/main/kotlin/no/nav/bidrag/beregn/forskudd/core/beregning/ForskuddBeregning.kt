package no.nav.bidrag.beregn.forskudd.core.beregning

import no.nav.bidrag.beregn.forskudd.core.ForskuddCore
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatBeregning


interface ForskuddBeregning {

    fun beregn(grunnlag: GrunnlagBeregning): ResultatBeregning

    companion object {
        fun getInstance(): ForskuddBeregning {
            return ForskuddBeregningImpl()
        }
    }
}