package no.nav.bidrag.beregn.forskudd;

import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.BostedStatusKode.MED_ANDRE_ENN_FORELDRE;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.BostedStatusKode.MED_FORELDRE;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.SivilstandKode.ENSLIG;
import static no.nav.bidrag.beregn.forskudd.beregning.grunnlag.SivilstandKode.GIFT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import no.nav.bidrag.beregn.forskudd.beregning.resultat.ResultatKode;
import no.nav.bidrag.beregn.forskudd.periode.ForskuddPeriode;
import no.nav.bidrag.beregn.forskudd.periode.ForskuddPeriodeImpl;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.ForskuddPeriodeGrunnlag;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.Periode;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.SivilstandPeriode;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.SoknadBarn;
import no.nav.bidrag.beregn.forskudd.periode.resultat.ForskuddPeriodeResultat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ForskuddsperiodeberegningTest")
class ForskuddPeriodeTest {

  private ForskuddPeriodeGrunnlag grunnlag = new ForskuddPeriodeGrunnlag();

  @Test
  @Order(1)
  @DisplayName("Test utvidet grunnlag")
  void testUtvidetGrunnlag() {
    lagGrunnlag();
    var resultat = forskuddsperiode().beregnPerioder(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getPeriodeResultatListe().size()).isEqualTo(8),

        () -> assertThat(resultat.getPeriodeResultatListe().get(0).getDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(0).getDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-12-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(0).getForskuddBeregningResultat().getResultatKode())
            .isEqualTo(ResultatKode.INNVILGET_100_PROSENT),
        () -> assertThat(resultat.getPeriodeResultatListe().get(0).getForskuddBeregningResultat().getResultatBeskrivelse()).isEqualTo("REGEL 8"),

        () -> assertThat(resultat.getPeriodeResultatListe().get(1).getDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-12-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(1).getDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(1).getForskuddBeregningResultat().getResultatKode())
            .isEqualTo(ResultatKode.INNVILGET_125_PROSENT),
        () -> assertThat(resultat.getPeriodeResultatListe().get(1).getForskuddBeregningResultat().getResultatBeskrivelse()).isEqualTo("REGEL 7"),

        () -> assertThat(resultat.getPeriodeResultatListe().get(2).getDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(2).getDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-05-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(2).getForskuddBeregningResultat().getResultatKode())
            .isEqualTo(ResultatKode.INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getPeriodeResultatListe().get(2).getForskuddBeregningResultat().getResultatBeskrivelse()).isEqualTo("REGEL 15"),

        () -> assertThat(resultat.getPeriodeResultatListe().get(3).getDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-05-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(3).getDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-09-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(3).getForskuddBeregningResultat().getResultatKode())
            .isEqualTo(ResultatKode.INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getPeriodeResultatListe().get(3).getForskuddBeregningResultat().getResultatBeskrivelse()).isEqualTo("REGEL 11"),

        () -> assertThat(resultat.getPeriodeResultatListe().get(4).getDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-09-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(4).getDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-12-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(4).getForskuddBeregningResultat().getResultatKode())
            .isEqualTo(ResultatKode.INNVILGET_125_PROSENT),
        () -> assertThat(resultat.getPeriodeResultatListe().get(4).getForskuddBeregningResultat().getResultatBeskrivelse()).isEqualTo("REGEL 4"),

        () -> assertThat(resultat.getPeriodeResultatListe().get(5).getDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-12-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(5).getDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(5).getForskuddBeregningResultat().getResultatKode())
            .isEqualTo(ResultatKode.INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getPeriodeResultatListe().get(5).getForskuddBeregningResultat().getResultatBeskrivelse()).isEqualTo("REGEL 11"),

        () -> assertThat(resultat.getPeriodeResultatListe().get(6).getDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(6).getDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(6).getForskuddBeregningResultat().getResultatKode())
            .isEqualTo(ResultatKode.INNVILGET_50_PROSENT),
        () -> assertThat(resultat.getPeriodeResultatListe().get(6).getForskuddBeregningResultat().getResultatBeskrivelse()).isEqualTo("REGEL 12"),

        () -> assertThat(resultat.getPeriodeResultatListe().get(7).getDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getPeriodeResultatListe().get(7).getDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getPeriodeResultatListe().get(7).getForskuddBeregningResultat().getResultatKode())
            .isEqualTo(ResultatKode.INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getPeriodeResultatListe().get(7).getForskuddBeregningResultat().getResultatBeskrivelse()).isEqualTo("REGEL 11")
    );
    printGrunnlagResultat(resultat);
  }

  private ForskuddPeriode forskuddsperiode() {
    return new ForskuddPeriodeImpl();
  }

  private void lagGrunnlag() {
    grunnlag.setBeregnDatoFra(LocalDate.parse("2017-01-01"));
    grunnlag.setBeregnDatoTil(LocalDate.parse("2019-08-01"));

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), BigDecimal.valueOf(250000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")), BigDecimal.valueOf(400000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), null), BigDecimal.valueOf(500000)));
    grunnlag.setBidragMottakerInntektPeriodeListe(bmInntektListe);

    var bmSivilstandListe = new ArrayList<SivilstandPeriode>();
    bmSivilstandListe.add(new SivilstandPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-04-17")), GIFT));
    bmSivilstandListe.add(new SivilstandPeriode(new Periode(LocalDate.parse("2018-04-17"), null), ENSLIG));
    grunnlag.setBidragMottakerSivilstandPeriodeListe(bmSivilstandListe);

    var bmBarnListe = new ArrayList<Periode>();
    bmBarnListe.add(new Periode(LocalDate.parse("2017-01-01"), null));
    bmBarnListe.add(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-17")));
    bmBarnListe.add(new Periode(LocalDate.parse("2019-03-31"), null));
    grunnlag.setBidragMottakerBarnPeriodeListe(bmBarnListe);

    var soknadBarn = new SoknadBarn();
    var bostedStatusListe = new ArrayList<BostatusPeriode>();
    soknadBarn.setFodselDato(LocalDate.parse("2006-12-19"));
    bostedStatusListe.add(new BostatusPeriode(new Periode(LocalDate.parse("2006-12-19"), LocalDate.parse("2018-08-16")), MED_FORELDRE));
    bostedStatusListe.add(new BostatusPeriode(new Periode(LocalDate.parse("2018-08-16"), LocalDate.parse("2018-11-13")), MED_ANDRE_ENN_FORELDRE));
    bostedStatusListe.add(new BostatusPeriode(new Periode(LocalDate.parse("2018-11-13"), null), MED_FORELDRE));
    soknadBarn.setSoknadBarnBostatusPeriodeListe(bostedStatusListe);
    grunnlag.setSoknadBarn(soknadBarn);
  }

  private void printGrunnlagResultat(ForskuddPeriodeResultat resultat) {
    resultat.getPeriodeResultatListe().stream().sorted(Comparator.comparing(pR -> pR.getDatoFraTil().getDatoFra()))
        .forEach(sortedPR -> System.out
            .println("Dato fra: " + sortedPR.getDatoFraTil().getDatoFra() + "; " + "Dato til: " + sortedPR.getDatoFraTil().getDatoTil()
                + "; " + "Beløp: " + sortedPR.getForskuddBeregningResultat().getBelop() + "; " + "Resultatkode: " + sortedPR
                .getForskuddBeregningResultat().getResultatKode()
                + "; " + "Regel: " + sortedPR.getForskuddBeregningResultat().getResultatBeskrivelse()));
  }
}
