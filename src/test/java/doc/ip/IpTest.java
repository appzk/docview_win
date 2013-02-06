package doc.ip;

import idocv.docview.common.IpUtil;

public class IpTest {
	public static void main(String[] args) {
		String encodedIp = "2qhl1c1g";
		String ip = IpUtil.decodeIp(encodedIp);
		System.out.println(ip);
	}
}
