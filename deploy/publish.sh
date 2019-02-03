#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" == "false"  ]; then
	echo "PR"
  echo "$TRAVIS_PULL_REQUEST"
    #mvn deploy --settings $GPG_DIR/settings.xml -DperformRelease=true -DskipTests=true
    #exit $?
fi

if [ "$TRAVIS_BRANCH" == "master" ]; then
  echo "master"
  echo "$TRAVIS_BRANCH"
fi