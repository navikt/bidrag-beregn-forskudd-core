package no.nav.bidrag.beregn.forskudd.core;

import static no.nav.bidrag.beregn.forskudd.core.bo.BostatusKode.MED_ANDRE_ENN_FORELDRE;
import static no.nav.bidrag.beregn.forskudd.core.bo.BostatusKode.MED_FORELDRE;
import static no.nav.bidrag.beregn.forskudd.core.bo.SivilstandKode.ENSLIG;
import static no.nav.bidrag.beregn.forskudd.core.bo.SivilstandKode.GIFT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.Periode;
import no.nav.bidrag.beregn.forskudd.core.bo.Periodiserer;
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PeriodisererTest")
class PeriodisererTest {

  @Test
  void testPeriodiseringKunEnDato() {
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(LocalDate.parse("2019-01-01"))
        .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"));

    assertAll(
        () -> assertThat(perioder).isNotNull(),
        () -> assertThat(perioder.size()).isEqualTo(0)
    );
  }

  @Test
  void testPeriodiseringMedToDatoer() {
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(LocalDate.parse("2019-01-01"))
        .addBruddpunkt(LocalDate.parse("2019-03-01"))
        .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"));

    assertAll(
        () -> assertThat(perioder).isNotNull(),
        () -> assertThat(perioder.size()).isEqualTo(1),
        () -> assertThat(perioder.get(0).getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(perioder.get(0).getDatoTil()).isEqualTo(LocalDate.parse("2019-03-01"))
    );
  }

  @Test
  void testPeriodiseringMedGrunnlag() {
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkter(new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-03-01")), BigDecimal.valueOf(1000)))
        .addBruddpunkter(new SivilstandPeriode(new Periode(LocalDate.parse("2019-02-01"), LocalDate.parse("2019-04-01")), GIFT))
        .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"));

    assertAll(
        () -> assertThat(perioder).isNotNull(),
        () -> assertThat(perioder.size()).isEqualTo(3),

        () -> assertThat(perioder.get(0).getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(perioder.get(0).getDatoTil()).isEqualTo(LocalDate.parse("2019-02-01")),

        () -> assertThat(perioder.get(1).getDatoFra()).isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(perioder.get(1).getDatoTil()).isEqualTo(LocalDate.parse("2019-03-01")),

        () -> assertThat(perioder.get(2).getDatoFra()).isEqualTo(LocalDate.parse("2019-03-01")),
        () -> assertThat(perioder.get(2).getDatoTil()).isEqualTo(LocalDate.parse("2019-04-01"))
    );
  }

  @Test
  void testDuplikatePerioder() {
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkter(new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-04-01")), BigDecimal.valueOf(1000)))
        .addBruddpunkter(new SivilstandPeriode(new Periode(LocalDate.parse("2019-02-01"), LocalDate.parse("2019-04-01")), GIFT))
        .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"));

    assertAll(
        () -> assertThat(perioder).isNotNull(),
        () -> assertThat(perioder.size()).isEqualTo(2),

        () -> assertThat(perioder.get(0).getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(perioder.get(0).getDatoTil()).isEqualTo(LocalDate.parse("2019-02-01")),

        () -> assertThat(perioder.get(1).getDatoFra()).isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(perioder.get(1).getDatoTil()).isEqualTo(LocalDate.parse("2019-04-01"))
    );
  }

  @Test
  void testPeriodiseringMedGrunnlagOgAapenSlutt() {
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkter(new SivilstandPeriode(new Periode(LocalDate.parse("2019-02-01"), null), GIFT))
        .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"));

    assertAll(
        () -> assertThat(perioder).isNotNull(),
        () -> assertThat(perioder.size()).isEqualTo(1),

        () -> assertThat(perioder.get(0).getDatoFra()).isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(perioder.get(0).getDatoTil()).isNull()
    );
  }

  @Test
  void testPeriodiseringMedUtvidetGrunnlag() {
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkter(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), BigDecimal.valueOf(250000)))
        .addBruddpunkter(new InntektPeriode(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")), BigDecimal.valueOf(400000)))
        .addBruddpunkter(new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), null), BigDecimal.valueOf(500000)))
        .addBruddpunkter(new SivilstandPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-04-17")), GIFT))
        .addBruddpunkter(new SivilstandPeriode(new Periode(LocalDate.parse("2018-04-17"), null), ENSLIG))
        .addBruddpunkter(new Periode(LocalDate.parse("2017-01-01"), null))
        .addBruddpunkter(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-16")))
        .addBruddpunkter(new Periode(LocalDate.parse("2019-03-31"), null))
        .addBruddpunkter(new BostatusPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-08-16")), MED_FORELDRE))
        .addBruddpunkter(new BostatusPeriode(new Periode(LocalDate.parse("2018-08-16"), LocalDate.parse("2018-11-13")), MED_ANDRE_ENN_FORELDRE))
        .addBruddpunkter(new BostatusPeriode(new Periode(LocalDate.parse("2018-11-13"), null), MED_FORELDRE))
        .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"));

    assertAll(
        () -> assertThat(perioder).isNotNull(),
        () -> assertThat(perioder.size()).isEqualTo(8),

        () -> assertThat(perioder.get(0).getDatoFra()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(perioder.get(0).getDatoTil()).isEqualTo(LocalDate.parse("2018-01-01")),

        () -> assertThat(perioder.get(1).getDatoFra()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(perioder.get(1).getDatoTil()).isEqualTo(LocalDate.parse("2018-04-17")),

        () -> assertThat(perioder.get(2).getDatoFra()).isEqualTo(LocalDate.parse("2018-04-17")),
        () -> assertThat(perioder.get(2).getDatoTil()).isEqualTo(LocalDate.parse("2018-06-16")),

        () -> assertThat(perioder.get(3).getDatoFra()).isEqualTo(LocalDate.parse("2018-06-16")),
        () -> assertThat(perioder.get(3).getDatoTil()).isEqualTo(LocalDate.parse("2018-08-16")),

        () -> assertThat(perioder.get(4).getDatoFra()).isEqualTo(LocalDate.parse("2018-08-16")),
        () -> assertThat(perioder.get(4).getDatoTil()).isEqualTo(LocalDate.parse("2018-11-13")),

        () -> assertThat(perioder.get(5).getDatoFra()).isEqualTo(LocalDate.parse("2018-11-13")),
        () -> assertThat(perioder.get(5).getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),

        () -> assertThat(perioder.get(6).getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(perioder.get(6).getDatoTil()).isEqualTo(LocalDate.parse("2019-03-31")),

        () -> assertThat(perioder.get(7).getDatoFra()).isEqualTo(LocalDate.parse("2019-03-31")),
        () -> assertThat(perioder.get(7).getDatoTil()).isNull()
    );
  }
}