package com.caiyi.lottery.tradesystem.util.sign.snk;

/**
 * BLOBHEADER structure
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
public class BlobHeader {
    public byte bType; // PRIVATEKEYBLOB = 0x7 or PUBLICKEYBLOB = 0x06
    public byte bVersion; // Digital Signature Standard=3; CUR_BLOB_VERSION=2
    public short reserved; //
    public int aiKeyAlg;// CALG_RSA_KEYX=0xa400; CALG_RSA_SIGN=0x2400
}