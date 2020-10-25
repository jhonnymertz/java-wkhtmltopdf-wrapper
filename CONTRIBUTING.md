Requirements
------------
**[wkhtmltopdf](http://wkhtmltopdf.org/) must be installed and working on your system.**

Running tests
------------

This repo has two kinds of tests: integration (dependent on `wkhtmltopdf`) and unit tests.

By default, `mvn clean install` runs all the tests. Use `mvn test -B` to run only the unit tests.
