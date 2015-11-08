package com.idocv.test;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

public class Test {
	public static void main(String[] args) {
		try {
			Date startDate = new Date(114, 0, 8);
			Date endDate = new Date(115, 11, 8);

			// year
			Calendar startCalendar = DateUtils.toCalendar(startDate);
			int startYear = startCalendar.get(Calendar.YEAR);
			System.out.println(startYear);

			Calendar endCalendar = DateUtils.toCalendar(endDate);
			int endYear = endCalendar.get(Calendar.YEAR);
			System.out.println(endYear);

			DateFormat dfDatePath = new SimpleDateFormat("yyyy" + File.separator + "MMdd");
			for (Date increDate = startDate; increDate.before(endDate); increDate = DateUtils.addDays(increDate, 1)) {
				String path = dfDatePath.format(increDate);
				System.out.println(path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}