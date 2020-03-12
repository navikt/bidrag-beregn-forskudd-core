package no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag;

import java.math.BigDecimal;

public class ForskuddBeregningGrunnlag {

  public ForskuddBeregningGrunnlag() {
  }

  private BigDecimal bidragMottakerInntekt;
  private SivilstandKode bidragMottakerSivilstandKode;
  private Integer antallBarnIHusstand;
  private Integer soknadBarnAlder;
  private BostedStatusKode soknadBarnBostedStatusKode;
  private Integer forskuddssats100Prosent;
  private Integer multiplikatorMaksInntektsgrense;
  private Integer inntektsgrense100ProsentForskudd;
  private Integer inntektsgrenseEnslig75ProsentForskudd;
  private Integer inntektsgrenseGift75ProsentForskudd;
  private Integer inntektsintervallForskudd;


  public ForskuddBeregningGrunnlag(BigDecimal bidragMottakerInntekt,
      SivilstandKode bidragMottakerSivilstandKode, Integer antallBarnIHusstand, Integer soknadBarnAlder,
      BostedStatusKode soknadBarnBostedStatusKode, Integer forskuddssats100Prosent, Integer multiplikatorMaksInntektsgrense,
      Integer inntektsgrense100ProsentForskudd, Integer inntektsgrenseEnslig75ProsentForskudd, Integer inntektsgrenseGift75ProsentForskudd,
      Integer inntektsintervallForskudd) {
    this.bidragMottakerInntekt = bidragMottakerInntekt;
    this.bidragMottakerSivilstandKode = bidragMottakerSivilstandKode;
    this.antallBarnIHusstand = antallBarnIHusstand;
    this.soknadBarnAlder = soknadBarnAlder;
    this.soknadBarnBostedStatusKode = soknadBarnBostedStatusKode;
    this.forskuddssats100Prosent = forskuddssats100Prosent;
    this.multiplikatorMaksInntektsgrense = multiplikatorMaksInntektsgrense;
    this.inntektsgrense100ProsentForskudd = inntektsgrense100ProsentForskudd;
    this.inntektsgrenseEnslig75ProsentForskudd = inntektsgrenseEnslig75ProsentForskudd;
    this.inntektsgrenseGift75ProsentForskudd = inntektsgrenseGift75ProsentForskudd;
    this.inntektsintervallForskudd = inntektsintervallForskudd;
  }

  public BigDecimal getBidragMottakerInntekt() {
    return bidragMottakerInntekt;
  }

  public SivilstandKode getBidragMottakerSivilstandKode() {
    return bidragMottakerSivilstandKode;
  }

  public Integer getAntallBarnIHusstand() {
    return antallBarnIHusstand;
  }

  public Integer getSoknadBarnAlder() {
    return soknadBarnAlder;
  }

  public BostedStatusKode getSoknadBarnBostedStatusKode() {
    return soknadBarnBostedStatusKode;
  }

  public Integer getForskuddssats100Prosent() {
    return forskuddssats100Prosent;
  }

  public Integer getMultiplikatorMaksInntektsgrense() {
    return multiplikatorMaksInntektsgrense;
  }

  public Integer getInntektsgrense100ProsentForskudd() {
    return inntektsgrense100ProsentForskudd;
  }

  public Integer getInntektsgrenseEnslig75ProsentForskudd() {
    return inntektsgrenseEnslig75ProsentForskudd;
  }

  public Integer getInntektsgrenseGift75ProsentForskudd() {
    return inntektsgrenseGift75ProsentForskudd;
  }

  public Integer getInntektsintervallForskudd() {
    return inntektsintervallForskudd;
  }
}
