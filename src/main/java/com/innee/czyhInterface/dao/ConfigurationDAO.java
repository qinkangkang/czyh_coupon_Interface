package com.innee.czyhInterface.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.innee.czyhInterface.entity.TConfiguration;

public interface ConfigurationDAO
		extends JpaRepository<TConfiguration, String>, JpaSpecificationExecutor<TConfiguration> {


	@Query("select t from TConfiguration t")
	List<TConfiguration> findAll();

}