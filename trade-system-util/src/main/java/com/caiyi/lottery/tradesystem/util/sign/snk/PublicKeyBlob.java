package com.caiyi.lottery.tradesystem.util.sign.snk;

import java.math.BigInteger;

/**
 * Public key structure
 * 
 * <h1>参考地址：</h1>
 *  <ul>
 *   <li>http://www.developerfusion.com/article/84422/the-key-to-strong-names/</li>
 *   <li>http://www.cnblogs.com/gyxdbk/archive/2013/05/10/3071271.html</li>
 * </ul>
 * 
 * @author sunaolin
 * 
 */
public class PublicKeyBlob {
    public BlobHeader blobheader = new BlobHeader();

    public RSAPubKey rsapubkey = new RSAPubKey();

    public int signatureAlg = 0x00002400; // CALG_RSA_SIGN=0x2400
    public int hashAlg = 0x00008004; // CALG_SHA1=0x8004
    public int blobLength = 148; // 148

    public BigInteger modulus;
}