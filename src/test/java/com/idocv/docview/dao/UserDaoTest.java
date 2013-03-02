package com.idocv.docview.dao;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.idocv.docview.exception.DBException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/beans_context.xml" })
public class UserDaoTest {

	@Resource
	private UserDao userDao;

	@Test
	public void testAdd() {
		try {
			String uid = "51322f9c2a88f3f6c4278201";
			String sid = userDao.addSid(uid);
			System.err.println("Add " + sid + " -> " + uid);
		} catch (DBException e) {
			e.printStackTrace();
		}
	}
}