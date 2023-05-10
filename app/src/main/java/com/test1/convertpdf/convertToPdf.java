package com.test1.convertpdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class convertToPdf {

    private String filePath;
    public convertToPdf(String filePath) {
        this.filePath = filePath;
    }

    public convertToPdf() {
    }

    public String jpgToPdf(File pdfFile, String imageFilePath) {
        try {
            Document document = new Document(PageSize.A4, 0, 0, 0, 0);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            Image image = Image.getInstance(imageFilePath);
            image.setRotationDegrees(270); //images are always flipped for some reason
            image.scaleToFit(image.getWidth()+1f, image.getHeight()+1f);
            //image.setAbsolutePosition(0,0);

            //when you call scale to fit, height and width change so you have to explicitly call getScaledHeight and getScaledWeight
            Rectangle size = new Rectangle(image.getScaledWidth()+1f, image.getScaledHeight()+1f);
            document.setPageSize(size);


            document.newPage();
            document.add(image);
            document.close();
            writer.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return pdfFile.getAbsolutePath();
    }
}
