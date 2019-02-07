#!/bin/bash

if [ ! -z "$TRAVIS_TAG" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $GPG_DIR/pubring.gpg.enc -out $GPG_DIR/pubring.gpg -d
  openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $GPG_DIR/secring.gpg.enc -out $GPG_DIR/secring.gpg -d
  mvn deploy --settings $GPG_DIR/settings.xml -DperformRelease=true -DskipTests=true
  exit $?
else
  echo "This will not deploy because its not a release!"
fi