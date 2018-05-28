/*
 *  Copyright (C) 2018 kiz, University Ulm
 */
package com.github.jhonnymertz.wkhtmltopdf.wrapper;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author kdenzel
 */
public class CleanUpTempFilesTest {
    
    @Test
    public void test(){
        Pdf pdf = new Pdf();
        pdf.addPageFromString("<!DOCTYPE html><head><title>title</title></head><body><p>TEST</p></body>");
        try {
            byte[] pdfAsByteArray = pdf.getPDF();
        } catch(Exception ex){
            Assert.fail(ex.getMessage());
        }
    }
    
}
