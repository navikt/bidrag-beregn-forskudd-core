package no.nav.bidrag.beregn.forskudd.core

import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriodeImpl

import no.nav.bidrag.transport.beregning.forskudd.core.request.BeregnForskuddGrunnlagCore

import no.nav.bidrag.transport.beregning.forskudd.core.response.BeregnetForskuddResultatCore


interface ForskuddCore {
    fun beregnForskudd(grunnlag: BeregnForskuddGrunnlagCore): BeregnetForskuddResultatCore

    companion object {
        fun getInstance(): ForskuddCore {
            return ForskuddCoreImpl(ForskuddPeriode.getInstance())
        }
    }
}