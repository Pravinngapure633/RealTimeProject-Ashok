package com.cjc.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

import com.cjc.entity.CitizenPlan;
import com.cjc.request.SearchRequest;
import com.cjc.service.ReportService;

@Controller
public class ReportController {

	@Autowired
	private ReportService service;
	
	//2
	@PostMapping("/search")
	public String handleSearch(@ModelAttribute ("search") SearchRequest request,Model model) {
		System.out.println(request);
		List<CitizenPlan> plans = service.search(request);
		model.addAttribute("plans",plans);
		
		init(model);
		return "Index";
	}
	
	//1
	@GetMapping("/")
	public String indexPage(Model model) {

		SearchRequest search = new SearchRequest();
		model.addAttribute("search", search);
		init(model);

		return "Index";
	}

	private void init(Model model) {

		model.addAttribute("names",service.getPlanNames());
		model.addAttribute("status",service.getPlanStatus());
	}
	
	@GetMapping("/excel")
	public void excelExport(HttpServletResponse response) throws Exception{
		
		response.setContentType("application/octet-stream");
		response.addHeader("Content-Disposition","attachment;filename=plans.xls");
		service.exportExcel(response);
	}
	
	@GetMapping("/pdf")
	public void pdfExport(HttpServletResponse response) throws Exception{
		
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition","attachment;filename=plans.pdf");
		service.exportPdf(response);
	}
	
	

}
