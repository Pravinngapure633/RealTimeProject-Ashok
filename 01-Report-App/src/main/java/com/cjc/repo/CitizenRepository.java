package com.cjc.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cjc.entity.CitizenPlan;
@Repository
public interface CitizenRepository extends JpaRepository<CitizenPlan,Integer> {

	//Hql query
	@Query("select distinct (planName) from CitizenPlan")
	public List<String> getPlanNames();
	

	@Query("select distinct (planStatus) from CitizenPlan")
	public List<String> getPlanStatus();
	
	
	
}
