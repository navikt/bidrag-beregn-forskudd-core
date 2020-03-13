package no.nav.bidrag.beregn.forskudd.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ForskuddBeregningResultat;
import no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ResultatKode;
import no.nav.bidrag.beregn.forskudd.core.dto.BidragMottakerInntektPeriodeDto;
import no.nav.bidrag.beregn.forskudd.core.dto.BidragMottakerSivilstandPeriodeDto;
import no.nav.bidrag.beregn.forskudd.core.dto.BostatusPeriodeDto;
import no.nav.bidrag.beregn.forskudd.core.dto.ForskuddPeriodeGrunnlagDto;
import no.nav.bidrag.beregn.forskudd.core.dto.PeriodeDto;
import no.nav.bidrag.beregn.forskudd.core.dto.SjablontallDto;
import no.nav.bidrag.beregn.forskudd.core.dto.SoknadBarnDto;
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode;
import no.nav.bidrag.beregn.forskudd.core.periode.grunnlag.Periode;
import no.nav.bidrag.beregn.forskudd.core.periode.resultat.ForskuddPeriodeResultat;
import no.nav.bidrag.beregn.forskudd.core.periode.resultat.PeriodeResultat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("ForskuddCore (dto test)")
public class ForskuddCoreTest {

  @InjectMocks
  private ForskuddCore forskuddCore = ForskuddCore.getInstance();

  @Mock
  private ForskuddPeriode forskuddPeriodeMock;

  private ForskuddPeriodeGrunnlagDto forskuddPeriodeGrunnlagDto;
  private ForskuddPeriodeResultat forskuddPeriodeResultat;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @DisplayName("skal beregne forskudd")
  void skalBeregneForskudd() {
    byggForskuddPeriodeGrunnlagDto();
    byggForskuddPeriodeResultat();

    when(forskuddPeriodeMock.beregnPerioder(any())).thenReturn(forskuddPeriodeResultat);
    var forskuddPeriodeResultatDto = forskuddCore.beregnForskudd(forskuddPeriodeGrunnlagDto);

    assertAll(
        () -> assertThat(forskuddPeriodeResultatDto).isNotNull(),
        () -> assertThat(forskuddPeriodeResultatDto.getPeriodeResultatListe().size()).isEqualTo(3),

        () -> assertThat(
            forskuddPeriodeResultatDto.getPeriodeResultatListe().get(0).getDatoFraTil().getDatoFra().equals(LocalDate.parse("2017-01-01"))),
        () -> assertThat(
            forskuddPeriodeResultatDto.getPeriodeResultatListe().get(0).getDatoFraTil().getDatoTil().equals(LocalDate.parse("2018-01-01"))),
        () -> assertThat(
            forskuddPeriodeResultatDto.getPeriodeResultatListe().get(0).getForskuddBeregningResultat().getBelop().equals(BigDecimal.valueOf(1600))),
        () -> assertThat(forskuddPeriodeResultatDto.getPeriodeResultatListe().get(0).getForskuddBeregningResultat().getResultatKode()
            .equals("INNVILGET_100_PROSENT")),
        () -> assertThat(
            forskuddPeriodeResultatDto.getPeriodeResultatListe().get(0).getForskuddBeregningResultat().getResultatBeskrivelse().equals("REGEL 1")),

        () -> assertThat(
            forskuddPeriodeResultatDto.getPeriodeResultatListe().get(1).getDatoFraTil().getDatoFra().equals(LocalDate.parse("2018-01-01"))),
        () -> assertThat(
            forskuddPeriodeResultatDto.getPeriodeResultatListe().get(1).getDatoFraTil().getDatoTil().equals(LocalDate.parse("2019-01-01"))),
        () -> assertThat(
            forskuddPeriodeResultatDto.getPeriodeResultatListe().get(1).getForskuddBeregningResultat().getBelop().equals(BigDecimal.valueOf(1200))),
        () -> assertThat(forskuddPeriodeResultatDto.getPeriodeResultatListe().get(1).getForskuddBeregningResultat().getResultatKode()
            .equals("INNVILGET_75_PROSENT")),
        () -> assertThat(
            forskuddPeriodeResultatDto.getPeriodeResultatListe().get(1).getForskuddBeregningResultat().getResultatBeskrivelse().equals("REGEL 2")),

        () -> assertThat(
            forskuddPeriodeResultatDto.getPeriodeResultatListe().get(2).getDatoFraTil().getDatoFra().equals(LocalDate.parse("2019-01-01"))),
        () -> assertThat(
            forskuddPeriodeResultatDto.getPeriodeResultatListe().get(2).getDatoFraTil().getDatoTil().equals(LocalDate.parse("2020-01-01"))),
        () -> assertThat(
            forskuddPeriodeResultatDto.getPeriodeResultatListe().get(2).getForskuddBeregningResultat().getBelop().equals(BigDecimal.valueOf(0))),
        () -> assertThat(
            forskuddPeriodeResultatDto.getPeriodeResultatListe().get(2).getForskuddBeregningResultat().getResultatKode().equals("AVSLAG")),
        () -> assertThat(
            forskuddPeriodeResultatDto.getPeriodeResultatListe().get(2).getForskuddBeregningResultat().getResultatBeskrivelse().equals("REGEL 11"))
    );
  }

  private void byggForskuddPeriodeGrunnlagDto() {
    var bostatusPeriodeDto = new BostatusPeriodeDto(new PeriodeDto(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), "MED_FORELDRE");
    var bostatusPeriodeDtoListe = new ArrayList<BostatusPeriodeDto>();
    bostatusPeriodeDtoListe.add(bostatusPeriodeDto);
    var soknadBarnDto = new SoknadBarnDto(LocalDate.parse("2006-05-12"), bostatusPeriodeDtoListe);

    var bidragMottakerInntektPeriodeDto = new BidragMottakerInntektPeriodeDto(
        new PeriodeDto(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(0));
    var bidragMottakerInntektPeriodeDtoListe = new ArrayList<BidragMottakerInntektPeriodeDto>();
    bidragMottakerInntektPeriodeDtoListe.add(bidragMottakerInntektPeriodeDto);

    var bidragMottakerSivilstandPeriodeDto = new BidragMottakerSivilstandPeriodeDto(
        new PeriodeDto(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), "GIFT");
    var bidragMottakerSivilstandPeriodeDtoListe = new ArrayList<BidragMottakerSivilstandPeriodeDto>();
    bidragMottakerSivilstandPeriodeDtoListe.add(bidragMottakerSivilstandPeriodeDto);

    var periodeDtoListe = new ArrayList<PeriodeDto>();
    periodeDtoListe.add(new PeriodeDto(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")));

    var sjablontallDto = new SjablontallDto("0013", new PeriodeDto(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(0));
    var sjablontallDtoListe = new ArrayList<SjablontallDto>();
    sjablontallDtoListe.add(sjablontallDto);

    forskuddPeriodeGrunnlagDto = new ForskuddPeriodeGrunnlagDto(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"), soknadBarnDto,
        bidragMottakerInntektPeriodeDtoListe, bidragMottakerSivilstandPeriodeDtoListe, periodeDtoListe, sjablontallDtoListe);
  }

  private void byggForskuddPeriodeResultat() {
    List<PeriodeResultat> periodeResultatListe = new ArrayList<>();
    periodeResultatListe
        .add(new PeriodeResultat(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), new ForskuddBeregningResultat(
            BigDecimal.valueOf(1600), ResultatKode.INNVILGET_100_PROSENT, "REGEL 1")));
    periodeResultatListe
        .add(new PeriodeResultat(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")), new ForskuddBeregningResultat(
            BigDecimal.valueOf(1200), ResultatKode.INNVILGET_75_PROSENT, "REGEL 2")));
    periodeResultatListe
        .add(new PeriodeResultat(new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")), new ForskuddBeregningResultat(
            BigDecimal.valueOf(0), ResultatKode.AVSLAG, "REGEL 11")));
    forskuddPeriodeResultat = new ForskuddPeriodeResultat(periodeResultatListe);
  }
}
