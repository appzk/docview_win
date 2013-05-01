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
			// add test app
			String id = "test";
			String name = "I Doc View";
			String token = "testtoken";
			String phone = "18611898831";
			String email = "godwin668@gmail.com";
			appDao.add(id, name, token, phone, email);
			System.err.println("Add test app done!");

			// add wev app
			id = "wev";
			name = "wev.cc";
			token = "wevtoken";
			phone = "18611898831";
			appDao.add(id, name, token, phone, email);
			System.err.println("Add wev app done!");

			// add eco app
			id = "eco";
			name = "Economist Chinese Edition";
			token = "ecotoken";
			phone = "18611898831";
			appDao.add(id, name, token, phone, email);
			System.err.println("Add eco app done!");

			// add letong app
			id = "letong";
			name = "乐通科技";
			token = "letongtoken";
			phone = "15025688808";
			email = "1522415420@qq.com";
			appDao.add(id, name, token, phone, email);
			System.err.println("Add letong done!");
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
