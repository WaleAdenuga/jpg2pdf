package com.test1.convertpdf;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Arrays;

public class convertToPdf {

    private String filePath;
    private final String pdf_directory = "/Documents/ConvertPDF";
    private final String pdf_tag = ".pdf";
    private Context context;
    public convertToPdf(String filePath) {
        this.filePath = filePath;
    }

    public convertToPdf(Context context) {
        this.context = context;
    }


    //type is for single or multi capture
    //1 is single
    //0 is multi
    public String jpgToPdf(String imageFilePath, String imageFileName, int requestCode, int type
                            , ArrayList<String>paths) {
        File pdfFile = null;
        try {

            File directory = new File(Environment.getExternalStorageDirectory()+pdf_directory);
            if (!directory.exists()) {
                boolean state = directory.mkdirs();
                if (!state) {
                    Toast.makeText(context, "Making directory failed", Toast.LENGTH_SHORT).show();
                    return null;
                } else {
                    Toast.makeText(context, "Making directory success", Toast.LENGTH_SHORT).show();
                }
            }
            //Delete the PDF file if it already exists
            File[] files = new File(directory.getAbsolutePath()).listFiles();
            Log.d("TAG", ""+ Arrays.toString(files));
            if (!(files == null || files.length==0)) {
                for (File f : files) {
                    if (f.getName().equals(imageFileName+pdf_tag)) {
                        boolean state = f.delete();
                        if (!state) Toast.makeText(context, "File name already exists!", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }
            }

            pdfFile = new File(directory.getAbsolutePath(), imageFileName+pdf_tag);
            Log.d("TAG", "on click convert pdf file " + pdfFile.getAbsolutePath());

            Document document = new Document(PageSize.A4, 0, 0, 0, 0);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            float documentWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            float documentHeight = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();


            switch(type) {
                case 0: //multi
                    for (String s: paths) {
                        addImagePage(requestCode, s, document, documentWidth, documentHeight);
                    }
                    break;
                case 1: //single
                    addImagePage(requestCode, imageFilePath, document, documentWidth, documentHeight);
                    break;
                default:
                    break;
            }
            document.close();
            writer.close();

            //Make newly created pdf file visible by the file manager
            if (context.getPackageManager() != null) {
                MediaScannerConnection.scanFile(context, new String[] {pdfFile.getAbsolutePath()}, new String[]{}, null);
            }

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return pdfFile.getAbsolutePath();
    }

    public void addImagePage(int requestCode, String imageFilePath, Document document, float documentWidth,float documentHeight) {
        try {
            Image image = Image.getInstance(imageFilePath);
            if ((requestCode == 0)) {
                image.setRotationDegrees(270);//images are always flipped when taken on my camera for some reason
            }
            image.scaleToFit(documentWidth, documentHeight);

            //when you call scale to fit, height and width change so you have to explicitly call getScaledHeight and getScaledWeight
            Rectangle size = new Rectangle(image.getScaledWidth()+1f, image.getScaledHeight()+1f);
            document.setPageSize(size);

            document.newPage();
            document.add(image);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

    }
}
