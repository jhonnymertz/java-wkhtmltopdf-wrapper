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
		compile 'com.github.jhonnymertz:java-wkhtmltopdf-wrapper:0.0.3-SNAPSHOT'
	}
```

##### Maven
In your `pom.xml`:
```xml
	<dependencies>
		<dependency>
				<groupId>com.github.jhonnymertz</groupId>
				<artifactId>java-wkhtmltopdf-wrapper</artifactId>
				<version>0.0.3-SNAPSHOT</version>
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

pdf.addPage("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>", PageType.htmlAsString);
pdf.addPage("http://www.google.com", PageType.url);

// Add a Table of contents
pdf.addToc();

// The `wkhtmltopdf` shell command accepts different types of options such as global, page, headers and footers, and toc. Please see `wkhtmltopdf -H` for a full explanation.
// All options are passed as array, for example:
pdf.addParam(new Param("--no-footer-line"), new Param("--html-header", "file:///header.html"));
pdf.addParam(new Param("--enable-javascript"));

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
pdf.addPage("http://www.google.com", PageType.url);

pdf.saveAs("output.pdf");
```

License
------------
This project is available under MIT Licence.
