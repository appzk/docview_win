package idocv.docview.common;

import javax.servlet.http.HttpServletRequest;

public class IpUtil {
	private static byte[] bytes = new byte[] { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z' };

	public static void main(String[] args) {
		String ip = "192.168.51.231";
		System.err.println("Original ip: " + ip);
		String encodedIp = encodeIp(ip);
		System.err.println("encodedIp: " + encodedIp);
		String decodedIp = decodeIp(encodedIp);
		System.err.println("decodedIp: " + decodedIp);
		System.out.println("--- " + decodeIp("j0010o00"));
	}

	public static String encodeIp(String ip) throws IllegalArgumentException {
		if (null == ip
				|| !ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
			throw new IllegalArgumentException("Illegal IP format " + ip
					+ " it should be like 192.168.1.1");
		}
		String[] ipElement = ip.split("\\.");
		String[] ipElements36Base = new String[ipElement.length];
		for (int i = 0; i < ipElement.length; i++) {
			ipElements36Base[i] = get36BaseString(Integer
					.parseInt(ipElement[i]));
		}
		String pre4DigitString = combine4Digits(
				Integer.valueOf(ipElements36Base[0].substring(0, 1)),
				Integer.valueOf(ipElements36Base[1].substring(0, 1)),
				Integer.valueOf(ipElements36Base[2].substring(0, 1)),
				Integer.valueOf(ipElements36Base[3].substring(0, 1)));
		String last4DigitString = ipElements36Base[0].substring(1, 2)
				+ ipElements36Base[1].substring(1, 2)
				+ ipElements36Base[2].substring(1, 2)
				+ ipElements36Base[3].substring(1, 2);
		return last4DigitString + pre4DigitString;
	}

	public static String decodeIp(String encodedIpString)
			throws IllegalArgumentException {
		if (null == encodedIpString || encodedIpString.length() != 8) {
			throw new IllegalArgumentException(
					"String should have 8 characters.");
		}
		String last4DigitString = encodedIpString.substring(0, 4);
		String pre4DigitString = encodedIpString.substring(4, 8);
		String pre2Base12Digits = Integer
				.toBinaryString(parse36BaseString(pre4DigitString.substring(0,
						2)));
		String pre2Base34Digits = Integer
				.toBinaryString(parse36BaseString(pre4DigitString.substring(2,
						4)));
		int preA = Integer.parseInt(
				pre2Base12Digits.length() > 3 ? pre2Base12Digits.substring(0,
						pre2Base12Digits.length() - 3) : "0", 2);
		int preB = Integer.parseInt(
				pre2Base12Digits.length() >= 3 ? pre2Base12Digits.substring(
						pre2Base12Digits.length() - 3,
						pre2Base12Digits.length()) : pre2Base12Digits, 2);
		int preC = Integer.parseInt(
				pre2Base34Digits.length() > 3 ? pre2Base34Digits.substring(0,
						pre2Base34Digits.length() - 3) : "0", 2);
		int preD = Integer.parseInt(
				pre2Base34Digits.length() >= 3 ? pre2Base34Digits.substring(
						pre2Base34Digits.length() - 3,
						pre2Base34Digits.length()) : pre2Base34Digits, 2);

		int a = parse36BaseString(preA + last4DigitString.substring(0, 1));
		int b = parse36BaseString(preB + last4DigitString.substring(1, 2));
		int c = parse36BaseString(preC + last4DigitString.substring(2, 3));
		int d = parse36BaseString(preD + last4DigitString.substring(3, 4));
		return a + "." + b + "." + c + "." + d;
	}

	/**
	 * 合并四个十进制整数为36进制数，四个数均小于等于7
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	private static String combine4Digits(int a, int b, int c, int d)
			throws NumberFormatException {
		if (a < 0 || b < 0 || c < 0 || d < 0 || a > 7 || b > 7 || c > 7
				|| d > 7) {
			throw new NumberFormatException("digits should be within 0 and 7");
		}
		StringBuffer comString = new StringBuffer();
		String aString = Integer.toBinaryString(a);
		String bString = Integer.toBinaryString(b);
		String cString = Integer.toBinaryString(c);
		String dString = Integer.toBinaryString(d);

		if (3 - aString.length() > 0) {
			for (int i = 0; i < 3 - aString.length(); i++) {
				comString.append('0');
			}
		}
		comString.append(aString);

		if (3 - bString.length() > 0) {
			for (int i = 0; i < 3 - bString.length(); i++) {
				comString.append('0');
			}
		}
		comString.append(bString);

		if (3 - cString.length() > 0) {
			for (int i = 0; i < 3 - cString.length(); i++) {
				comString.append('0');
			}
		}
		comString.append(cString);

		if (3 - dString.length() > 0) {
			for (int i = 0; i < 3 - dString.length(); i++) {
				comString.append('0');
			}
		}
		comString.append(dString);
		return get36BaseString(Integer.parseInt(comString.substring(0, 6), 2))
				+ get36BaseString(Integer.parseInt(comString.substring(6, 12),
						2));
	}

	/**
	 * 0-255间的整数转换为36进制的两位字符串
	 * 
	 * @param num
	 * @return
	 */
	private static String get36BaseString(int num) throws NumberFormatException {
		if (num < 0 || num > 255) {
			throw new NumberFormatException("Number must within [0, 255].");
		}
		StringBuffer sb = new StringBuffer();
		while (num >= bytes.length) {
			sb.insert(0, (char) bytes[num % bytes.length]);
			num /= bytes.length;
		}
		sb.insert(0, (char) bytes[num]);
		if (sb.length() < 2) {
			sb.insert(0, '0');
		}
		return sb.toString();
	}

	/**
	 * 将36进制字符串转换为10进制数字
	 * 
	 * @param s
	 * @return
	 * @throws IllegalArgumentException
	 */
	private static int parse36BaseString(String s)
			throws IllegalArgumentException {
		if (null == s) {
			return 0;
		}
		int sum = 0;
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			for (int j = 0; j < bytes.length; j++) {
				if (chars[i] == (char) bytes[j]) {
					sum += Math.pow(36, chars.length - i - 1) * j;
					break;
				}
			}
		}
		return sum;
	}

	/**
	 * 从HttpServletRequest中获取ip地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		if (null == request) {
			return null;
		}
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("REMOTE-HOST");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}