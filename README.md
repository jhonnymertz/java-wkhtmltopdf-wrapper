Java WkHtmlToPdf Wrapper [![Build](https://github.com/jhonnymertz/java-wkhtmltopdf-wrapper/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/jhonnymertz/java-wkhtmltopdf-wrapper/actions/workflows/build.yml) [![Release](https://github.com/jhonnymertz/java-wkhtmltopdf-wrapper/actions/workflows/publish.yml/badge.svg)](https://github.com/jhonnymertz/java-wkhtmltopdf-wrapper/actions/workflows/publish.yml)
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
    compile 'com.github.jhonnymertz:java-wkhtmltopdf-wrapper:1.2.0-RELEASE'
}
```

#### Maven
In your `pom.xml`:
```xml
<dependency>
    <groupId>com.github.jhonnymertz</groupId>
    <artifactId>java-wkhtmltopdf-wrapper</artifactId>
    <version>1.2.0-RELEASE</version>
</dependency>
```

Usage and Examples
------------
```java
// Attempt to find the wkhtmltopdf command from OS path
String executable = WrapperConfig.findExecutable();

// Initialize with the command wrapper
// Customize the OS command to be called if needed
Pdf pdf = new Pdf(new WrapperConfig(executable));

// Add global params:
pdf.addParam(new Param("--no-footer-line"), new Param("--header-html", "file:///header.html"));
pdf.addParam(new Param("--enable-javascript"));
pdf.addParam(new Param("--javascript-delay", "2000"));

// Add pages
Page page1 = pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");
Page page2 = pdf.addPageFromUrl("http://www.google.com");

// Add per-page params
page1.addParam(new Param("--footer-center", "Page1Footer"));
page1.addParam(new Param("--exclude-from-outline"));
page2.addParam(new Param( "--header-center", "Page2HeaderOverride"));

// Add a Table of Contents and ToC params
TableOfContents toc = pdf.addToc();
toc.addParam(new Param("--footer-center", "TocFooter"));
toc.addParam(new Param("--xsl-style-sheet", "my_toc.xsl"));

// Save the PDF
pdf.saveAs("output.pdf");
```

### `wkhtmltopdf` location and automatic retrieval attempt

Library provides a method to attempt to find the `wkhtmltopdf` executable from the OS path, or an exact location can be provided:

```java
// Attempt to find the wkhtmltopdf command from OS path
String executable = WrapperConfig.findExecutable();

// Exact custom location
String executable = "/usr/local/bin/wkhtmltopdf";

final Pdf pdf = new Pdf(new WrapperConfig(executable));
```

> Note: make sure the tool `wkhtmltopdf` is installed and in the OS path for the user currently running the Java application. Otherwise, library will not be able to find the `wkhtmltopdf`. To test if the command is visible, you may try `which wkhtmltopdf` (linux) or `where wkhtmltopdf` (windows) in the command line. 

### Global params vs. per-object params

`wkhtmltopdf` accepts different types of options such as global, page, headers and footers, and toc. Please see `wkhtmltopdf -H` for a full explanation. The library allows setting params globally and per-object, as follows.

```java
final Pdf pdf = new Pdf(new WrapperConfig(WrapperConfig.findExecutable()));

// Adding global params
pdf.addParam(new Param("--header-center", "GlobalHeader"));

// Adding per-object params
TableOfContents toc = pdf.addToc();
toc.addParam(new Param("--footer-center", "TocFooter"));

Page page1 = pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Page1</h1></html>");
page1.addParam(new Param("--footer-center", "Page1Footer"));
page1.addParam(new Param("--exclude-from-outline")); // removes from toc

Page page2 = pdf.addPageFromUrl("http://www.google.com");
page2.addParam(new Param( "--header-center", "Page2HeaderOverride")); // override global header
```

### Page/object ordering and ToC positioning

The list of pages/objects in the document appear as the order in which pages/objects were added to the main `pdf`. Except by the ToC, which is always placed at the beginning of the document. However, you can change this by using the `setAlwaysPutTocFirst(false)`.

```java
final Pdf pdf = new Pdf(new WrapperConfig(WrapperConfig.findExecutable()));

pdf.addCoverFromString("<html><head><meta charset=\"utf-8\"></head><h1>CoverPage</h1></html>");
pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h2>Page 1</h2></html>");
pdf.addToc(); // ToC will be placed at the beginning of the document by default, regardless of the order of addition
pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h2>Page 2</h2></html>");

pdf.getPDF(); // ToC forced to go first, then in order of addition: cover, page 1 and page 2 
```

#### Changing ToC position

By default, the ToC is always placed at the beginning of the document. You can change this by using the `setAlwaysPutTocFirst(false)`:

```java
final WrapperConfig config = new WrapperConfig(WrapperConfig.findExecutable());
config.setAlwaysPutTocFirst(false);
final Pdf pdf = new Pdf(config);

pdf.addCoverFromString("<html><head><meta charset=\"utf-8\"></head><h1>CoverPage</h1></html>");
pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h2>Page 1</h2></html>");
pdf.addToc(); // ToC will be placed according to the order of addition as config.setAlwaysPutTocFirst(false) is set
pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h2>Page 2</h2></html>");

pdf.getPDF(); // Follows order of addition: cover, page 1, ToC, and page 2
```

### Xvfb Support

```java
XvfbConfig xc = new XvfbConfig();
xc.addParams(new Param("--auto-servernum"), new Param("--server-num=1"));

WrapperConfig wc = new WrapperConfig(WrapperConfig.findExecutable());
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

Known issues
------------

**Output of wkhtmltopdf is being added to resulting pdf** ([Issue #19](https://github.com/jhonnymertz/java-wkhtmltopdf-wrapper/issues/19))
- Starting from 1.1.10-RELEASE version, there is a method `saveAsDirect(String path)`, which executes wkhtmltopdf passing the `path` as output for wkhtmltopdf, instead of the standard input `-`. This saves the results directly to the specified file `path`.

**Because this library relies on `wkhtmltopdf`, it does not support concurrent PDF generations.**

License
------------
This project is available under MIT Licence.
