package com.example.payments.service;

import com.example.payments.model.Payment;
import com.example.payments.util.NumberToWordsConverter; // Import the utility
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class InvoiceGeneratorService {

    private static final String PDF_DIRECTORY = "pdf/";
    private static final BaseColor TITLE_COLOR = new BaseColor(0, 102, 204); // Blue color
    private static final BaseColor HEADER_COLOR = new BaseColor(34, 139, 34); // Forest green
    private static final BaseColor TOTAL_COLOR = new BaseColor(255, 140, 0); // Dark orange
    private static final BaseColor SIGNATURE_COLOR = new BaseColor(128, 0, 128); // Purple

    public void generateInvoice(Payment payment) {
        try {
            Path path = Paths.get(PDF_DIRECTORY);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String pdfFileName = PDF_DIRECTORY + "Payment_" + payment.getInvoicenumber() + ".pdf";

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFileName));
            document.open();

            // Title
            document.add(new Paragraph("DETAILS", FontFactory.getFont(FontFactory.TIMES_ITALIC, 15, TITLE_COLOR)));
            document.add(new Paragraph(" "));
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            PdfPCell receiverCell = new PdfPCell(new Paragraph("Seller Info: \n" + payment.getReceiverInfo() + "\nSeller A/C: " + payment.getTargetBankAccount()));
            PdfPCell buyerCell = new PdfPCell(new Paragraph("Buyer Info: \n" + payment.getBuyerInfo() + "\nBuyer A/C: " + payment.getSourceBankAccount()));
            receiverCell.setPadding(10);
            buyerCell.setPadding(10);
            infoTable.addCell(receiverCell);
            infoTable.addCell(buyerCell);
            document.add(infoTable);

            document.add(new Paragraph(" "));

            // P.O. Number
            document.add(new Paragraph("P.O. NO: " + payment.getPonumber(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, HEADER_COLOR)));
            document.add(new Paragraph(" "));


            PdfPTable itemTable = new PdfPTable(4);
            itemTable.setWidthPercentage(100);
            itemTable.addCell(new Paragraph("Product Name", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, HEADER_COLOR)));
            itemTable.addCell(new Paragraph("Quantity", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, HEADER_COLOR)));
            itemTable.addCell(new Paragraph("Unit Price", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, HEADER_COLOR)));
            itemTable.addCell(new Paragraph("Total", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, HEADER_COLOR)));

            double totalAmount = 0;
            for (Payment.Item item : payment.getItems()) {
                double itemTotal = item.getAmount() * item.getQuantity();
                totalAmount += itemTotal;

                itemTable.addCell(item.getItemName());
                itemTable.addCell(String.valueOf(item.getQuantity()));
                itemTable.addCell(String.valueOf(item.getAmount()));
                itemTable.addCell(String.valueOf(itemTotal));
            }

            PdfPCell emptyCell1 = new PdfPCell();
            PdfPCell emptyCell2 = new PdfPCell();
            emptyCell1.setBorder(Rectangle.NO_BORDER);
            emptyCell2.setBorder(Rectangle.NO_BORDER);
            itemTable.addCell(emptyCell1);
            itemTable.addCell(emptyCell2);
            itemTable.addCell(new PdfPCell(new Paragraph("Total", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, TOTAL_COLOR))));
            itemTable.addCell(new PdfPCell(new Paragraph(String.valueOf(totalAmount), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, TOTAL_COLOR))));
            document.add(itemTable);

            double tdsAmount = (totalAmount * payment.getTds()) / 100;
            double finalAmount = totalAmount - tdsAmount;
            String finalAmountInWords = NumberToWordsConverter.convert((int) Math.floor(finalAmount)) + " Rupees";

            PdfPTable tdsTable = new PdfPTable(4);
            tdsTable.setWidthPercentage(100);
            tdsTable.addCell(emptyCell1);
            tdsTable.addCell(emptyCell2);
            tdsTable.addCell(new Paragraph("TDS(tax) (" + payment.getTds() + "%)", FontFactory.getFont(FontFactory.HELVETICA, 12, HEADER_COLOR)));
            tdsTable.addCell(String.valueOf(tdsAmount));

            tdsTable.addCell(emptyCell1);
            tdsTable.addCell(emptyCell2);
            tdsTable.addCell(new Paragraph("Final Amount", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, TOTAL_COLOR)));
            tdsTable.addCell(new PdfPCell(new Paragraph(String.valueOf(finalAmount), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, TOTAL_COLOR))));

            document.add(tdsTable);


            document.add(new Paragraph("Final Amount in Words: " + finalAmountInWords, FontFactory.getFont(FontFactory.HELVETICA, 12, TOTAL_COLOR)));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Authorized Signature: ", FontFactory.getFont(FontFactory.HELVETICA, 12, SIGNATURE_COLOR)));

            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}
