package com.idocv.docview.dao;

import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.UserPo;

public interface UserDao {

	UserPo signUp(String appId, String username, String password, String email) throws DBException;

	boolean isExistUsername(String username) throws DBException;

	boolean isExistEmail(String email) throws DBException;
}