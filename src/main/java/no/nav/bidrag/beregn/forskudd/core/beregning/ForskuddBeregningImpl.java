package no.nav.bidrag.beregn.forskudd.core.beregning;

import static no.nav.bidrag.beregn.felles.enums.BostatusKode.ENSLIG_ASYLANT;
import static no.nav.bidrag.beregn.felles.enums.BostatusKode.MED_FORELDRE;
import static no.nav.bidrag.beregn.felles.enums.SivilstandKode.ENSLIG;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.AVSLAG;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_125_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_200_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_250_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_50_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_75_PROSENT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.enums.SivilstandKode;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.Inntekt;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode;

public class ForskuddBeregningImpl implements ForskuddBeregning {

  private double maksInntektForskuddMottakerMultiplikator;
  private double inntektsintervallForskuddBelop;
  private double forskuddssats100ProsentBelop;
  private double inntektsgrense100ProsentForskuddBelop;
  private double inntektsgrenseEnslig75ProsentForskuddBelop;
  private double inntektsgrenseGiftSamboer75ProsentForskuddBelop;

  public ForskuddBeregningImpl() {
    this.maksInntektForskuddMottakerMultiplikator = 0d;
    this.inntektsintervallForskuddBelop = 0d;
    this.forskuddssats100ProsentBelop = 0d;
    this.inntektsgrense100ProsentForskuddBelop = 0d;
    this.inntektsgrenseEnslig75ProsentForskuddBelop = 0d;
    this.inntektsgrenseGiftSamboer75ProsentForskuddBelop = 0d;
  }

  @Override
  public ResultatBeregning beregn(GrunnlagBeregning grunnlag) {

    hentSjablonVerdier(grunnlag.getSjablonListe());

    var maksInntektsgrense = forskuddssats100ProsentBelop * maksInntektForskuddMottakerMultiplikator;
    var inntektsIntervallTotal = (grunnlag.getAntallBarnIHusstand() - 1) * inntektsintervallForskuddBelop;
    if (inntektsIntervallTotal < 0) {
      inntektsIntervallTotal = 0;
    }
    ResultatKode resultatKode;
    String regel;

    // Legger sammen inntektene
    var bidragMottakerInntekt = grunnlag.getBidragMottakerInntektListe().stream().map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Søknadsbarn er over 18 år (REGEL 1)
    if (grunnlag.getSoknadBarnAlder() >= 18) {
      resultatKode = AVSLAG;
      regel = "REGEL 1";

      // Søknadsbarn er enslig asylant (REGEL 2/3)
    } else if (grunnlag.getSoknadBarnBostatusKode().equals(ENSLIG_ASYLANT)) {
      resultatKode = (grunnlag.getSoknadBarnAlder() >= 11) ? INNVILGET_250_PROSENT : INNVILGET_200_PROSENT;
      regel = (resultatKode.equals(INNVILGET_250_PROSENT) ? "REGEL 2" : "REGEL 3");

      // Søknadsbarn bor alene eller ikke med foreldre (REGEL 4/5)
    } else if (!(grunnlag.getSoknadBarnBostatusKode().equals(MED_FORELDRE))) {
      resultatKode = (grunnlag.getSoknadBarnAlder() >= 11) ? INNVILGET_125_PROSENT : INNVILGET_100_PROSENT;
      regel = (resultatKode.equals(INNVILGET_125_PROSENT) ? "REGEL 4" : "REGEL 5");

      // Over maks inntektsgrense for forskudd (REGEL 6)
    } else if (!(erUnderInntektsGrense(maksInntektsgrense, bidragMottakerInntekt))) {
      resultatKode = AVSLAG;
      regel = "REGEL 6";

      // Under maks inntektsgrense for fullt forskudd (REGEL 7/8)
    } else if (erUnderInntektsGrense(inntektsgrense100ProsentForskuddBelop, bidragMottakerInntekt)) {
      resultatKode = (grunnlag.getSoknadBarnAlder() >= 11) ? INNVILGET_125_PROSENT : INNVILGET_100_PROSENT;
      regel = (resultatKode.equals(INNVILGET_125_PROSENT) ? "REGEL 7" : "REGEL 8");

      // Resterende regler (gift/enslig) (REGEL 9/10/11/12/13/14/15/16)
    } else {
      resultatKode = (erUnderInntektsGrense(
          settInntektsgrense(grunnlag.getBidragMottakerSivilstandKode(), inntektsgrenseEnslig75ProsentForskuddBelop,
              inntektsgrenseGiftSamboer75ProsentForskuddBelop) + inntektsIntervallTotal, bidragMottakerInntekt)) ? INNVILGET_75_PROSENT
          : INNVILGET_50_PROSENT;
      if (grunnlag.getBidragMottakerSivilstandKode().equals(ENSLIG)) {
        if (grunnlag.getAntallBarnIHusstand() == 1) {
          regel = (resultatKode.equals(INNVILGET_75_PROSENT) ? "REGEL 9" : "REGEL 10");
        } else {
          regel = (resultatKode.equals(INNVILGET_75_PROSENT) ? "REGEL 11" : "REGEL 12");
        }
      } else {
        if (grunnlag.getAntallBarnIHusstand() == 1) {
          regel = (resultatKode.equals(INNVILGET_75_PROSENT) ? "REGEL 13" : "REGEL 14");
        } else {
          regel = (resultatKode.equals(INNVILGET_75_PROSENT) ? "REGEL 15" : "REGEL 16");
        }
      }
    }

    return new ResultatBeregning(beregnForskudd(resultatKode, forskuddssats100ProsentBelop), resultatKode, regel, byggSjablonListe());
  }

  // Henter sjablonverdier
  private void hentSjablonVerdier(List<Sjablon> sjablonListe) {
    forskuddssats100ProsentBelop = SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.FORSKUDDSSATS_BELOP);
    maksInntektForskuddMottakerMultiplikator = SjablonUtil
        .hentSjablonverdi(sjablonListe, SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR);
    inntektsgrense100ProsentForskuddBelop = SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP);
    inntektsgrenseEnslig75ProsentForskuddBelop = SjablonUtil
        .hentSjablonverdi(sjablonListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP);
    inntektsgrenseGiftSamboer75ProsentForskuddBelop = SjablonUtil
        .hentSjablonverdi(sjablonListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP);
    inntektsintervallForskuddBelop = SjablonUtil
        .hentSjablonverdi(sjablonListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP);
  }

  // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
  private List<SjablonNavnVerdi> byggSjablonListe() {
    var sjablonNavnVerdiListe = new ArrayList<SjablonNavnVerdi>();
    sjablonNavnVerdiListe.add(new SjablonNavnVerdi(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), forskuddssats100ProsentBelop));
    sjablonNavnVerdiListe.add(new SjablonNavnVerdi(SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.getNavn(),
        maksInntektForskuddMottakerMultiplikator));
    sjablonNavnVerdiListe.add(new SjablonNavnVerdi(SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.getNavn(),
        inntektsgrense100ProsentForskuddBelop));
    sjablonNavnVerdiListe.add(new SjablonNavnVerdi(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.getNavn(),
        inntektsgrenseEnslig75ProsentForskuddBelop));
    sjablonNavnVerdiListe.add(new SjablonNavnVerdi(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.getNavn(),
        inntektsgrenseGiftSamboer75ProsentForskuddBelop));
    sjablonNavnVerdiListe.add(new SjablonNavnVerdi(SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.getNavn(), inntektsintervallForskuddBelop));
    return sjablonNavnVerdiListe;
  }

  // Beregner forskuddsbeløp basert på resultatkode
  private static BigDecimal beregnForskudd(ResultatKode resultatKode, double forskuddssats100ProsentBelop) {
    return switch (resultatKode) {
      case INNVILGET_50_PROSENT -> BigDecimal.valueOf(forskuddssats100ProsentBelop * 0.5);
      case INNVILGET_75_PROSENT -> BigDecimal.valueOf(forskuddssats100ProsentBelop * 0.75);
      case INNVILGET_100_PROSENT -> BigDecimal.valueOf(forskuddssats100ProsentBelop);
      case INNVILGET_125_PROSENT -> BigDecimal.valueOf(forskuddssats100ProsentBelop * 1.25);
      case INNVILGET_200_PROSENT -> BigDecimal.valueOf(forskuddssats100ProsentBelop * 2);
      case INNVILGET_250_PROSENT -> BigDecimal.valueOf(forskuddssats100ProsentBelop * 2.5);
      default -> BigDecimal.ZERO;
    };
  }

  private static boolean erUnderInntektsGrense(double inntektsgrense, BigDecimal inntekt) {
    return inntekt.compareTo(BigDecimal.valueOf(inntektsgrense)) < 1;
  }

  private static double settInntektsgrense(SivilstandKode sivilstandKode, double inntektsgrenseEnslig75Prosent,
      double inntektsgrenseGift75Prosent) {
    return sivilstandKode.equals(ENSLIG) ? inntektsgrenseEnslig75Prosent : inntektsgrenseGift75Prosent;
  }
}
