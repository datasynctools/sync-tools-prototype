package tools.datasync.api.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import tools.datasync.api.dao.HashFromObject;

public class DefaultHashFromObject implements HashFromObject {

    private Stringify stringify = new Jsonify();
    private MessageDigest messageDigest;

    public DefaultHashFromObject() {
	try {
	    messageDigest = MessageDigest.getInstance("MD5");
	} catch (NoSuchAlgorithmException e) {
	    throw (new RuntimeException(e));
	}
    }

    public String createHash(Object item) {
	String str = stringify.toString(item);
	byte[] input = str.getBytes();
	messageDigest.update(input);
	byte[] digest = messageDigest.digest();
	String checksum = toHexString1(digest);
	return checksum;
    }

    private String toHexString1(byte[] bytes) {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < bytes.length; i++) {
	    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
		    .substring(1));
	}
	return sb.toString();
    }

    public void setStringify(Stringify stringify) {
	this.stringify = stringify;
    }

    public void setMessageDigest(MessageDigest messageDigest) {
	this.messageDigest = messageDigest;
    }

}
