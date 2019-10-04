#!/bin/bash

BRANCH="$1"

if [ "$BRANCH" != "travis_release" ]; then
    git remote -v
    git branch
    git checkout -b travis_release
    git merge "$TRAVIS_BRANCH"
    git branch
    git push https://__GITHUB_TOKEN__@github.com/vable travis_release  
else
	echo "Noting with github"
	exit 0
fi
