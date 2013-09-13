package com.idocv.docview.service;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.vo.UserVo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/beans_context.xml" })
public class UserServiceTest {

	@Resource
	private UserService userService;

	/**
	 * Add app admin
	 */
	@Test
	public void testAddAdmin() {
		try {
			String token = "ecotoken";
			String username = "ecoadmin";
			String password = "ecoadmin";
			String email = "137123093@qq.com";
			UserVo userVo = userService.add(token, username, password, email);
			System.err.println("Add admin " + userVo.getId() + " -> " + userVo);
		} catch (DocServiceException e) {
			e.printStackTrace();
		}
	}

}