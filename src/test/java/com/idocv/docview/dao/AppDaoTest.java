package com.idocv.docview.dao;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.AppPo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/beans_context.xml" })
public class AppDaoTest {

	@Resource
	private AppDao appDao;

	@Test
	public void testAdd() {
		try {
			String id = "doc";
			String name = "I Doc View";
			String key = "doctest";
			String phone = "18611898831";
			appDao.add(id, name, key, phone);
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDelete() {
		try {
			String id = "doc";
			appDao.delete(id);
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testList() {
		try {
			List<AppPo> list = appDao.list(0, 0);
			if (null != list) {
				for (AppPo po : list) {
					System.err.println(po);
				}
			}
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

}
