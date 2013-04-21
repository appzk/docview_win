package com.idocv.docview.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.ConvertService;
import com.idocv.docview.service.PreviewService;

@Service
public class ConvertServiceImpl implements ConvertService {

	private static final ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	@Resource
	private PreviewService previewService;

	private String rid;

	public ConvertServiceImpl() {
		System.out.println("ConvertServiceImpl constructor...");
	}

	public ConvertServiceImpl(PreviewService previewService, String rid) {
		System.out.println("ConvertServiceImpl(String rid) constructor...");
		this.previewService = previewService;
		this.rid = rid;
	}

	@Override
	public Boolean call() throws Exception {
		previewService.convert(rid);
		return true;
	}


	@Override
	public Future<Boolean> convert(String rid) throws DocServiceException {
		ConvertService convertService = new ConvertServiceImpl(previewService, rid);
		return es.submit(convertService);
	}

}