package no.nav.bidrag.beregn.forskudd.core

import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnetForskuddResultatCore
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode

fun interface ForskuddCore {
    fun beregnForskudd(grunnlag: BeregnForskuddGrunnlagCore): BeregnetForskuddResultatCore

    companion object {
        fun getInstance(): ForskuddCore {
            return ForskuddCoreImpl(ForskuddPeriode.getInstance())
        }
    }
}
