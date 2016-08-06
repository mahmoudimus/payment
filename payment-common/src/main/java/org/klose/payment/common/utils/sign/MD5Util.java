package org.klose.payment.common.utils.sign;

import org.apache.commons.codec.digest.DigestUtils;
import org.klose.payment.common.exception.GeneralRuntimeException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class MD5Util {

	private static String byteArrayToHexString(byte b[]) {
		StringBuilder resultSb = new StringBuilder();
		for (byte aB : b)
            resultSb.append(byteToHexString(aB));

		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String MD5Encode(String origin, String charsetname) {
		String resultString;
		try {
			resultString = origin;
			MessageDigest md = MessageDigest.getInstance("MD5");
			if (charsetname == null || "".equals(charsetname))
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes()));
			else
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes(charsetname)));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new GeneralRuntimeException(e);
		}
		return resultString;
	}

	private static final String hexDigits[] = 
		{ "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };


	/**
	 * 签名字符串
	 * @param text 需要签名的字符串
	 * @param key 密钥
	 * @param input_charset 编码格式
	 * @return 签名结果
	 */
	public static String sign(String text, String key, String input_charset) {
		text = text + key;
		return DigestUtils.md5Hex(getContentBytes(text, input_charset));
	}

	/**
	 * 签名字符串
	 * @param text 需要签名的字符串
	 * @param sign 签名结果
	 * @param key 密钥
	 * @param input_charset 编码格式
	 * @return 签名结果
	 */
	public static boolean verify(String text, String sign, String key, String input_charset) {
		text = text + key;
		String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
		return mysign.equals(sign);
	}

	/**
	 * @param content
	 * @param charset
	 * @return
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}
}
