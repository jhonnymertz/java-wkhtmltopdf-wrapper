#!/bin/bash

if ([ "$TRAVIS_BRANCH" == "master" ] || [ ! -z "$TRAVIS_TAG" ]) &&
    [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  echo "This will deploy!"
else
  echo "This will not deploy!"
fi

echo "$TRAVIS_BRANCH"
echo "$TRAVIS_PULL_REQUEST"
echo "$TRAVIS_TAG"

if [[ ($TRAVIS_PULL_REQUEST == "false") && ($TRAVIS_BRANCH == "master") ]]; then
    #openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $GPG_DIR/pubring.gpg.enc -out $GPG_DIR/pubring.gpg -d
    #openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $GPG_DIR/secring.gpg.enc -out $GPG_DIR/secring.gpg -d
    #mvn deploy --settings $GPG_DIR/settings.xml -DperformRelease=true -DskipTests=true
    exit $?
fi