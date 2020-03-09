package no.nav.bidrag.beregn.forskudd.core;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.BostedStatusKode;
import no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.SivilstandKode;
import no.nav.bidrag.beregn.forskudd.core.dto.BidragMottakerInntektPeriodeDto;
import no.nav.bidrag.beregn.forskudd.core.dto.BidragMottakerSivilstandPeriodeDto;
import no.nav.bidrag.beregn.forskudd.core.dto.BostatusPeriodeDto;
import no.nav.bidrag.beregn.forskudd.core.dto.ForskuddBeregningResultatDto;
import no.nav.bidrag.beregn.forskudd.core.dto.ForskuddPeriodeGrunnlagDto;
import no.nav.bidrag.beregn.forskudd.core.dto.ForskuddPeriodeResultatDto;
import no.nav.bidrag.beregn.forskudd.core.dto.PeriodeDto;
import no.nav.bidrag.beregn.forskudd.core.dto.PeriodeResultatDto;
import no.nav.bidrag.beregn.forskudd.core.dto.SoknadBarnDto;
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode;
import no.nav.bidrag.beregn.forskudd.core.periode.grunnlag.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.periode.grunnlag.ForskuddPeriodeGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.periode.grunnlag.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.periode.grunnlag.Periode;
import no.nav.bidrag.beregn.forskudd.core.periode.grunnlag.SivilstandPeriode;
import no.nav.bidrag.beregn.forskudd.core.periode.grunnlag.SoknadBarn;
import no.nav.bidrag.beregn.forskudd.core.periode.resultat.ForskuddPeriodeResultat;
import no.nav.bidrag.beregn.forskudd.core.periode.resultat.PeriodeResultat;

public class ForskuddCoreImpl implements ForskuddCore {

  private ForskuddPeriode forskuddPeriode = ForskuddPeriode.getInstance();

  public ForskuddPeriodeResultatDto beregnForskudd(ForskuddPeriodeGrunnlagDto grunnlag) {
    var forskuddPeriodeGrunnlag = mapTilCore(grunnlag);
    var forskuddPeriodeResultat = forskuddPeriode.beregnPerioder(forskuddPeriodeGrunnlag);
    return mapFraCore(forskuddPeriodeResultat);
  }

  private ForskuddPeriodeGrunnlag mapTilCore(ForskuddPeriodeGrunnlagDto grunnlag) {
    var fPG = new ForskuddPeriodeGrunnlag();
    fPG.setBeregnDatoFra(grunnlag.getBeregnDatoFra());
    fPG.setBeregnDatoTil(grunnlag.getBeregnDatoTil());
    fPG.setSoknadBarn(mapSoknadBarnTilCore(grunnlag.getSoknadBarn()));
    fPG.setBidragMottakerInntektPeriodeListe(mapInntektPeriodeListe(grunnlag.getBidragMottakerInntektPeriodeListe()));
    fPG.setBidragMottakerSivilstandPeriodeListe(mapSivilstandPeriodeListe(grunnlag.getBidragMottakerSivilstandPeriodeListe()));
    fPG.setBidragMottakerBarnPeriodeListe(mapBarnPeriodeListe(grunnlag.getBidragMottakerBarnPeriodeListe()));
    return fPG;
  }

  private SoknadBarn mapSoknadBarnTilCore(SoknadBarnDto soknadBarnDto) {
    SoknadBarn sB = new SoknadBarn();
    sB.setFodselDato(soknadBarnDto.getSoknadBarnFodselsdato());
    sB.setSoknadBarnBostatusPeriodeListe(mapBostatusPeriodeListeTilCore(soknadBarnDto.getBostatusPeriode()));
    return sB;
  }

  private List<BostatusPeriode> mapBostatusPeriodeListeTilCore(List<BostatusPeriodeDto> bostatusPeriodeListeDto) {
    List<BostatusPeriode> lBPL = new ArrayList<>();
    for (BostatusPeriodeDto bostatusPeriodeDto : bostatusPeriodeListeDto) {
      lBPL.add(new BostatusPeriode(new Periode(bostatusPeriodeDto.getDatoFraTil().getDatoFra(), bostatusPeriodeDto.getDatoFraTil().getDatoTil()),
          BostedStatusKode.valueOf(bostatusPeriodeDto.getBostedStatusKode())));
    }
    return lBPL;
  }

  private List<InntektPeriode> mapInntektPeriodeListe(List<BidragMottakerInntektPeriodeDto> bidragMottakerInntektPeriodeListeDto) {
    List<InntektPeriode> iPL = new ArrayList<>();
    for (BidragMottakerInntektPeriodeDto mottakerInntektPeriodeDto : bidragMottakerInntektPeriodeListeDto) {
      iPL.add(new InntektPeriode(new Periode(mottakerInntektPeriodeDto.getDatoFraTil().getDatoFra(), mottakerInntektPeriodeDto.getDatoFraTil().getDatoTil()),
          mottakerInntektPeriodeDto.getBelop()));
    }
    return iPL;
  }

  private List<SivilstandPeriode> mapSivilstandPeriodeListe(List<BidragMottakerSivilstandPeriodeDto> bidragMottakerSivilstandPeriodeListeDto) {
    List<SivilstandPeriode> sPL = new ArrayList<>();
    for (BidragMottakerSivilstandPeriodeDto mottakerSivilstandPeriodeDto : bidragMottakerSivilstandPeriodeListeDto) {
      sPL.add(new SivilstandPeriode(new Periode(mottakerSivilstandPeriodeDto.getDatoFraTil().getDatoFra(), mottakerSivilstandPeriodeDto.getDatoFraTil().getDatoTil()),
          SivilstandKode.valueOf(mottakerSivilstandPeriodeDto.getSivilstandKode())));
    }
    return sPL;
  }

  private List<Periode> mapBarnPeriodeListe(List<PeriodeDto> bidragMottakerBarnPeriodeListeDto) {
    List<Periode> bPL = new ArrayList<>();
    for (PeriodeDto mottakerBarnPeriodeDto : bidragMottakerBarnPeriodeListeDto) {
      bPL.add(new Periode(mottakerBarnPeriodeDto.getDatoFra(), mottakerBarnPeriodeDto.getDatoTil()));
    }
    return bPL;
  }

  private ForskuddPeriodeResultatDto mapFraCore(ForskuddPeriodeResultat resultat) {
    var fPRDto = new ForskuddPeriodeResultatDto();
    fPRDto.setPeriodeResultatListe(mapPeriodeResultat(resultat.getPeriodeResultatListe()));
    return fPRDto;
  }

  private List<PeriodeResultatDto> mapPeriodeResultat(List<PeriodeResultat> periodeResultatListe) {
    List<PeriodeResultatDto> periodeResultatDtoListe = new ArrayList<>();
    for (PeriodeResultat periodeResultat : periodeResultatListe) {
      var pRBR = periodeResultat.getForskuddBeregningResultat();
      periodeResultatDtoListe.add(new PeriodeResultatDto(new PeriodeDto(periodeResultat.getDatoFraTil().getDatoFra(), periodeResultat.getDatoFraTil().getDatoTil()),
          new ForskuddBeregningResultatDto(pRBR.getBelop(), pRBR.getResultatKode().toString(), pRBR.getResultatBeskrivelse())));
    }
    return periodeResultatDtoListe;
  }
}