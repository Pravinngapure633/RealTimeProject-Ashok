package com.cjc.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.cjc.entity.CitizenPlan;
import com.cjc.repo.CitizenRepository;
import com.cjc.request.SearchRequest;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
@Service
public class ReqestServiceImpl implements ReportService {

	@Autowired
	private CitizenRepository planRepo;
	 
	@Override
	public List<String> getPlanNames() {
		
		return planRepo.getPlanNames();
	}

	@Override
	public List<String> getPlanStatus() {
		
		return planRepo.getPlanStatus();
	}

	@Override 
	public List<CitizenPlan> search(SearchRequest request) {
		CitizenPlan entity=new CitizenPlan();
		
		if(null!=request.getPlanName() && !"".equals(request.getPlanName())) {
			entity.setPlanName(request.getPlanName());
		}
		
		if(null!=request.getPlanStatus() && !"".equals(request.getPlanStatus())) {
			entity.setPlanStatus(request.getPlanStatus());
		}
		if(null!=request.getGender()&& !"".equals(request.getGender())) {
			entity.setGender(request.getGender());
		}

		if(null!=request.getStartDate()&& !"".equals(request.getStartDate())) {
			
			String startDate = request.getStartDate();
			DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-d");
			
			//converting String to LocalDate
			LocalDate localDate=LocalDate.parse(startDate,formatter);
			entity.setPlanStartDate(localDate);
		}
		//Example is used for prepared query dynamically
		return planRepo.findAll(Example.of(entity));
	}

	@Override
	public boolean exportExcel(HttpServletResponse response) throws Exception {
		
		
		Workbook workBook=new HSSFWorkbook();
		//or
		//Workbook workBook=new XSSFWorkbook();  only extension change .xlss
		Sheet sheet = workBook.createSheet("plans-data");
		Row headerRow = sheet.createRow(0);
		
		headerRow.createCell(0).setCellValue("ID");;
		headerRow.createCell(1).setCellValue("Citizen Name");;
		headerRow.createCell(2).setCellValue("Plan Name");;
		headerRow.createCell(3).setCellValue("Plan Status");;
		headerRow.createCell(4).setCellValue("Plan Start Date");;
		headerRow.createCell(5).setCellValue("Plan End Date");;
		headerRow.createCell(6).setCellValue("Benifit Amt");;
		
		List<CitizenPlan> records = planRepo.findAll();
		
		int dataRowIndex=1;
		for(CitizenPlan plan:records) {
			Row dataRow = sheet.createRow(dataRowIndex);
			dataRow.createCell(0).setCellValue(plan.getCitizenId());
			dataRow.createCell(1).setCellValue(plan.getCitizenName());
			dataRow.createCell(2).setCellValue(plan.getPlanName());
			dataRow.createCell(3).setCellValue(plan.getPlanStatus());
			if(null!=plan.getPlanStartDate()) {
				dataRow.createCell(4).setCellValue(plan.getPlanStartDate()+"");
					
			}else {
				dataRow.createCell(4).setCellValue("N/A");
				
			}
			if(null!=plan.getPlanEndDate()) {
				dataRow.createCell(5).setCellValue(plan.getPlanEndDate()+"");	
			}
			else {
				dataRow.createCell(5).setCellValue("N/A");
				
			}
			if(null!=plan.getBenifitAmt()) {
				dataRow.createCell(6).setCellValue(plan.getBenifitAmt());	
			}
			else {
				dataRow.createCell(6).setCellValue("N/A");
			}
			
			dataRowIndex++;
		}
		ServletOutputStream outputStream = response.getOutputStream();
		workBook.write(outputStream);
		workBook.close();
		return true;
		
		
	}
	
	
	@Override
	public boolean exportPdf(HttpServletResponse response) throws Exception {
		
		Document document=new Document(PageSize.A4);
		
		PdfWriter.getInstance(document, response.getOutputStream());
		
		document.open();
		//creating Font
		//setting fomt style
		Font fontTitle=FontFactory.getFont(FontFactory.TIMES_ROMAN);
		//creating paragraph
		Paragraph p=new Paragraph("Citizen Plan Info",fontTitle);
		
		//aliging the paragraph in document
		p.setAlignment(p.ALIGN_CENTER);
		document.add(p);
		
		PdfPTable table=new PdfPTable(6);
		
		table.setSpacingBefore(6);
		
		table.addCell("ID");
		table.addCell("Citizen Name");
		table.addCell("Plan Name");
		table.addCell("Plan Status");
		table.addCell("Start Date");
		table.addCell("End Date");
		
		List<CitizenPlan> plans = planRepo.findAll();
		
		for(CitizenPlan plan:plans) {
			table.addCell(plan.getCitizenId()+"");
			//or
			//table.addCell(String.valueOf(plan.getCitizenId()));	
			table.addCell(plan.getCitizenName());
			table.addCell(plan.getPlanName());
			table.addCell(plan.getPlanStatus());
			table.addCell(plan.getPlanStartDate()+"");
			table.addCell(plan.getPlanEndDate()+"");
			
		}
		
		document.add(table);
		
		document.close();
		
		
		return false;
	}

}
