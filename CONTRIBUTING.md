Requirements
------------
**[wkhtmltopdf](http://wkhtmltopdf.org/) must be installed and working on your system.**

Running tests
------------

This repo has two kinds of tests: integration (dependent on `wkhtmltopdf`) and unit tests.

By default, `mvn clean install` runs all the tests. Use `mvn test -B` to run only the unit tests.

Generating releases
------------

Workflows are automated via Github Actions.

In order to generate a new release:
- Update the release version in pom.xml and README.md
- Create a new release tag in github, this will trigger a github actions workflow

After the Github Actions `publish` workflow finishes, the release must be [available at OSSRH](https://oss.sonatype.org/#welcome).