Java WkHtmlToPdf Wrapper [![Build Status](https://travis-ci.org/jhonnymertz/java-wkhtmltopdf-wrapper.svg?branch=master)](https://travis-ci.org/jhonnymertz/java-wkhtmltopdf-wrapper)
=========

A Java based wrapper for the [wkhtmltopdf](http://wkhtmltopdf.org/) command line tool. As the name implies, it uses WebKit to convert HTML documents to PDFs.

Requirements
------------
**[wkhtmltopdf](http://wkhtmltopdf.org/) must be installed and working on your system.**

### Wrapper project dependency
Make sure you have Java Wrapper dependency added to your project.

If you are using Gradle/Maven, see example below:

#### Gradle
In your `build.gradle`:
```groovy
dependencies {
    compile 'com.github.jhonnymertz:java-wkhtmltopdf-wrapper:1.1.14-RELEASE'
}
```

#### Maven
In your `pom.xml`:
```xml
<dependency>
    <groupId>com.github.jhonnymertz</groupId>
    <artifactId>java-wkhtmltopdf-wrapper</artifactId>
    <version>1.1.14-RELEASE</version>
</dependency>
```

Usage and Examples
------------
```java
Pdf pdf = new Pdf();

pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");
pdf.addPageFromUrl("http://www.google.com");

// Add a Table of Contents
pdf.addToc();

// The `wkhtmltopdf` shell command accepts different types of options such as global, page, headers and footers, and toc. Please see `wkhtmltopdf -H` for a full explanation.
// All options are passed as array, for example:
pdf.addParam(new Param("--no-footer-line"), new Param("--header-html", "file:///header.html"));
pdf.addParam(new Param("--enable-javascript"));

// Add styling for Table of Contents
pdf.addTocParam(new Param("--xsl-style-sheet", "my_toc.xsl"));

// Save the PDF
pdf.saveAs("output.pdf");
```

### Xvfb Support

```java
XvfbConfig xc = new XvfbConfig();
xc.addParams(new Param("--auto-servernum"), new Param("--server-num=1"));

WrapperConfig wc = new WrapperConfig();
wc.setXvfbConfig(xc);

Pdf pdf = new Pdf(wc);
pdf.addPageFromUrl("http://www.google.com");

pdf.saveAs("output.pdf");
```

### wkhtmltopdf exit codes

wkhtmltopdf may return non-zero exit codes to denote warnings, you can now set the Pdf object to allow this:

```java
Pdf pdf = new Pdf();
pdf.addPageFromUrl("http://www.google.com");

pdf.setAllowMissingAssets();
// or:  
pdf.setSuccessValues(Arrays.asList(0, 1));

pdf.saveAs("output.pdf");
```

### Cleaning up temporary files

After the PDF generation, the library automatically cleans up the temporary files created. However, there may be situations in which the `Pdf` object is created but no PDF is generated. To avoid increasing the temp folder size and having problems, you can force the deletion of all temporary files created by the library by:

```java
Pdf pdf = new Pdf();
pdf.cleanAllTempFiles();
```

This is not an official Wkhtmltopdf product
------------
This library is not an official Wkhtmltopdf product. Support is available on a best-effort basis via github issue tracking. Pull requests are welcomed.

Bugs
------------
- Tests are incomplete

Known issues
------------

**Output of wkhtmltopdf is being added to resulting pdf** ([Issue #19](https://github.com/jhonnymertz/java-wkhtmltopdf-wrapper/issues/19))
- Starting from 1.1.10-RELEASE version, there is a method `saveAsDirect(String path)`, which executes wkhtmltopdf passing the `path` as output for wkhtmltopdf, instead of the standard input `-`. This saves the results directly to the specified file `path`.

**Because this library relies on `wkhtmltopdf`, it does not support concurrent PDF generations.**

License
------------
This project is available under MIT Licence.
