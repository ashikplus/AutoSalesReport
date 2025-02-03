package com.ashik.AutoSalesReport;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class SalesReportService {
	
    private final SalesRepository salesRepository;

    public SalesReportService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }
    
    

    // Runs on the 1st of every month at 00:00 (midnight)
//    
//    @Scheduled(cron = "0 * * * * ?") for every minutes
    @Scheduled(cron = "0 0 0 1 * ?") // for first day of every month
    public void generateMonthlyReport() throws DocumentException {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfLastMonth = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());
        
        System.out.println("firstDayOfLastMonth "+ firstDayOfLastMonth);
        System.out.println("lastDayOfLastMonth "+ lastDayOfLastMonth);

        List<Sale> sales = salesRepository.findByDateBetween(firstDayOfLastMonth, lastDayOfLastMonth);
        
        if(sales.isEmpty()) {
        	System.out.println("sale is empty!..........");
        }
        BigDecimal totalSales = sales.stream().map(sale -> sale.getAmount()).reduce(BigDecimal.ZERO, (subtotal, amount) -> subtotal.add(amount)); //.reduce(BigDecimal.ZERO, BigDecimal::add);
//        sales.stream().map(s->s.getAmount()).re;

        String reportPath = "monthly_sales_report_" + firstDayOfLastMonth.getMonth() + "_" + firstDayOfLastMonth.getYear() + ".pdf";
        
        createPdfReport(reportPath, sales, totalSales, firstDayOfLastMonth);
    }

    private void createPdfReport(String reportPath, List<Sale> sales, BigDecimal totalSales, LocalDate reportDate) throws DocumentException {
        try {
            
        	Document document = new Document();
        	PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(reportPath));
        	
        	document.open();
        	Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        	
        	 Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            // ✅ Pass both text and font to Paragraph constructor
            Paragraph title = new Paragraph("Sales Report for " + reportDate.getMonth() + " " + reportDate.getYear(), titleFont);
        	
            document.add(title);
            document.add(new Paragraph("\n"));

            PdfPTable labelValueTable = new PdfPTable(3);
            
            labelValueTable.addCell(new PdfPCell(new Paragraph("ID", boldFont))); //.add(new Paragraph("ID")).setBold());
            labelValueTable.addCell(new PdfPCell(new Paragraph("Product Name", boldFont)));
            labelValueTable.addCell(new PdfPCell(new Paragraph("Amount", boldFont)));

            // Table Data
            for (Sale sale : sales) {
            	labelValueTable.addCell(new PdfPCell(new Paragraph(String.valueOf(sale.getId()))));
            	labelValueTable.addCell(new PdfPCell(new Paragraph(sale.getProduct())));
            	labelValueTable.addCell(new PdfPCell(new Paragraph("$" + sale.getAmount())));
            }

            // Total Sales
            document.add(labelValueTable);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Total Sales: $" + totalSales, boldFont));

            document.close();
            System.out.println("Monthly Sales Report PDF Generated: " + reportPath);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
