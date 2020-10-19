package no.nav.bidrag.beregn.forskudd.core.periode;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.firstDayOfNextMonth;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.forskudd.core.beregning.ForskuddBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.AlderPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.Inntekt;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode;

public class ForskuddPeriodeImpl implements ForskuddPeriode {

  private final ForskuddBeregning forskuddBeregning;

  private List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

  private List<InntektPeriode> justertInntektPeriodeListe;
  private List<SivilstandPeriode> justertSivilstandPeriodeListe;
  private List<Periode> justertBarnPeriodeListe;
  private List<BostatusPeriode> justertBostatusPeriodeListe;
  private List<AlderPeriode> justertAlderPeriodeListe;
  private List<SjablonPeriode> justertSjablonPeriodeListe;
  private List<Periode> bruddPeriodeListe;

  public ForskuddPeriodeImpl(ForskuddBeregning forskuddBeregning) {
    this.forskuddBeregning = forskuddBeregning;
  }

  public BeregnForskuddResultat beregnPerioder(BeregnForskuddGrunnlag periodeGrunnlag) {

    // Juster datoer
    justerDatoerGrunnlagslister(periodeGrunnlag);

    // Lag bruddperioder
    lagBruddperioder(periodeGrunnlag);

    // Foreta beregning
    beregnForskuddPerPeriode();

    return new BeregnForskuddResultat(periodeResultatListe);
  }

  // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)
  private void justerDatoerGrunnlagslister(BeregnForskuddGrunnlag periodeGrunnlag) {
    justertInntektPeriodeListe = periodeGrunnlag.getBidragMottakerInntektPeriodeListe()
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    justertSivilstandPeriodeListe = periodeGrunnlag.getBidragMottakerSivilstandPeriodeListe()
        .stream()
        .map(SivilstandPeriode::new)
        .collect(toCollection(ArrayList::new));

    justertBarnPeriodeListe = Optional.ofNullable(periodeGrunnlag.getBidragMottakerBarnPeriodeListe())
        .stream()
        .flatMap(Collection::stream)
        .map(Periode::new)
        .collect(toCollection(ArrayList::new));

    justertBostatusPeriodeListe = periodeGrunnlag.getSoknadBarn().getSoknadBarnBostatusPeriodeListe()
        .stream()
        .map(BostatusPeriode::new)
        .collect(toCollection(ArrayList::new));

    justertAlderPeriodeListe = settBarnAlderPerioder(periodeGrunnlag.getSoknadBarn().getSoknadBarnFodselsdato(),
        periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil())
        .stream()
        .map(alderPeriode -> new AlderPeriode(alderPeriode.getAlderDatoFraTil(), alderPeriode.getAlder()))
        .collect(toCollection(ArrayList::new));

    justertSjablonPeriodeListe = periodeGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));
  }

  // Lagger bruddperioder ved å løpe gjennom alle periodelistene
  private void lagBruddperioder(BeregnForskuddGrunnlag periodeGrunnlag) {

    // Bygger opp liste over perioder, basert på alle typer inputparametre
    bruddPeriodeListe = new Periodiserer()
        .addBruddpunkt(periodeGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start beregning fra-dato
        .addBruddpunkter(justertInntektPeriodeListe)
        .addBruddpunkter(justertSivilstandPeriodeListe)
        .addBruddpunkter(justertBarnPeriodeListe)
        .addBruddpunkter(justertBostatusPeriodeListe)
        .addBruddpunkter(justertAlderPeriodeListe)
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkt(periodeGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start beregning til-dato
        .finnPerioder(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    if (bruddPeriodeListe.size() > 1) {
      if ((bruddPeriodeListe.get(bruddPeriodeListe.size() - 2).getDatoTil().equals(periodeGrunnlag.getBeregnDatoTil())) &&
          (bruddPeriodeListe.get(bruddPeriodeListe.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(bruddPeriodeListe.get(bruddPeriodeListe.size() - 2).getDatoFra(), null);
        bruddPeriodeListe.remove(bruddPeriodeListe.size() - 1);
        bruddPeriodeListe.remove(bruddPeriodeListe.size() - 1);
        bruddPeriodeListe.add(nyPeriode);
      }
    }
  }

  // Løper gjennom alle bruddperioder og foretar beregning
  private void beregnForskuddPerPeriode() {

    // Løper gjennom periodene og finner matchende verdi for hver kategori
    for (Periode beregningsperiode : bruddPeriodeListe) {

      var inntektListe = justertInntektPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getInntektType(), inntektPeriode.getInntektBelop())).collect(toList());

      var sivilstandKode = justertSivilstandPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(SivilstandPeriode::getSivilstandKode).findFirst().orElse(null);

      var alder = justertAlderPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(AlderPeriode::getAlder).findFirst().orElse(null);

      var bostatusKode = justertBostatusPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(BostatusPeriode::getBostatusKode).findFirst().orElse(null);

      var antallBarn = (int) justertBarnPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode)).count();

      var sjablonListe = justertSjablonPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      // Øk antall barn med 1 hvis søknadsbarnet bor hjemme
      if (BostatusKode.MED_FORELDRE.equals(bostatusKode)) {
        antallBarn = antallBarn + 1;
      }

      // Kaller beregningsmodulen for hver beregningsperiode
      var grunnlagBeregning = new GrunnlagBeregning(inntektListe, sivilstandKode, antallBarn, alder, bostatusKode, sjablonListe);

      periodeResultatListe.add(new ResultatPeriode(beregningsperiode, forskuddBeregning.beregn(grunnlagBeregning), grunnlagBeregning));
    }
  }

  // Deler opp i aldersperioder med utgangspunkt i fødselsdato
  private List<AlderPeriode> settBarnAlderPerioder(LocalDate fodselDato, LocalDate beregnDatoFra, LocalDate beregnDatoTil) {
    var bruddAlderListe = new ArrayList<AlderPeriode>();
    var barn11AarDato = fodselDato.plusYears(11).with(firstDayOfMonth());
    var barn18AarDato = fodselDato.plusYears(18).with(firstDayOfNextMonth());

    var alderStartPeriode = 0;
    if (!(barn11AarDato.isAfter(beregnDatoFra))) {
      if (!(barn18AarDato.isAfter(beregnDatoFra))) {
        alderStartPeriode = 18;
      } else {
        alderStartPeriode = 11;
      }
    }

    // Barn fyller 11 år i perioden
    var barn11AarIPerioden = (barn11AarDato.isAfter(beregnDatoFra.minusDays(1)) && barn11AarDato.isBefore(beregnDatoTil.plusDays(1)));

    // Barn fyller 18 år i perioden
    var barn18AarIPerioden = (barn18AarDato.isAfter(beregnDatoFra.minusDays(1)) && barn18AarDato.isBefore(beregnDatoTil.plusDays(1)));

    if (barn11AarIPerioden) {
      bruddAlderListe.add(new AlderPeriode(new Periode(beregnDatoFra.with(firstDayOfMonth()), barn11AarDato.with(firstDayOfMonth())), 0));
      if (barn18AarIPerioden) {
        bruddAlderListe.add(new AlderPeriode(new Periode(barn11AarDato.with(firstDayOfMonth()), barn18AarDato.with(firstDayOfMonth())), 11));
        bruddAlderListe.add(new AlderPeriode(new Periode(barn18AarDato.with(firstDayOfMonth()), null), 18));
      } else {
        bruddAlderListe.add(new AlderPeriode(new Periode(barn11AarDato.with(firstDayOfMonth()), null), 11));
      }

    } else {
      if (barn18AarIPerioden) {
        bruddAlderListe.add(new AlderPeriode(new Periode(beregnDatoFra.with(firstDayOfMonth()), barn18AarDato.with(firstDayOfMonth())), 11));
        bruddAlderListe.add(new AlderPeriode(new Periode(barn18AarDato.with(firstDayOfMonth()), null), 18));

      } else {
        bruddAlderListe.add(new AlderPeriode(new Periode(beregnDatoFra.with(firstDayOfMonth()), null), alderStartPeriode));
      }
    }

    return bruddAlderListe;
  }

  // Validerer at input-verdier til forskuddsberegning er gyldige
  public List<Avvik> validerInput(BeregnForskuddGrunnlag periodeGrunnlag) {

    // Sjekk beregn dato fra/til
    var avvikListe = new ArrayList<>(PeriodeUtil.validerBeregnPeriodeInput(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil()));

    // Sjekk perioder for inntekt
    var bidragMottakerInntektPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode bidragMottakerInntektPeriode : periodeGrunnlag.getBidragMottakerInntektPeriodeListe()) {
      bidragMottakerInntektPeriodeListe.add(bidragMottakerInntektPeriode.getDatoFraTil());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "bidragMottakerInntektPeriodeListe",
            bidragMottakerInntektPeriodeListe, false, true, false, true));

    // Sjekk perioder for sivilstand
    var bidragMottakerSivilstandPeriodeListe = new ArrayList<Periode>();
    for (SivilstandPeriode bidragMottakerSivilstandPeriode : periodeGrunnlag.getBidragMottakerSivilstandPeriodeListe()) {
      bidragMottakerSivilstandPeriodeListe.add(bidragMottakerSivilstandPeriode.getDatoFraTil());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "bidragMottakerSivilstandPeriodeListe",
            bidragMottakerSivilstandPeriodeListe, true, true, true, true));

    // Sjekk perioder for bostatus
    var soknadBarnBostatusPeriodeListe = new ArrayList<Periode>();
    for (BostatusPeriode soknadBarnBostatusPeriode : periodeGrunnlag.getSoknadBarn().getSoknadBarnBostatusPeriodeListe()) {
      soknadBarnBostatusPeriodeListe.add(soknadBarnBostatusPeriode.getDatoFraTil());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "soknadBarnBostatusPeriodeListe",
            soknadBarnBostatusPeriodeListe, true, true, true, true));

    // Sjekk perioder for barn
    if (periodeGrunnlag.getBidragMottakerBarnPeriodeListe() != null) {
      var bidragMottakerBarnPeriodeListe = new ArrayList<Periode>();

      for (Periode bidragMottakerBarnPeriode : periodeGrunnlag.getBidragMottakerBarnPeriodeListe()) {
        bidragMottakerBarnPeriodeListe.add(bidragMottakerBarnPeriode.getDatoFraTil());
      }
      avvikListe.addAll(
          PeriodeUtil.validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "bidragMottakerBarnPeriodeListe",
              bidragMottakerBarnPeriodeListe, false, false, false, false));
    }

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : periodeGrunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil
        .validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "sjablonPeriodeListe", sjablonPeriodeListe, false,
            false, false, false));

    return avvikListe;
  }
}
