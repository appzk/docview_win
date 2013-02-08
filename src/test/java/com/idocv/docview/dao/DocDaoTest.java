package com.idocv.docview.dao;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.DocPo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/beans_context.xml" })
public class DocDaoTest {

	@Resource
	private DocDao docDao;

	@Test
	public void testAdd() {
		try {
			String id = "doc_20130101_0101011abc_doc";
			String appId = "doc";
			String name = "test.doc";
			long size = 1;
			String ext = "doc";
			docDao.add(id, appId, name, size, ext);
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDelete() {
		try {
			String id = "doc_20130101_0101011_doc";
			docDao.delete(id);
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testList() {
		try {
			List<DocPo> list = docDao.list(0, 0);
			if (!CollectionUtils.isEmpty(list)) {
				for (DocPo po : list) {
					System.err.println(po);
				}
			} else {
				System.err.println("Doc NOT found!");
			}
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

}
