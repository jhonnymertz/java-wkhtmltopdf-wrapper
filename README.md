Java WkHtmlToPdf Wrapper
=========

A Java based wrapper for the [wkhtmltopdf](http://wkhtmltopdf.org/) command line tool. As the name implies, it uses WebKit to convert HTML documents to PDFs.

Requirements
------------
**[wkhtmltopdf](http://wkhtmltopdf.org/) must be installed and working on your system.**

Usage
------------
```
Pdf pdf = new Pdf();
pdf.addHtmlInput("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");
```

The `wkhtmltopdf` shell command accepts different types of options such as global, page, headers and footers, and toc. Please see `wkhtmltopdf -H` for a full explanation. All options are passed as array, for example:

Options can be defined through an abstract param object:

```
Pdf pdf = new Pdf();
pdf.addParam(new Param("--enable-javascript"), new Param("--html-header", "file:///header.html"));
```

Wrapper options
-------------------
TODO

Error handling
------------
TODO

License
------------
This project is available under MIT Licence.
