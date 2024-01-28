package es.us.dit.fjfj.oauthlti;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtil {
	// Datos compartidos por todas las instancias
	private static final SecretKey eKey;
	private static final SecureRandom sr = new SecureRandom();
	public static final int AES_KEY_SIZE = 256;
	public static final int GCM_IV_LENGTH = 12;
	public static final int GCM_TAG_LENGTH = 16;

	static {
		Security.setProperty("crypto.policy", "unlimited");
		KeyGenerator keyGeneratorE = null;
		try {
			keyGeneratorE = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			keyGeneratorE=null;
		}
		if (keyGeneratorE != null) {
			keyGeneratorE.init(256);
			eKey = keyGeneratorE.generateKey();
		} else {
			eKey = null;
		}
	}

	private CryptUtil() {
	}

	public static byte[] generateIv() {
		byte[] iv = new byte[GCM_IV_LENGTH];
		sr.nextBytes(iv);
		return iv;
	}

	public static String createRandomBase64String(int size) {
		int eSize = sr.nextInt(size + 1);
		byte[] n = new byte[eSize];
		sr.nextBytes(n);
		byte[] buf = Base64.getUrlEncoder().encode(n);
		String ns = new String(buf, StandardCharsets.US_ASCII);
		return ns.substring(0, eSize);
	}

	public static byte[] encrypt(byte[] plaintext, byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		SecretKeySpec keySpec = new SecretKeySpec(eKey.getEncoded(), "AES");
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
		return cipher.doFinal(plaintext);
	}

	public static byte[] decrypt(byte[] cipherText, byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		SecretKeySpec keySpec = new SecretKeySpec(eKey.getEncoded(), "AES");
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
		return cipher.doFinal(cipherText);
	}
}
