package com.caiyi.lottery.tradesystem.util.sign.snk;


/**
 * RSAPUBKEY structure
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
public class RSAPubKey {
    public int magic; // 0x32415352 ("RSA2") for public/private key; ("RSA1")
                      // for public key only
    public int bitlen; // Number of bits in the modulus. A multiple of eight.
    public int pubexp; // The public exponent
}