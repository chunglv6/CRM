package com.viettel.etc.utils.exports;

import com.itextpdf.text.pdf.BaseFont;
import com.lowagie.text.DocumentException;
import it.innove.play.pdf.PdfUserAgent;
import lombok.extern.log4j.Log4j;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Class dung de xuat du lieu ra file PDF
 *
 * @author ITSOL-QUANGNV
 * @since 20200704
 */
@Log4j
@Component
public class PdfHtmlTemplateExporter {
    @Autowired
    private SpringTemplateEngine templateEngine;

    /**
     *
     * @param html
     * @return filePath
     * @throws IOException
     * @throws DocumentException
     */
    public  String generatePdfFromHtml(String html, String outputPath) throws DocumentException, SAXException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outputPath);
            InputStream input = new ByteArrayInputStream(html.getBytes("UTF-8"));
            Document document = new HtmlDocumentBuilder().parse(input);
            ITextRenderer renderer = new ITextRenderer();
            renderer.getFontResolver().addFont("fonts/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            PdfUserAgent myUserAgent = new PdfUserAgent(renderer.getOutputDevice(), null);
            myUserAgent.setSharedContext(renderer.getSharedContext());
            renderer.getSharedContext().setUserAgentCallback(myUserAgent);
            renderer.setDocument(document, outputPath);
            renderer.layout();
            renderer.createPDF(outputStream);
        }catch (Exception ex) {
            log.error(ex);
        }
        finally {
            if(outputStream != null) {
                try {
                    outputStream.close();
                }catch (Exception exception){
                    log.error(exception);
                }
            }
        }
        return outputPath;
    }

    /**
     *
     * @param templatePath
     * @param dataMap
     * @return html format
     */
    public String parseThymeleafTemplate(String templatePath, HashMap<String, Object> dataMap) {
        Context context = new Context();
        if (dataMap != null) {
            Set set = dataMap.entrySet();

            Map.Entry mentry;
            Object value;
            for(Iterator iterator = set.iterator(); iterator.hasNext(); context.setVariable(mentry.getKey().toString(), value)) {
                mentry = (Map.Entry)iterator.next();
                value = mentry.getValue();
                if (value == null) {
                    value = "";
                }
            }
        }
        return templateEngine.process(templatePath, context);
    }
}
