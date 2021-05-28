package no.nav.bidrag.beregn.forskudd.core;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNokkelCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SivilstandKode;
import no.nav.bidrag.beregn.forskudd.core.bo.BarnPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SoknadBarn;
import no.nav.bidrag.beregn.forskudd.core.dto.BarnPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnetForskuddResultatCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BostatusPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SivilstandPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SoknadBarnCore;
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode;

public class ForskuddCoreImpl implements ForskuddCore {

  private final ForskuddPeriode forskuddPeriode;

  ForskuddCoreImpl(ForskuddPeriode forskuddPeriode) {
    this.forskuddPeriode = forskuddPeriode;
  }

  public BeregnetForskuddResultatCore beregnForskudd(BeregnForskuddGrunnlagCore grunnlag) {
    var beregnForskuddGrunnlag = mapTilBusinessObject(grunnlag);
    BeregnForskuddResultat beregnForskuddResultat;
    var avvikListe = forskuddPeriode.validerInput(beregnForskuddGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnForskuddResultat = forskuddPeriode.beregnPerioder(beregnForskuddGrunnlag);
    } else {
      beregnForskuddResultat = new BeregnForskuddResultat(emptyList());
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
    var sBReferanse = soknadBarnCore.getReferanse();
    var sBFodselsdato = soknadBarnCore.getFodselsdato();
    var sBBostatusPeriodeListe = mapSoknadBarnBostatusPeriodeListe(soknadBarnCore.getBostatusPeriodeListe());
    return new SoknadBarn(sBReferanse, sBFodselsdato, sBBostatusPeriodeListe);
  }

  private List<BostatusPeriode> mapSoknadBarnBostatusPeriodeListe(List<BostatusPeriodeCore> bidragMottakerBostatusPeriodeListeCore) {
    var bidragMottakerBostatusPeriodeListe = new ArrayList<BostatusPeriode>();
    for (BostatusPeriodeCore bidragMottakerBostatusPeriodeCore : bidragMottakerBostatusPeriodeListeCore) {
      bidragMottakerBostatusPeriodeListe.add(new BostatusPeriode(
          bidragMottakerBostatusPeriodeCore.getReferanse(),
          new Periode(bidragMottakerBostatusPeriodeCore.getPeriode().getDatoFom(),
              bidragMottakerBostatusPeriodeCore.getPeriode().getDatoTil()),
          BostatusKode.valueOf(bidragMottakerBostatusPeriodeCore.getKode())));
    }
    return bidragMottakerBostatusPeriodeListe.stream()
        .sorted(comparing(bostatusPeriode -> bostatusPeriode.getPeriode().getDatoFom())).collect(toList());
  }

  private List<InntektPeriode> mapBidragMottakerInntektPeriodeListe(List<InntektPeriodeCore> bidragMottakerInntektPeriodeListeCore) {
    var bidragMottakerInntektPeriodeListe = new ArrayList<InntektPeriode>();
    for (InntektPeriodeCore bidragMottakerInntektPeriodeCore : bidragMottakerInntektPeriodeListeCore) {
      bidragMottakerInntektPeriodeListe.add(new InntektPeriode(
          bidragMottakerInntektPeriodeCore.getReferanse(),
          new Periode(bidragMottakerInntektPeriodeCore.getPeriode().getDatoFom(),
              bidragMottakerInntektPeriodeCore.getPeriode().getDatoTil()),
          InntektType.valueOf(bidragMottakerInntektPeriodeCore.getType()),
          bidragMottakerInntektPeriodeCore.getBelop()));
    }
    return bidragMottakerInntektPeriodeListe.stream()
        .sorted(comparing(inntektPeriode -> inntektPeriode.getPeriode().getDatoFom())).collect(toList());
  }

  private List<SivilstandPeriode> mapBidragMottakerSivilstandPeriodeListe(List<SivilstandPeriodeCore> bidragMottakerSivilstandPeriodeListeCore) {
    var bidragMottakerSivilstandPeriodeListe = new ArrayList<SivilstandPeriode>();
    for (SivilstandPeriodeCore bidragMottakerSivilstandPeriodeCore : bidragMottakerSivilstandPeriodeListeCore) {
      bidragMottakerSivilstandPeriodeListe.add(new SivilstandPeriode(
          bidragMottakerSivilstandPeriodeCore.getReferanse(),
          new Periode(bidragMottakerSivilstandPeriodeCore.getPeriode().getDatoFom(),
              bidragMottakerSivilstandPeriodeCore.getPeriode().getDatoTil()),
          SivilstandKode.valueOf(bidragMottakerSivilstandPeriodeCore.getKode())));
    }
    return bidragMottakerSivilstandPeriodeListe.stream()
        .sorted(comparing(sivilstandPeriode -> sivilstandPeriode.getPeriode().getDatoFom())).collect(toList());
  }

  private List<BarnPeriode> mapBidragMottakerBarnPeriodeListe(List<BarnPeriodeCore> bidragMottakerBarnPeriodeListeCore) {
    var bidragMottakerBarnPeriodeListe = new ArrayList<BarnPeriode>();
    for (BarnPeriodeCore bidragMottakerBarnPeriodeCore : bidragMottakerBarnPeriodeListeCore) {
      bidragMottakerBarnPeriodeListe.add(new BarnPeriode(
          bidragMottakerBarnPeriodeCore.getReferanse(),
          new Periode(bidragMottakerBarnPeriodeCore.getPeriode().getDatoFom(),
              bidragMottakerBarnPeriodeCore.getPeriode().getDatoTil())));
    }
    return bidragMottakerBarnPeriodeListe.stream()
        .sorted(comparing(barnPeriode -> barnPeriode.getPeriode().getDatoFom())).collect(toList());
  }

  private List<SjablonPeriode> mapSjablonPeriodeListe(List<SjablonPeriodeCore> sjablonPeriodeListeCore) {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    for (SjablonPeriodeCore sjablonPeriodeCore : sjablonPeriodeListeCore) {
      var sjablonNokkelListe = new ArrayList<SjablonNokkel>();
      var sjablonInnholdListe = new ArrayList<SjablonInnhold>();
      for (SjablonNokkelCore sjablonNokkelCore : sjablonPeriodeCore.getNokkelListe()) {
        sjablonNokkelListe.add(new SjablonNokkel(sjablonNokkelCore.getNavn(), sjablonNokkelCore.getVerdi()));
      }
      for (SjablonInnholdCore sjablonInnholdCore : sjablonPeriodeCore.getInnholdListe()) {
        sjablonInnholdListe.add(new SjablonInnhold(sjablonInnholdCore.getNavn(), sjablonInnholdCore.getVerdi()));
      }
      sjablonPeriodeListe.add(new SjablonPeriode(
          new Periode(sjablonPeriodeCore.getPeriode().getDatoFom(), sjablonPeriodeCore.getPeriode().getDatoTil()),
          new Sjablon(sjablonPeriodeCore.getNavn(), sjablonNokkelListe, sjablonInnholdListe)));
    }
    return sjablonPeriodeListe;
  }

  private BeregnetForskuddResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnForskuddResultat resultat) {
    return new BeregnetForskuddResultatCore(mapResultatPeriode(resultat.getBeregnetForskuddPeriodeListe()), mapAvvik(avvikListe));
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
      var forskuddBeregningResultat = periodeResultat.getResultat();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(periodeResultat.getPeriode().getDatoFom(), periodeResultat.getPeriode().getDatoTil()),
          new ResultatBeregningCore(forskuddBeregningResultat.getBelop(), forskuddBeregningResultat.getKode().toString(),
              forskuddBeregningResultat.getRegel()),
          mapReferanseListe(periodeResultat.getGrunnlag())));
    }
    return resultatPeriodeCoreListe;
  }

  private List<String> mapReferanseListe(GrunnlagBeregning resultatGrunnlag) {
    var referanseListe = new ArrayList<String>();
    resultatGrunnlag.getBidragMottakerInntektListe().forEach(inntekt -> referanseListe.add(inntekt.getReferanse()));
    referanseListe.add(resultatGrunnlag.getBidragMottakerSivilstand().getReferanse());
    referanseListe.addAll(resultatGrunnlag.getAntallBarnIHusstand().getReferanseListe());
    referanseListe.add(resultatGrunnlag.getSoknadBarnAlder().getReferanse());
    referanseListe.add(resultatGrunnlag.getSoknadBarnBostatus().getReferanse());
    return referanseListe;
  }
}