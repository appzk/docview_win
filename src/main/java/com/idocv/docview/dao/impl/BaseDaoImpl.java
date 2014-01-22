package com.idocv.docview.dao.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.BaseDao;
import com.mongodb.DB;

@Repository
public class BaseDaoImpl implements BaseDao {

	@Resource
	protected DB db;

}