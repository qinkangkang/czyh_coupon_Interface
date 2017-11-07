package com.innee.czyhInterface.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.innee.czyhInterface.entity.TEventBonus;

public interface EventBonusDAO extends JpaRepository<TEventBonus, String>, JpaSpecificationExecutor<TEventBonus> {

	@Query("select t from TEventBonus t where t.fstatus =20 order by t.forder")
	List<TEventBonus> getByCustomerLevelList();
}