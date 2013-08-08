package com.enix.hoken.encode;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import android.util.Log;

/**
 * 加密解密类
 * 
 * @author gumc
 * 
 */
public class Encryp {
	// KeyGenerator 提供对称密钥生成器的功能，支持各种算法
	private KeyGenerator keygen;
	// SecretKey 负责保存对称密钥
	private SecretKey deskey;
	// Cipher负责完成加密或解密工作
	private Cipher c;
	// 该字节数组负责保存加密的结果
	private byte[] cipherByte;
	private static String MD5METHOD = "MD5";
	private static String SHAMETHOD = "SHA";
	public static final String ENCODING = "utf-8";
	public Encryp() throws NoSuchAlgorithmException, NoSuchPaddingException {
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		// 实例化支持DES算法的密钥生成器(算法名称命名需按规定，否则抛出异常)
		keygen = KeyGenerator.getInstance("AES");
		// 生成密钥
		deskey = keygen.generateKey();
		// 生成Cipher对象,指定其支持的DES算法
		c = Cipher.getInstance("AES");
	}

	/**
	 * 单向不可逆字符串加密加密
	 * 
	 * @param info
	 * @param eccryptMethod
	 *            MD5/SHA
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException 
	 */
	private byte[] encryptOneWay(String info, String eccryptMethod)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// 根据MD5算法生成MessageDigest对象
		MessageDigest md5 = MessageDigest.getInstance(eccryptMethod);
		byte[] srcBytes = info.getBytes(ENCODING);
		// 使用srcBytes更新摘要
		md5.update(srcBytes);
		// 完成哈希计算，得到result
		byte[] resultBytes = md5.digest();
		return resultBytes;
	}

	/**
	 * 获取经过md5加密后的字符串
	 * 
	 * @param info
	 * @return
	 */
	public String getMd5(String info) {
		if (info != null && !info.isEmpty()) {
			try {
				return new String(encryptOneWay(info, MD5METHOD));
			} catch (Exception e) {
				Log.e("DEBUG", "getMd5_FAILED");
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 获取经过sha加密后的字符串
	 * 
	 * @param info
	 * @return
	 */
	public String getSha(String info) {
		if (info != null && !info.isEmpty()) {
			try {
				return new String(encryptOneWay(info, SHAMETHOD));
			} catch (Exception e) {
				Log.e("DEBUG", "getSha_FAILED");
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 对字符串加密
	 * 
	 * @param str
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException 
	 */
	private byte[] Encrytor(String str) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		// 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密模式
		c.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] src = str.getBytes(ENCODING);
		// 加密，结果保存进cipherByte
		cipherByte = c.doFinal(src);
		return cipherByte;
	}

	/**
	 * 获取经过AES加密后的字符串
	 * 
	 * @param str
	 * @return
	 */
	public String getAESEncode(String str) {
		if (str != null && !str.isEmpty()) {
			try {
				return new String(Encrytor(str));
			} catch (Exception e) {
				Log.e("DEBUG", "getAESEncode_FAILED");
				e.printStackTrace();
			}
		} else {
			return null;
		}
		return null;
	}

	/**
	 * 对字符串解密
	 * 
	 * @param buff
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private byte[] Decryptor(byte[] buff) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		// 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示加密模式
		c.init(Cipher.DECRYPT_MODE, deskey);
		cipherByte = c.doFinal(buff);
		return cipherByte;
	}

	/**
	 * 获取AES解密后字符串
	 * 
	 * @param str
	 * @return
	 */
	public String getAESDecode(String str) {
		if (str != null && !str.isEmpty()) {
			try {
				return new String(Decryptor(str.getBytes()));
			} catch (Exception e) {
				Log.e("DEBUG", "getAESEncode_FAILED");
				e.printStackTrace();
			}
		} else {
			return null;
		}
		return null;
	}

}
