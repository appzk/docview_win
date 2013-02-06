package idocv.start.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * TT服务停止线程
 * @author yxy
 *
 */
public class TTServerStop {
	
	private static final Logger logger = Logger.getLogger(TTServerStop.class);
	
	public static void main(String[] args){
		Properties prop = new Properties();
		try {
			//读取classpath下的文件
			InputStream is = TTServerStop.class.getResourceAsStream("/server_params.properties");
			//加载propertis配置文件
			prop.load(is);
			is.close();
			int port = Integer.parseInt(prop.getProperty("tt.start.stopport"));//停止端口
			Socket s = new Socket("127.0.0.1", port);
			logger.info("*** sending jetty stop request.");
			OutputStream out = s.getOutputStream();
			out.write((prop.getProperty("tt.start.stopkey")+"\n").getBytes());//停止key
			out.flush();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IOUtils.copyLarge(s.getInputStream(), bos);
			logger.info(new String(bos.toByteArray()));//服务端返回的相应内容
			System.out.println("***************** http server stopped. "+new String(bos.toByteArray())+"*****************");
			s.close();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}
}
