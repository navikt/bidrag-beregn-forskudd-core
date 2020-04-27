package no.nav.bidrag.beregn.forskudd.core;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.SivilstandKode;
import no.nav.bidrag.beregn.forskudd.core.bo.Avvik;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.Inntekt;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektType;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SjablonPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SoknadBarn;
import no.nav.bidrag.beregn.forskudd.core.dto.AvvikCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddResultatCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BostatusPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.InntektCore;
import no.nav.bidrag.beregn.forskudd.core.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.PeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SivilstandPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SoknadBarnCore;
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode;

public class ForskuddCoreImpl implements ForskuddCore {

  private final ForskuddPeriode forskuddPeriode;

  ForskuddCoreImpl(ForskuddPeriode forskuddPeriode) {
    this.forskuddPeriode = forskuddPeriode;
  }

  public BeregnForskuddResultatCore beregnForskudd(BeregnForskuddGrunnlagCore grunnlag) {
    var beregnForskuddGrunnlag = mapTilBusinessObject(grunnlag);
    var beregnForskuddResultat = new BeregnForskuddResultat(Collections.emptyList());
    var avvikListe = forskuddPeriode.validerInput(beregnForskuddGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnForskuddResultat = forskuddPeriode.beregnPerioder(beregnForskuddGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnForskuddResultat);
  }

  private BeregnForskuddGrunnlag mapTilBusinessObject(BeregnForskuddGrunnlagCore grunnlag) {
    var beregnDatoFra = grunnlag.getBeregnDatoFra();
    var beregnDatoTil = grunnlag.getBeregnDatoTil();
    var soknadBarn = mapSoknadBarn(grunnlag.getSoknadBarn());
    var bMInntektPeriodeListe = mapBidragMottakerInntektPeriodeListe(grunnlag.getBidragMottakerInntektPeriodeListe());
    var bMSivilstandPeriodeListe = mapBidragMottakerSivilstandPeriodeListe(grunnlag.getBidragMottakerSivilstandPeriodeListe());
    var bMBarnPeriodeListe = mapBidragMottakerBarnPeriodeListe(grunnlag.getBidragMottakerBarnPeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(grunnlag.getSjablonPeriodeListe());
    return new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadBarn, bMInntektPeriodeListe, bMSivilstandPeriodeListe, bMBarnPeriodeListe,
        sjablonPeriodeListe);
  }

  private SoknadBarn mapSoknadBarn(SoknadBarnCore soknadBarnCore) {
    var sBFodselsdato = soknadBarnCore.getSoknadBarnFodselsdato();
    var sBBostatusPeriodeListe = mapSoknadBarnBostatusPeriodeListe(soknadBarnCore.getSoknadBarnBostatusPeriodeListe());
    return new SoknadBarn(sBFodselsdato, sBBostatusPeriodeListe);
  }

  private List<BostatusPeriode> mapSoknadBarnBostatusPeriodeListe(List<BostatusPeriodeCore> bidragMottakerBostatusPeriodeListeCore) {
    var bidragMottakerBostatusPeriodeListe = new ArrayList<BostatusPeriode>();
    for (BostatusPeriodeCore bidragMottakerBostatusPeriodeCore : bidragMottakerBostatusPeriodeListeCore) {
      bidragMottakerBostatusPeriodeListe.add(new BostatusPeriode(
          new Periode(bidragMottakerBostatusPeriodeCore.getBostatusDatoFraTil().getPeriodeDatoFra(),
              bidragMottakerBostatusPeriodeCore.getBostatusDatoFraTil().getPeriodeDatoTil()),
          BostatusKode.valueOf(bidragMottakerBostatusPeriodeCore.getBostatusKode())));
    }
    return bidragMottakerBostatusPeriodeListe.stream()
        .sorted(comparing(bostatusPeriode -> bostatusPeriode.getBostatusDatoFraTil().getDatoFra())).collect(toList());
  }

  private List<InntektPeriode> mapBidragMottakerInntektPeriodeListe(List<InntektPeriodeCore> bidragMottakerInntektPeriodeListeCore) {
    var bidragMottakerInntektPeriodeListe = new ArrayList<InntektPeriode>();
    for (InntektPeriodeCore bidragMottakerInntektPeriodeCore : bidragMottakerInntektPeriodeListeCore) {
      bidragMottakerInntektPeriodeListe.add(new InntektPeriode(
          new Periode(bidragMottakerInntektPeriodeCore.getInntektDatoFraTil().getPeriodeDatoFra(),
              bidragMottakerInntektPeriodeCore.getInntektDatoFraTil().getPeriodeDatoTil()),
          InntektType.valueOf(bidragMottakerInntektPeriodeCore.getInntektType()),
          bidragMottakerInntektPeriodeCore.getInntektBelop()));
    }
    return bidragMottakerInntektPeriodeListe.stream()
        .sorted(comparing(inntektPeriode -> inntektPeriode.getInntektDatoFraTil().getDatoFra())).collect(toList());
  }

  private List<SivilstandPeriode> mapBidragMottakerSivilstandPeriodeListe(List<SivilstandPeriodeCore> bidragMottakerSivilstandPeriodeListeCore) {
    var bidragMottakerSivilstandPeriodeListe = new ArrayList<SivilstandPeriode>();
    for (SivilstandPeriodeCore bidragMottakerSivilstandPeriodeCore : bidragMottakerSivilstandPeriodeListeCore) {
      bidragMottakerSivilstandPeriodeListe.add(new SivilstandPeriode(
          new Periode(bidragMottakerSivilstandPeriodeCore.getSivilstandDatoFraTil().getPeriodeDatoFra(),
              bidragMottakerSivilstandPeriodeCore.getSivilstandDatoFraTil().getPeriodeDatoTil()),
          SivilstandKode.valueOf(bidragMottakerSivilstandPeriodeCore.getSivilstandKode())));
    }
    return bidragMottakerSivilstandPeriodeListe.stream()
        .sorted(comparing(sivilstandPeriode -> sivilstandPeriode.getSivilstandDatoFraTil().getDatoFra())).collect(toList());
  }

  private List<Periode> mapBidragMottakerBarnPeriodeListe(List<PeriodeCore> bidragMottakerBarnPeriodeListeCore) {
    var bidragMottakerBarnPeriodeListe = new ArrayList<Periode>();
    for (PeriodeCore bidragMottakerBarnPeriodeCore : bidragMottakerBarnPeriodeListeCore) {
      bidragMottakerBarnPeriodeListe
          .add(new Periode(bidragMottakerBarnPeriodeCore.getPeriodeDatoFra(), bidragMottakerBarnPeriodeCore.getPeriodeDatoTil()));
    }
    return bidragMottakerBarnPeriodeListe.stream()
        .sorted(comparing(barnPeriode -> barnPeriode.getDatoFraTil().getDatoFra())).collect(toList());
  }

  private List<SjablonPeriode> mapSjablonPeriodeListe(List<SjablonPeriodeCore> sjablonPeriodeListeCore) {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    for (SjablonPeriodeCore sjablonPeriodeCore : sjablonPeriodeListeCore) {
      sjablonPeriodeListe.add(new SjablonPeriode(
          new Periode(sjablonPeriodeCore.getSjablonDatoFraTil().getPeriodeDatoFra(), sjablonPeriodeCore.getSjablonDatoFraTil().getPeriodeDatoTil()),
          sjablonPeriodeCore.getSjablonType(), sjablonPeriodeCore.getSjablonVerdi().intValue()));
    }
    return sjablonPeriodeListe;
  }

  private BeregnForskuddResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnForskuddResultat resultat) {
    return new BeregnForskuddResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
  }

  private List<AvvikCore> mapAvvik(List<Avvik> avvikListe) {
    var avvikCoreListe = new ArrayList<AvvikCore>();
    for (Avvik avvik : avvikListe) {
      avvikCoreListe.add(new AvvikCore(avvik.getAvvikTekst(), avvik.getAvvikType().toString()));
    }
    return avvikCoreListe;
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> periodeResultatListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode periodeResultat : periodeResultatListe) {
      var forskuddBeregningResultat = periodeResultat.getResultatBeregning();
      var forskuddResultatGrunnlag = periodeResultat.getResultatGrunnlag();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(periodeResultat.getResultatDatoFraTil().getDatoFra(), periodeResultat.getResultatDatoFraTil().getDatoTil()),
          new ResultatBeregningCore(forskuddBeregningResultat.getResultatBelop(), forskuddBeregningResultat.getResultatKode().toString(),
              forskuddBeregningResultat.getResultatBeskrivelse()),
          new ResultatGrunnlagCore(mapResultatGrunnlagInntekt(forskuddResultatGrunnlag.getBidragMottakerInntektListe()),
              forskuddResultatGrunnlag.getBidragMottakerSivilstandKode().toString(), forskuddResultatGrunnlag.getAntallBarnIHusstand(),
              forskuddResultatGrunnlag.getSoknadBarnAlder(), forskuddResultatGrunnlag.getSoknadBarnBostatusKode().toString(),
              forskuddResultatGrunnlag.getForskuddssats100Prosent(), forskuddResultatGrunnlag.getMultiplikatorMaksInntektsgrense(),
              forskuddResultatGrunnlag.getInntektsgrense100ProsentForskudd(), forskuddResultatGrunnlag.getInntektsgrenseEnslig75ProsentForskudd(),
              forskuddResultatGrunnlag.getInntektsgrenseGift75ProsentForskudd(), forskuddResultatGrunnlag.getInntektsintervallForskudd())));
    }
    return resultatPeriodeCoreListe;
  }

  private List<InntektCore> mapResultatGrunnlagInntekt(List<Inntekt> resultatGrunnlagInntektListe) {
    var resultatGrunnlagInntektListeCore = new ArrayList<InntektCore>();
    for (Inntekt resultatGrunnlagInntekt : resultatGrunnlagInntektListe) {
      resultatGrunnlagInntektListeCore
          .add(new InntektCore(resultatGrunnlagInntekt.getInntektType().toString(), resultatGrunnlagInntekt.getInntektBelop()));
    }
    return resultatGrunnlagInntektListeCore;
  }
}