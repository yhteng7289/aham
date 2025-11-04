package com.pivot.aham.api.web;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.pivot.aham.common.core.support.context.ApplicationContextHolder;
import com.pivot.aham.common.core.util.InstanceUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPdf {
    public static final String sourceFolder = "/Users/jinling.cui/Documents/IdeaProjects/pivot/aham/aham-common/src/test/resources/";
    public static final String destinationFolder = "/Users/jinling.cui/Documents/IdeaProjects/pivot/aham/aham-common/src/test/resources/";

    @Test
    public void test() throws IOException {
        PdfWriter pdfWriter = new PdfWriter(new File(destinationFolder + "hello_paragraph.pdf"));
        TemplateEngine templateEngine = (TemplateEngine) ApplicationContextHolder.getBean("templateEngine");
        Context context = new Context();
        context.setVariables(InstanceUtil.newHashMap("exMsg","111111112222"));
        String body = templateEngine.process("ExceptionEmail",context);

        HtmlConverter.convertToPdf(body,pdfWriter);
    }

    public void testHello() throws IOException {
        HtmlConverter.convertToPdf(new File(sourceFolder + "hello_paragraph.html"), new File(destinationFolder + "hello_paragraph.pdf"));
    }
}
