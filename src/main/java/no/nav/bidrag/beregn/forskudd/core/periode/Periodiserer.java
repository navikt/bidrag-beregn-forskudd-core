package no.nav.bidrag.beregn.forskudd.core.periode;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.firstDayOfNextMonth;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Periodiserer {

  private final Set<LocalDate> bruddpunkter = new HashSet<>();
  private boolean aapenSluttdato = false;

  public Periodiserer addBruddpunkt(LocalDate dato) {
    bruddpunkter.add(dato);
    return this;
  }

  private void addBruddpunkter(Periode periode) {
    addBruddpunkt(periode.getDatoFra());
    if (periode.getDatoTil() == null) {
      aapenSluttdato = true;
    } else {
      addBruddpunkt(periode.getDatoTil());
    }
  }

  public Periodiserer addBruddpunkter(PeriodisertGrunnlag grunnlag) {
    addBruddpunkter(grunnlag.getDatoFraTil());
    return this;
  }

  Periodiserer addBruddpunkter(Iterable<? extends PeriodisertGrunnlag> grunnlagListe) {
    for (PeriodisertGrunnlag grunnlag : grunnlagListe) {
      addBruddpunkter(grunnlag);
    }
    return this;
  }

  // Genererer brudd når søknadsbarnet passerer 11 år og 18 år
  // 0 og 11 år justeres til den første inneværende måned, 18 år justeres til den første neste måned
  Periodiserer addBruddpunkter(LocalDate fodselDato, LocalDate beregnDatoFra, LocalDate beregnDatoTil) {

    var barn11AarDato = fodselDato.plusYears(11).with(firstDayOfMonth());
    var barn18AarDato = fodselDato.plusYears(18).with(firstDayOfNextMonth());
    var barn11AarIPerioden = barn11AarDato.isAfter(beregnDatoFra.minusDays(1)) && barn11AarDato.isBefore(beregnDatoTil.plusDays(1));
    var barn18AarIPerioden = barn18AarDato.isAfter(beregnDatoFra.minusDays(1)) && barn18AarDato.isBefore(beregnDatoTil.plusDays(1));

    if (barn11AarIPerioden) {
      addBruddpunkt(barn11AarDato);
    }

    if (barn18AarIPerioden) {
      addBruddpunkt(barn18AarDato);
    }
    return this;
  }

  // Setter perioder basert på fra- og til-dato
  public List<Periode> finnPerioder(LocalDate beregnDatoFra, LocalDate beregnDatoTil) {
    var sortertBruddpunktListe = bruddpunkter.stream().filter((dato) -> dato.isAfter(beregnDatoFra.minusDays(1)))
        .filter((dato) -> dato.isBefore(beregnDatoTil.plusDays(1))).sorted().collect(toList());

    List<Periode> perioder = new ArrayList<>();
    Iterator<LocalDate> bruddpunktIt = sortertBruddpunktListe.iterator();
    if (bruddpunktIt.hasNext()) {
      LocalDate start = bruddpunktIt.next();
      while (bruddpunktIt.hasNext()) {
        LocalDate end = bruddpunktIt.next();
        perioder.add(new Periode(start, end));
        start = end;
      }
      if (aapenSluttdato) {
        perioder.add(new Periode(start, null));
      }
    }
    return perioder;
  }
}
