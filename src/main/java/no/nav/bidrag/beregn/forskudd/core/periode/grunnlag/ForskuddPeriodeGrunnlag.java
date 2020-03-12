package no.nav.bidrag.beregn.forskudd.core.periode.grunnlag;

import java.time.LocalDate;
import java.util.List;

public class ForskuddPeriodeGrunnlag {

  public ForskuddPeriodeGrunnlag() {
  }

  private LocalDate beregnDatoFra;
  private LocalDate beregnDatoTil;
  private SoknadBarn soknadBarn;
  private List<InntektPeriode> bidragMottakerInntektPeriodeListe;
  private List<SivilstandPeriode> bidragMottakerSivilstandPeriodeListe;
  private List<Periode> bidragMottakerBarnPeriodeListe;
  private List<SjablonPeriode> sjablonPeriodeListe;

  public LocalDate getBeregnDatoFra() {
    return beregnDatoFra;
  }

  public void setBeregnDatoFra(LocalDate beregnDatoFra) {
    this.beregnDatoFra = beregnDatoFra;
  }

  public LocalDate getBeregnDatoTil() {
    return beregnDatoTil;
  }

  public void setBeregnDatoTil(LocalDate beregnDatoTil) {
    this.beregnDatoTil = beregnDatoTil;
  }

  public SoknadBarn getSoknadBarn() {
    return soknadBarn;
  }

  public void setSoknadBarn(SoknadBarn soknadBarn) {
    this.soknadBarn = soknadBarn;
  }

  public List<InntektPeriode> getBidragMottakerInntektPeriodeListe() {
    return bidragMottakerInntektPeriodeListe;
  }

  public void setBidragMottakerInntektPeriodeListe(List<InntektPeriode> bidragMottakerInntektPeriodeListe) {
    this.bidragMottakerInntektPeriodeListe = bidragMottakerInntektPeriodeListe;
  }

  public List<SivilstandPeriode> getBidragMottakerSivilstandPeriodeListe() {
    return bidragMottakerSivilstandPeriodeListe;
  }

  public void setBidragMottakerSivilstandPeriodeListe(List<SivilstandPeriode> bidragMottakerSivilstandPeriodeListe) {
    this.bidragMottakerSivilstandPeriodeListe = bidragMottakerSivilstandPeriodeListe;
  }

  public List<Periode> getBidragMottakerBarnPeriodeListe() {
    return bidragMottakerBarnPeriodeListe;
  }

  public void setBidragMottakerBarnPeriodeListe(List<Periode> bidragMottakerBarnPeriodeListe) {
    this.bidragMottakerBarnPeriodeListe = bidragMottakerBarnPeriodeListe;
  }

  public List<SjablonPeriode> getSjablonPeriodeListe() {
    return sjablonPeriodeListe;
  }

  public void setSjablonPeriodeListe(List<SjablonPeriode> sjablonPeriodeListe) {
    this.sjablonPeriodeListe = sjablonPeriodeListe;
  }
}