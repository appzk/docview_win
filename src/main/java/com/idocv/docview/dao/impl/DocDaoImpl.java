package com.idocv.docview.dao.impl;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.idocv.docview.common.Field;
import com.idocv.docview.common.Paging;
import com.idocv.docview.dao.DocDao;
import com.idocv.docview.po.DocPo;


@Repository
public class DocDaoImpl implements DocDao, InitializingBean {

	@Resource
	private DataSource dataSource;

	@Override
	public void afterPropertiesSet() throws Exception {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			stmt.execute("create table IF NOT EXISTS " + Field.TABLE_NAME
					+ " (" + Field.FIELD_ID + " string, " + Field.FIELD_NAME
					+ " string, " + Field.FIELD_SIZE + " string, "
					+ Field.FIELD_CTIME + " long, " + Field.FIELD_URL
					+ " string)");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void add(DocPo doc) {
		String sql = "insert into " + Field.TABLE_NAME + "(" + Field.FIELD_ID
				+ "," + Field.FIELD_NAME + "," + Field.FIELD_SIZE + ","
				+ Field.FIELD_CTIME + "," + Field.FIELD_URL + ") values(?,?,?,?,?)";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstat = conn.prepareStatement(sql);
			pstat.setString(1, doc.getRid());
			pstat.setString(2, doc.getName());
			pstat.setString(3, doc.getSize());
			pstat.setLong(4, doc.getCtime());
			pstat.setString(5, doc.getUrl());
			pstat.executeUpdate();
			pstat.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean delete(String rid) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			stmt.execute("delete from " + Field.TABLE_NAME + " where id='" + rid + "'");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public boolean updateUrl(String rid, String url) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			stmt.execute("update " + Field.TABLE_NAME + " set url='" + url + "' where id='" + rid + "'");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public DocPo get(String rid) {
		Connection conn = null;
		DocPo po = null;
		try {
			conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from " + Field.TABLE_NAME + " where id='" + rid + "'");
			if (rs.next()) {
				po = new DocPo();
				po.setRid(rs.getString(Field.FIELD_ID));
				po.setName(rs.getString(Field.FIELD_NAME));
				po.setSize((rs.getString(Field.FIELD_SIZE)));
				po.setCtime((rs.getLong(Field.FIELD_CTIME)));
				po.setUrl(rs.getString(Field.FIELD_URL));
			}
			return po;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return po;
	}

	@Override
	public DocPo getUrl(String url) {
		Connection conn = null;
		DocPo po = null;
		try {
			conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from " + Field.TABLE_NAME + " where url='" + url + "'");
			if (rs.next()) {
				po = new DocPo();
				po.setRid(rs.getString(Field.FIELD_ID));
				po.setName(rs.getString(Field.FIELD_NAME));
				po.setSize((rs.getString(Field.FIELD_SIZE)));
				po.setCtime((rs.getLong(Field.FIELD_CTIME)));
				po.setUrl(rs.getString(Field.FIELD_URL));
			}
			return po;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return po;
	}

	@Override
	public Paging<DocPo> list(int start, int length) {
		Paging<DocPo> result = null;
		List<DocPo> data = new ArrayList<DocPo>();
		String sql = "select * from " + Field.TABLE_NAME + " order by " + Field.FIELD_CTIME + " desc limit " + start + ", " + length;
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				DocPo po = new DocPo();
				po.setRid(rs.getString(Field.FIELD_ID));
				po.setName(rs.getString(Field.FIELD_NAME));
				po.setSize((rs.getString(Field.FIELD_SIZE)));
				po.setCtime((rs.getLong(Field.FIELD_CTIME)));
				data.add(po);
			}
			result = new Paging<DocPo>(data, count());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	@Override
	public int count() {
		Connection conn = null;
		String sql = "select count(*) from " + Field.TABLE_NAME;
		try {
			conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
}
