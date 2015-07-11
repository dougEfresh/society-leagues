package com.society.leagues.resource.client;

import java.io.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ScoresheetResource {

    @RequestMapping(value = "/api/scoresheet/challenge", produces =  "application/pdf", method = RequestMethod.GET)
    void challengeScoreSheet(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException, DocumentException {
        ClassPathResource resource = new ClassPathResource("/Challenge_Scoresheet.pdf");
        InputStream in = resource.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = in.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        PdfReader reader = new PdfReader(buffer.toByteArray());
        //PdfWriter pdfWriter = PdfWriter.getInstance()
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("/tmp/p.pdf"));
        PdfContentByte over = writer.getDirectContent();

        httpServletResponse.getOutputStream().write(buffer.toByteArray());
    }
}
