package org.klose.payment.common.utils.sign;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class SHA1withRSAUtils {

	private final static Logger logger = LoggerFactory.getLogger(SHA1withRSAUtils.class);

	/**
	 * 加密
	 * 
	 * @param privatekeyPath
	 *            证书路径
	 * @param privatekeyPassword
	 *            证书密码
	 * @param encoding
	 *            字符编码
	 * @param paramStr
	 *            明文字符串
	 * @return
	 * @throws Exception
	 */
	public static String sign(String privatekeyPath, String privatekeyPassword,
			String encoding, String paramStr) {

		String base64 = "";

		try {
			KeyStore ks = KeyStore.getInstance("PKCS12");

			InputStream ksbufin = new BufferedInputStream(
					SHA1withRSAUtils.class.getResourceAsStream(privatekeyPath));
			char[] keyPwd = privatekeyPassword.toCharArray();
			ks.load(ksbufin, keyPwd);

			PrivateKey priK = (PrivateKey) ks.getKey("test-alias", keyPwd);
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initSign(priK);
			signature.update(paramStr.getBytes(encoding));

			base64 = Base64.encodeBase64String(signature.sign());
		} catch (Exception ex) {
			logger.error("sign failed.", ex);
		}

		return base64;
	}


	/**
	 * 校验签名
	 * @param publicFilePath 证书路径
	 * @param encoding 编码方式
	 * @param paramStr 加密字符串
	 * @param signStr 签名
     * @return
     */
	public static boolean enCodeByCer(String publicFilePath, String encoding,
			String paramStr, String signStr) {

		boolean flag = false;
		try {
			// 获得文件(相对路径)
			InputStream publicStream = SHA1withRSAUtils.class
					.getResourceAsStream(publicFilePath);

			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) cf
					.generateCertificate(publicStream);
			// 获得公钥
			PublicKey pk = cert.getPublicKey();
			// 签名
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initVerify(pk);
			signature.update(paramStr.getBytes(encoding));
			// 解码
			flag = signature.verify(Base64.decodeBase64(signStr));
		} catch (Exception ex) {
			logger.error("verify failed", ex);
		}
		return flag;
	}
	
	public static void main(String[] args) throws Exception{
		
		String errorCode = "0000";
		String orderId = "Q1512030000000202";
		String paramStr = "errorCode="+errorCode+"&orderId="+orderId;
		System.out.println(paramStr);
		String signMsg = SHA1withRSAUtils.sign("/META-INF/zking/99bill[1]-rsa.pfx", 
			"123321", "utf-8", paramStr);
		
		System.out.println(paramStr + "&signMsg=" + signMsg);
	}
	
}
