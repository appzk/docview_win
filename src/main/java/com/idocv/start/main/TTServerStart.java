package com.idocv.start.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.GzipHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.xml.sax.SAXException;

/**
 * TT 服务启动类，负责启动web Server服务
 * 服务使用Jetty为容器，采用Spring 3中的Java Config方式通过TTServerConfig类进行依赖注入配置
 * 参数文件参见classpath:server_params.properties参数配置
 * @author yxy
 *
 */
public class TTServerStart {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(TTServerStart.class);

	public static AnnotationConfigApplicationContext cntxt;
	private int stopPort;//停止端口号
	private int startPort;//启动端口号
	private String stopKey;//停止端口发送的key
	private Server server;
	private String warPath;
	private int maxThreads = 1000;
	private boolean isGzip = false;
	private long startTime;//服务器启动时间
	public static TTServerStart srvStart;//全局唯一Start类

	
	public void setStopKey(String stopKey) {
		this.stopKey = stopKey;
	}

	public void setStopPort(int stopPort) {
		this.stopPort = stopPort;
	}

	
	public void setStartPort(int startPort) {
		this.startPort = startPort;
	}
	
	public void setWarPath(String warPath) {
		this.warPath = warPath;
	}
	
	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public void setGzip(boolean isGzip) {
		this.isGzip = isGzip;
	}

	private Server jettyServer(String warPath) {
		Server server = new Server();
		server.setSendServerVersion(true);
		server.setStopAtShutdown(true);
		server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 1000000);

		QueuedThreadPool pool = new QueuedThreadPool();
		pool.setMaxThreads(maxThreads);
		pool.setMinThreads(128);
		//pool.setMaxQueued(50);
		server.setThreadPool(pool);

		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(startPort);
		int acc = Runtime.getRuntime().availableProcessors();
		logger.info("the system availableProcessors is "+acc);
		connector.setAcceptors(Math.max(1,acc+1));//监听read事件的线程数 •默认值是 1 对于NIO 来说，设置为(处理器内核数+1)比较合适
		connector.setMaxIdleTime(60000); //表示连接最大空闲时间默认值是 200000，一般这个值都太大了 典型值 3000 
		server.setConnectors(new Connector[] { connector });

		WebAppContext war = new WebAppContext();
		war.setMaxFormContentSize(1000000);

		if(warPath!=null){
			war.setWar(warPath);			
		}else{
			war.setWar(this.warPath);
		}
		if(isGzip){
			GzipHandler gzipHandler = new GzipHandler();
			gzipHandler.setMimeTypes("text/javascript,application/json,text/html,text/css,text/plain,image/gif,image/png,image/jpg,image/jpeg,image/bmp");
			gzipHandler.setMinGzipSize(50);

			war.setHandler(gzipHandler);
		}
		war.setContextPath("/");
		server.setHandler(war);
		this.server = server;
		return this.server;
	}	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//设置Jetty默认日志目录
		System.setProperty("jetty.log", "logs");
		System.setProperty("org.eclipse.jetty.server.Request.maxFormContentSize", "1000000");
		cntxt = new AnnotationConfigApplicationContext(TTServerConfig.class);
		srvStart = cntxt.getBean(TTServerStart.class);
		try {
			Thread monitor = new MonitorThread(srvStart.stopPort);
			monitor.start();//启动管理线程类

			String warPath = null;
			if(args!=null&&args.length>0){
				for(int i=0;i<args.length;i++){
					if(args[i].equals("-w")){  //update by zwg 20110816  启动脚本获取参数 -w 错误
						warPath = args[++i];
					}
				}
			}
			
			srvStart.jettyServer(warPath).start();//服务启动
			logger.info("connector--"
					+ java.util.Arrays.toString(srvStart.server.getConnectors()));
			logger.info("threadPool--" +((QueuedThreadPool)srvStart.server.getThreadPool()).toString());
			logger.info("ChildHanders--" + java.util.Arrays.toString(srvStart.server.getChildHandlers()));
			logger.info("maxFormContentSize:" + srvStart.server.getAttribute("org.eclipse.jetty.server.Request.maxFormContentSize"));
			logger.info("*** Server started , current Time: " + new Date()+" ***");
			System.out.println("***************** Server started , current Time: " + new Date()+" *****************");
			srvStart.startTime = System.currentTimeMillis();
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * 后台管理线程
	 * @author yxy
	 *
	 */
	private static class MonitorThread extends Thread {
		private ServerSocket socket;

		public MonitorThread(int port) {
			setDaemon(true);
			setName("StopMonitor");

			try {
				socket = new ServerSocket(port);
			} catch (UnknownHostException e) {
				logger.error(e.getMessage(), e);
				throw new RuntimeException(e);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}

		@Override
		public void run() {
			logger.info("*** stop server thread started, port: " + socket.getLocalPort()+" ***");
			System.out.println("***************** stop server thread started, port: " + socket.getLocalPort()+" *****************");
			Socket accept;
			try {
				while (true) {
					accept = socket.accept();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(accept.getInputStream()));
					String key = reader.readLine();
					if (key == null || !srvStart.stopKey.equals(key)) {
						logger.info("can't stop this server, the key is wrong.");
						accept.getOutputStream().write(
								"can't stop this server, the key is wrong.\n"
										.getBytes());
						accept.getOutputStream().flush();
						accept.close();
						continue;
					}
					break;
				}
				logger.info("*** Stopping server.");
				cntxt.close();
				logger.info("*** http server stopped. ***");
				System.out.println("***************** http server stopped. *****************");
				long ctime = System.currentTimeMillis() - srvStart.startTime;
				long hour = ctime / (1000 * 60 * 60);
				long min = (ctime - hour * 60 * 60 * 1000) / (1000 * 60);
				float second = (ctime - hour * 60 * 60 - min * 60) / 1000f;
				StringBuilder sinfo = new StringBuilder();
				sinfo.append("*** Server stopped, current Time : ");
				sinfo.append(new Date());
				sinfo.append("*** Running Time : " + hour + " hour, " + min
						+ " minutes, " + second + " second.\n");
				accept.getOutputStream().write(sinfo.toString().getBytes());
				accept.getOutputStream().flush();
				accept.getOutputStream().close();
				logger.info(sinfo.toString());
				System.out.println("***************** http server stopped. "+sinfo.toString()+"*****************");
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new RuntimeException(e);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new RuntimeException(e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}

		}

	}

	/**
	 * 启动类的配置文件
	 * @author yxy
	 *
	 */
	@Configuration
	@ImportResource("classpath:server_params_ctx.xml")
	public static class TTServerConfig {

		private @Value("${tt.start.startport}")
		int startport;
		private @Value("${tt.start.stopport}")
		int stopport;
		private @Value("${tt.start.stopkey}")
		String stopkey;
		private @Value("${tt.start.warPath}")
		String warPath;
		private @Value("${tt.start.maxThreads}")
		int maxThreads;
		private @Value("${tt.start.isGzip}")
		boolean isGzip;

		@Bean
		public TTServerStart serverStart() {
			TTServerStart start = new TTServerStart();
			start.setStopPort(stopport);
			start.setStopKey(stopkey);
			start.setStartPort(startport);
			start.setWarPath(warPath);
			start.setGzip(isGzip);
			start.setMaxThreads(maxThreads);
			return start;
		}

	}
}
