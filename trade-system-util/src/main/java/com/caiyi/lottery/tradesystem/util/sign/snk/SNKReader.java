package com.caiyi.lottery.tradesystem.util.sign.snk;

import com.caiyi.lottery.tradesystem.util.GeneralBase64Utils;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * {@code SNK}格式的公私钥读取
 * 
 * @author sunaolin
 * 
 */
public final class SNKReader {

    /**
     * 公钥类型
     */
    private static final byte PRIVATE_KEY_TYPE = 0x7;

    /**
     * 把{@code SNK}格式中公钥转换成通用公钥
     * 
     * @param publicKey {@code SNK}格式的公钥信息
     * @return 返回公钥信息
     * @throws Exception
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        byte[] decoded = GeneralBase64Utils.decode(publicKey);

        PublicKeyBlob publicKeyBlob = getPublicKeyBlob(decoded);
        BigInteger publicExponent = new BigInteger(String.valueOf(publicKeyBlob.rsapubkey.pubexp));
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(publicKeyBlob.modulus, publicExponent);

        return keyFactory.generatePublic(pubKeySpec);
    }

    /**
     * 把{@code SNK}格式中私钥转换成通用私钥
     * 
     * @param privateKey {@code SNK}格式的私钥信息
     * @return 返回私钥信息
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        byte[] decoded = GeneralBase64Utils.decode(privateKey);

        PrivateKeyBlob privateKeyBlob = getPrivateKeyBlob(decoded);
        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(privateKeyBlob.modulus, privateKeyBlob.privateExponent);

        return keyFactory.generatePrivate(privateKeySpec);
    }

    /**
     * Read PublicKeyBlob
     * 
     * @param snkBytes
     * @return
     */
    private static PublicKeyBlob getPublicKeyBlob(byte[] snkBytes) {
        if (snkBytes == null || snkBytes.length <= 20) {
            throw new IllegalArgumentException();
        }

        ByteBuffer buffer = ByteBuffer.allocate(snkBytes.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(snkBytes);
        buffer.rewind();

        PublicKeyBlob publicKeyBlob = new PublicKeyBlob();

        // Read PublicKeyBlob
        if (PRIVATE_KEY_TYPE != snkBytes[0]) {
            publicKeyBlob.signatureAlg = buffer.getInt();
            publicKeyBlob.hashAlg = buffer.getInt();
            publicKeyBlob.blobLength = buffer.getInt();
        }
        // Read BLOBHEADER
        publicKeyBlob.blobheader = getBlobHeader(buffer);
        // Read RSAPUBKEY
        publicKeyBlob.rsapubkey = getRsaPubKey(buffer);

        int keyBitLength = publicKeyBlob.rsapubkey.bitlen;
        int eightBitLength = keyBitLength / 8;

        // Read Modulus
        publicKeyBlob.modulus = getBigInteger(buffer, eightBitLength);

        return publicKeyBlob;
    }

    /**
     * Read PrivateKeyBlob
     * 
     * @param snkBytes
     * @return
     */
    private static PrivateKeyBlob getPrivateKeyBlob(byte[] snkBytes) {
        if (snkBytes == null || snkBytes.length <= 20) {
            throw new IllegalArgumentException();
        }

        ByteBuffer buffer = ByteBuffer.allocate(snkBytes.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(snkBytes);
        buffer.rewind();

        PrivateKeyBlob privateKeyBlob = new PrivateKeyBlob();

        // Read BLOBHEADER
        privateKeyBlob.blobheader = getBlobHeader(buffer);
        // Read RSAPUBKEY
        privateKeyBlob.rsapubkey = getRsaPubKey(buffer);

        int keyBitLength = privateKeyBlob.rsapubkey.bitlen;
        int eightBitLength = keyBitLength / 8;
        int sixteenBitLength = keyBitLength / 16;

        // Read Modulus
        privateKeyBlob.modulus = getBigInteger(buffer, eightBitLength);
        // Read PrivateKeyBlob
        privateKeyBlob.prime1 = getBigInteger(buffer, sixteenBitLength);
        privateKeyBlob.prime2 = getBigInteger(buffer, sixteenBitLength);
        privateKeyBlob.exponent1 = getBigInteger(buffer, sixteenBitLength);
        privateKeyBlob.exponent2 = getBigInteger(buffer, sixteenBitLength);
        privateKeyBlob.coefficient = getBigInteger(buffer, sixteenBitLength);
        privateKeyBlob.privateExponent = getBigInteger(buffer, eightBitLength);

        return privateKeyBlob;
    }

    /**
     * Read BLOBHEADER
     * 
     * @param buffer
     * @return
     */
    private static BlobHeader getBlobHeader(ByteBuffer buffer) {
        BlobHeader blobHeader = new BlobHeader();

        blobHeader.bType = buffer.get();
        blobHeader.bVersion = buffer.get();
        blobHeader.reserved = buffer.getShort();
        blobHeader.aiKeyAlg = buffer.getInt();

        return blobHeader;
    }

    /**
     * Read RSAPUBKEY
     * 
     * @param buffer
     * @return
     */
    private static RSAPubKey getRsaPubKey(ByteBuffer buffer) {
        RSAPubKey rsaPubKey = new RSAPubKey();

        rsaPubKey.magic = buffer.getInt();
        rsaPubKey.bitlen = buffer.getInt();
        rsaPubKey.pubexp = buffer.getInt();

        return rsaPubKey;
    }

    /**
     * Read BigInteger
     * 
     * @param buffer
     * @param length
     * @return
     */
    private static BigInteger getBigInteger(ByteBuffer buffer, int length) {
        byte[] bytes = new byte[length + 1];

        buffer.get(bytes, 0, length);
        ArrayUtils.reverse(bytes);

        return new BigInteger(bytes);
    }
}