package com.idocv.docview.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ProcessUtil.class);

	public static final List<String> serviceNameList = new ArrayList<String>();

	static {
		serviceNameList.add("word2html.exe");
		serviceNameList.add("excel2html.exe");
		serviceNameList.add("ppt2jpg.exe");
		serviceNameList.add("WINWORD.EXE");
		serviceNameList.add("EXCEL.EXE");
		serviceNameList.add("POWERPNT.EXE");
	}

	/**
	 * Get all process list
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<String> getAllProcesses() throws Exception {
		List<String> processList = new ArrayList<String>();
		String cmds[] = { "cmd", "/c", "tasklist" };
		Process proc = Runtime.getRuntime().exec(cmds);
		BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(proc.getInputStream(), "gbk"));
		String line;
		while ((line = bufferedreader.readLine()) != null) {
			processList.add(line);
		}
		bufferedreader.close();
		return processList;
	}
	
	/**
	 * Get all process list with pid
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Map<Integer, String> getAllProcessesWithPid() throws Exception {
		Map<Integer, String> processMap = new TreeMap<Integer, String>();
		String procRegex = "(\\S+)(\\s+)(\\d+)(\\s+)(.*)";
		String cmds[] = { "cmd", "/c", "tasklist" };
		Process proc = Runtime.getRuntime().exec(cmds);
		BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(proc.getInputStream(), "gbk"));
		String line;
		while ((line = bufferedreader.readLine()) != null) {
			String curPid = line.replaceFirst(procRegex, "$3");
			curPid = (null != curPid && curPid.matches("\\d+")) ? curPid : "-1";
			processMap.put(Integer.valueOf(curPid), line);
		}
		bufferedreader.close();
		return processMap;
	}

	/**
	 * Get process by service name list
	 * 
	 * @param processNameList
	 * @return Map of pid -> service name
	 * @throws Exception
	 */
	public static Map<Integer, String> getProcessByNameList(List<String> processNameList) throws Exception {
		Map<Integer, String> runningProcessMap = new HashMap<Integer, String>();
		List<String> processList = getAllProcesses();
		if (null == processList || processList.isEmpty()) {
			return runningProcessMap;
		}
		for (String process : processList) {
			for (String processName : processNameList) {
				if (process.length() > 0 && process.contains(processName)) {
					String procRegex = "(\\S+)(\\s+)(\\d+)(\\s+)(.*)";
					String curServiceName = process.replaceFirst(procRegex, "$1");
					String curPid = process.replaceFirst(procRegex, "$3");
					runningProcessMap.put(Integer.valueOf(curPid), curServiceName);
				}
			}
		}
		return runningProcessMap;
	}

	/**
	 * Kill process by service name
	 * 
	 * @param serviceName
	 * @throws Exception
	 */
	public static void killProcessByServiceName(String serviceName) throws Exception {
		String cmds[] = { "cmd", "/c", "taskkill", "/T", "/F", "/IM", serviceName };
		Runtime.getRuntime().exec(cmds);
	}

	/**
	 * Kill one process by pid
	 * 
	 * @param pid
	 * @throws Exception
	 */
	public static void killProcessByPID(Integer pid) throws Exception {
		if (null == pid || pid <= 0) {
			return;
		}
		Set<Integer> pids = new HashSet<Integer>();
		pids.add(pid);
		killProcessByPIDs(pids);
	}

	/**
	 * Kill processes by pid(s)
	 * 
	 * @param pids
	 * @throws Exception
	 */
	public static void killProcessByPIDs(Set<Integer> pids) throws Exception {
		if (null == pids || pids.isEmpty()) {
			return;
		}
		List<String> cmdList = new ArrayList<String>();
		cmdList.add("cmd");
		cmdList.add("/c");
		cmdList.add("taskkill");
		cmdList.add("/T");
		cmdList.add("/F");
		for (Integer pid : pids) {
			cmdList.add("/PID");
			cmdList.add("" + pid);
		}
		logger.warn("[IDOCV] Killing process(s): " + pids);
		Runtime.getRuntime().exec(cmdList.toArray(new String[0]));
	}
}