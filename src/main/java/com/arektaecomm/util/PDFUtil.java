package com.arektaecomm.util;

import com.arektaecomm.model.Order;
import com.arektaecomm.model.CartItem;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import java.util.List;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;

public class PDFUtil {
    public static File generateInvoice(Order order) throws Exception {
        File invoicesDir = new File("invoices");
        if (!invoicesDir.exists()) {
            invoicesDir.mkdirs(); // Ensure the directory exists
        }
        File out = new File(invoicesDir, "invoice_" + order.getId() + ".pdf");
        PdfWriter writer = new PdfWriter(out);
        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
        Document doc = new Document(pdfDoc);
        doc.add(new Paragraph("Invoice for Order: " + order.getId()));
        doc.add(new Paragraph("User: " + order.getUserId()));
        doc.add(new Paragraph("Status: " + order.getStatus()));
        doc.add(new Paragraph(" "));
        // Table header: Product, Quantity, Price, Subtotal
        Table table = new Table(new float[]{4, 2, 2, 2});
        table.addHeaderCell(new Cell().add(new Paragraph("Product ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Quantity")));
        table.addHeaderCell(new Cell().add(new Paragraph("Price")));
        table.addHeaderCell(new Cell().add(new Paragraph("Subtotal")));
        List<CartItem> items = order.getItems();
        if (items != null) {
            for (CartItem item : items) {
                table.addCell(item.getProductId());
                table.addCell(String.valueOf(item.getQuantity()));
                // For demo, price lookup is not available here; set as N/A
                table.addCell("N/A");
                table.addCell("N/A");
            }
        }
        doc.add(table);
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Total: ৳" + String.format("%.2f", order.getTotal())));
        doc.close();
        return out;
    }
}