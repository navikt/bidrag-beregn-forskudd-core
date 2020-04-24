#!/bin/bash
set -e

CHECKOUT_TAG=$1

if [ -z "$CHECKOUT_TAG" ]
then
  echo "No tag to checkout is given"
  exit 1
fi

git clone --branch $CHECKOUT_TAG --depth=1 --single-branch https://github.com/navikt/bidrag-beregn-felles
cd bidrag-beregn-felles

mvn -B install -e -DskipTests
