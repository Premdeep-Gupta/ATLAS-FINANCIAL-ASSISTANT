package com.atlas.financial.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    public String parsePdf(File file, int maxChars) {
        if (!file.exists()) {
            return "Error: File does not exist.";
        }
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (text.length() > maxChars) {
                return text.substring(0, maxChars) + "\n\n[Content truncated for memory processing...]";
            }
            return text;
        } catch (Exception e) {
            log.error("Failed to parse PDF {}: {}", file.getName(), e.getMessage());
            return "Error parsing PDF document: " + e.getMessage();
        }
    }

    public String parseSpreadsheet(File file, InputStream inputStream, String filename, int maxRows) {
        String lowerName = filename.toLowerCase();
        try {
            if (lowerName.endsWith(".csv")) {
                return parseCsv(inputStream, maxRows);
            } else if (lowerName.endsWith(".xls") || lowerName.endsWith(".xlsx")) {
                return parseExcel(inputStream, maxRows);
            }
            return "Unsupported file format: " + filename;
        } catch (Exception e) {
            log.error("Failed to parse spreadsheet {}: {}", filename, e.getMessage());
            return "Error parsing spreadsheet: " + e.getMessage();
        }
    }

    private String parseCsv(InputStream inputStream, int maxRows) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < maxRows) {
                sb.append(line).append("\n");
                count++;
            }
        }
        return sb.toString();
    }

    private String parseExcel(InputStream inputStream, int maxRows) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = 0;
            for (Row row : sheet) {
                if (rowCount >= maxRows) break;
                StringBuilder rowSb = new StringBuilder();
                for (Cell cell : row) {
                    rowSb.append(cell.toString()).append(" | ");
                }
                sb.append(rowSb.toString()).append("\n");
                rowCount++;
            }
        }
        return sb.toString();
    }
}
