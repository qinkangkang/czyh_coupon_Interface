package com.innee.czyhInterface.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.innee.czyhInterface.entity.TCustomerLevel;

public interface CustomerLevelDAO
		extends JpaRepository<TCustomerLevel, String>, JpaSpecificationExecutor<TCustomerLevel> {

	@Query("select t from TCustomerLevel t order by t.flevel asc")
	List<TCustomerLevel> getByCustomerLevelList();

	// @Modifying
	// @Query("update TCustomerLevel t set t.fpoint = (t.fpoint + ?2) where
	// t.fcustomerId = ?1")
	// void updatePoint(String customerId, int point);

}