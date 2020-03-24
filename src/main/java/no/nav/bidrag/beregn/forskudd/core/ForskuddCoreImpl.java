package no.nav.bidrag.beregn.forskudd.core;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusKode;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandKode;
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SjablonPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SoknadBarn;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddResultatCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BostatusPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.PeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SivilstandPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SoknadBarnCore;
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode;
import no.nav.bidrag.beregn.forskudd.core.periode.Periode;

public class ForskuddCoreImpl implements ForskuddCore {

  private ForskuddPeriode forskuddPeriode = ForskuddPeriode.getInstance();

  public BeregnForskuddResultatCore beregnForskudd(BeregnForskuddGrunnlagCore grunnlag) {
    var beregnForskuddGrunnlag = mapTilBusinessObject(grunnlag);
    var forskuddPeriodeResultat = forskuddPeriode.beregnPerioder(beregnForskuddGrunnlag);
    return mapFraBusinessObject(forskuddPeriodeResultat);
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
    return bidragMottakerBostatusPeriodeListe;
  }

  private List<InntektPeriode> mapBidragMottakerInntektPeriodeListe(List<InntektPeriodeCore> bidragMottakerInntektPeriodeListeCore) {
    var bidragMottakerInntektPeriodeListe = new ArrayList<InntektPeriode>();
    for (InntektPeriodeCore bidragMottakerInntektPeriodeCore : bidragMottakerInntektPeriodeListeCore) {
      bidragMottakerInntektPeriodeListe.add(new InntektPeriode(
          new Periode(bidragMottakerInntektPeriodeCore.getInntektDatoFraTil().getPeriodeDatoFra(),
              bidragMottakerInntektPeriodeCore.getInntektDatoFraTil().getPeriodeDatoTil()),
          bidragMottakerInntektPeriodeCore.getInntektBelop()));
    }
    return bidragMottakerInntektPeriodeListe;
  }

  private List<SivilstandPeriode> mapBidragMottakerSivilstandPeriodeListe(List<SivilstandPeriodeCore> bidragMottakerSivilstandPeriodeListeCore) {
    var bidragMottakerSivilstandPeriodeListe = new ArrayList<SivilstandPeriode>();
    for (SivilstandPeriodeCore bidragMottakerSivilstandPeriodeCore : bidragMottakerSivilstandPeriodeListeCore) {
      bidragMottakerSivilstandPeriodeListe.add(new SivilstandPeriode(
          new Periode(bidragMottakerSivilstandPeriodeCore.getSivilstandDatoFraTil().getPeriodeDatoFra(),
              bidragMottakerSivilstandPeriodeCore.getSivilstandDatoFraTil().getPeriodeDatoTil()),
          SivilstandKode.valueOf(bidragMottakerSivilstandPeriodeCore.getSivilstandKode())));
    }
    return bidragMottakerSivilstandPeriodeListe;
  }

  private List<Periode> mapBidragMottakerBarnPeriodeListe(List<PeriodeCore> bidragMottakerBarnPeriodeListeCore) {
    var bidragMottakerBarnPeriodeListe = new ArrayList<Periode>();
    for (PeriodeCore bidragMottakerBarnPeriodeCore : bidragMottakerBarnPeriodeListeCore) {
      bidragMottakerBarnPeriodeListe
          .add(new Periode(bidragMottakerBarnPeriodeCore.getPeriodeDatoFra(), bidragMottakerBarnPeriodeCore.getPeriodeDatoTil()));
    }
    return bidragMottakerBarnPeriodeListe;
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

  private BeregnForskuddResultatCore mapFraBusinessObject(BeregnForskuddResultat resultat) {
    return new BeregnForskuddResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()));
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> periodeResultatListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode periodeResultat : periodeResultatListe) {
      var forskuddBeregningResultat = periodeResultat.getResultatBeregning();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(periodeResultat.getResultatDatoFraTil().getDatoFra(), periodeResultat.getResultatDatoFraTil().getDatoTil()),
          new ResultatBeregningCore(forskuddBeregningResultat.getResultatBelop(), forskuddBeregningResultat.getResultatKode().toString(),
              forskuddBeregningResultat.getResultatBeskrivelse())));
    }
    return resultatPeriodeCoreListe;
  }
}