Java WkHtmlToPdf Wrapper
=========

A Java based wrapper for the [wkhtmltopdf](http://wkhtmltopdf.org/) command line tool. As the name implies, it uses WebKit to convert HTML documents to PDFs.

Requirements
------------
**[wkhtmltopdf](http://wkhtmltopdf.org/) must be installed and working on your system.**

### Wrapper project dependency
Make sure you have Java Wrapper dependency added to your project.

If you are using Gradle/Maven, see example below:

##### Gradle
In your `build.gradle`:
```groovy
	allprojects {
		repositories {
			maven { url "https://jitpack.io" }
		}
	}
	
	dependencies {
		compile 'com.github.jhonnymertz:java-wkhtmltopdf-wrapper:1.1.4-RELEASE'
	}
```

##### Maven
In your `pom.xml`:
```xml
	<dependencies>
		<dependency>
			<groupId>com.github.jhonnymertz</groupId>
			<artifactId>java-wkhtmltopdf-wrapper</artifactId>
			<version>1.1.4-RELEASE</version>
		</dependency>
	</dependencies>

	<repositories>
		<repository>
			<id>jitpack.io</id>
			<url>https://jitpack.io</url>
		</repository>
	</repositories>
```

Usage
------------
```
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

Xvfb Support
------------
```
XvfbConfig xc = new XvfbConfig();
xc.addParams(new Param("--auto-servernum"), new Param("--server-num=1"));

WrapperConfig wc = new WrapperConfig();
wc.setXvfbConfig(xc);

Pdf pdf = new Pdf(wc);
pdf.addPageFromUrl("http://www.google.com");

pdf.saveAs("output.pdf");
```

License
------------
This project is available under MIT Licence.
