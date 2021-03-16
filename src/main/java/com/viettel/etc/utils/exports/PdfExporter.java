package com.viettel.etc.utils.exports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Class dung de xuat du lieu ra file PDF
 *
 * @author ITSOL-QUANGNV
 * @since 20200704
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Log4j
public class PdfExporter<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfExporter.class);
    @Autowired
    private SpringTemplateEngine templateEngine;

    private String exportFilePath;
    private Font titleFont;
    private Font subTitleFont;
    private Font tableHeaderFont;
    private Font normalFont;

    /**
     *
     * @param document pdf file
     * @param clazz Class cua object export
     * @param datas list object du lieu can export
     * @param isAutoStt Tu dong write cot so thu tu
     * @param headers gia tri header - cach nhau bang dau ;
     * @param headerColor mau nen cua header
     * @param columnRate chia ti le cac cot
     *
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     * @author ITSOL-QuangNV
     * @since 20200704
     */
    public void addTableList(Document document , Class<T> clazz, List<Object> datas, boolean isAutoStt,
                             boolean isWriteSumRow, String sumRowCaption, String[] sumRowDatas,
                             String headers, BaseColor headerColor, int[] columnRate) throws DocumentException, NoSuchMethodException, InvocationTargetException, IllegalAccessException  {

        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        document.add(paragraph);
        PdfPTable table = new PdfPTable(columnRate.length);
        table.setWidthPercentage(100);
        table.setWidths(columnRate);
        //Write header row
        process(table, headers, tableHeaderFont);
        PdfPRow firstRow = table.getRow(0);
        for(PdfPCell cell : firstRow.getCells()){
            cell.setBackgroundColor(headerColor);
        }
        // Write sum row
        if(isWriteSumRow) {
            int colspan = columnRate.length - sumRowDatas.length;
            PdfPCell captionCell = new PdfPCell(new Paragraph(sumRowCaption, tableHeaderFont));
            captionCell.setColspan(colspan);
            captionCell.setPadding(2);
            table.addCell(captionCell);
            for(String item : sumRowDatas){
                PdfPCell dataCell = new PdfPCell(new Paragraph(item, normalFont));
                dataCell.setPadding(2);
                table.addCell(dataCell);
            }
        }


        // Write list data row
        int count = 0;
        for(Object item : datas){
            Method method = clazz.getMethod("toPdfExportFormat");
            String itemWrite = (String)method.invoke(item);
            if(isAutoStt) {
                itemWrite = ++count + ";" + itemWrite;
            }
            process(table, itemWrite, normalFont);
        }
        document.add(table);
    }

    /**
     * write cell to table
     *
     * @param table
     * @param line
     * @param font
     */
    private void process(PdfPTable table, String line, Font font)  {
        StringTokenizer tokenizer = new StringTokenizer(line, ";");
        int c = 0;
        while (tokenizer.hasMoreTokens()) {
            try {
                String cellValue = new String(tokenizer.nextToken().getBytes("UTF-8"));
                if(cellValue == null || "null".equals(cellValue)) {
                    cellValue = "";
                }
                PdfPCell cell = new PdfPCell(new Phrase(cellValue, font));
                cell.setPadding(2);
                table.addCell(cell);
            }catch (Exception exception){
                LOGGER.error("error", exception);
            }
        }
    }

    /**
     * Add metadata
     *
     * @param document
     * @param title
     * @param subject
     */
    public void addMetaData(Document document, String title, String subject) {
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor("Viettel-ETC");
        document.addCreator("ITSOL - Dev");
    }

    /**
     * Add title
     * @param document
     * @param titles
     * @throws DocumentException
     */
    public void addTitlePage(Document document, String... titles)
            throws  DocumentException {
        for(String title : titles) {
            if(title == null || "null".equals(title)) {
                title = "N/A";
            }
            if(title.contains("null")) {
                title = title.replace("null", "N/A");
            }
            Paragraph preface = new Paragraph(title, titleFont);
            preface.setAlignment(Element.ALIGN_CENTER);
            addEmptyLine(preface, 1);
            document.add(preface);
        }
    }

    /**
     * Add sub title
     *
     * @param document
     * @param subTitles
     * @throws DocumentException
     */
    public void addSubTitlePage(Document document, String... subTitles)
            throws  DocumentException {
        for(String title : subTitles) {
            if(title == null || "null".equals(title))
            {
                title = "N/A";
            }
            if(title.contains("null")) {
                title = title.replace("null", "N/A");
            }
            Paragraph preface = new Paragraph(title, subTitleFont);
            preface.setAlignment(Element.ALIGN_CENTER);
            document.add(preface);
        }
    }

    /**
     * Add empty line
     *
     * @param paragraph
     * @param number
     */
    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public void addEmpyLine(Document document, int number) throws DocumentException {
        for (int i = 0; i < number; i++) {
            Paragraph paragraph = new Paragraph();
            paragraph.add(new Paragraph(" "));
            document.add(paragraph);
        }
    }

    /*public void addWartermark(String filePath, String wartermarkStr) throws IOException {
        PdfReader reader = new PdfReader(filePath);
        int n = reader.getNumberOfPages();
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
    }*/

    /**
     * Class dung de add watermark
     */
    /*public static class Watermark extends PdfPageEventHelper {

        protected Phrase watermark = new Phrase("WATERMARK", new Font(Font.FontFamily.TIMES_ROMAN, 60, Font.NORMAL, BaseColor.LIGHT_GRAY));

        @Override
        public void onEndPage(PdfWriter writer, com.itextpdf.text.Document document) {
            PdfContentByte canvas = writer.getDirectContentUnder();
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 298, 421, 45);
        }
    }*/
}
