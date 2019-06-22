package com.caiyi.lottery.tradesystem.util.sign.snk;


import java.math.BigInteger;

/**
 * Private key structure
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
public class PrivateKeyBlob {
    public BlobHeader blobheader = new BlobHeader();

    public RSAPubKey rsapubkey = new RSAPubKey();

    public BigInteger modulus;

    public BigInteger prime1; // [rsapubkey.bitlen/16]; as P
    public BigInteger prime2; // [rsapubkey.bitlen/16]; as Q
    public BigInteger exponent1; // [rsapubkey.bitlen/16]; as D mod (P - 1)".
    public BigInteger exponent2; // [rsapubkey.bitlen/16]; as D mod (Q - 1)".
    public BigInteger coefficient; // [rsapubkey.bitlen/16]; as InverseQ
    public BigInteger privateExponent; // [rsapubkey.bitlen/8]; as D
}