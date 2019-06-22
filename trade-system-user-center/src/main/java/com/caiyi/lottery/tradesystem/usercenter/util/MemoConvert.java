package com.caiyi.lottery.tradesystem.usercenter.util;

import bean.Memo;

import static com.caiyi.lottery.tradesystem.util.xml.XmlUtil.*;

/**
 * @author wxy
 * @create 2017-12-08 20:01
 **/
public class MemoConvert {
    public static Memo showCmemo(String ibiztype, String cmemo){
        int  biztype=Integer.valueOf(ibiztype);
        Memo  memo=null;
        String [] memoarr=cmemo.split("\\|");
        String imemo=null;
        if (memoarr.length>1){
            switch (biztype){
                case 200:
                    imemo=CHONGZHI.get(memoarr[0])+"充值  订单号:" +memoarr[1];
                    memo=new Memo(imemo);
                    break;
                case 100:
                case 101:
                case 103:
                    imemo=getLotname(memoarr[0])+getBiztype(ibiztype);
                    memo=new Memo(memoarr[0], memoarr[1], imemo);
                    break;
                case 105:
                    imemo=getLotname(memoarr[0])+getBiztype(ibiztype);
                    memo=new Memo(memoarr[0], memoarr[1], imemo);
                    break;
                case 201:
                case 202:
                case 203:
                case 210:
                case 211:
                case 252:
                case 253:
                case 215:
                case 256:
                case 257:
                case 113:
                    imemo = getLotname(memoarr[0]) + getBiztype(ibiztype);
                    memo = new Memo(memoarr[0], memoarr[1], imemo);
                    break;
                case 98:
                    String [] NT=memoarr[1].split("ZH");
                    imemo=getLotname(NT[0])+getBiztype(ibiztype);
                    memo=new Memo(NT[0],memoarr[1], imemo);
                    break;
                case 102:
                case 212:
                case 254:
                    String [] nt=memoarr[1].split("ZH");
                    imemo=getLotname(nt[0])+getBiztype(ibiztype);
                    memo=new Memo(nt[0],memoarr[1], imemo);
                    break;
                case 204:
                    imemo=getLotname(memoarr[0])+getBiztype(ibiztype);
                    memo=new Memo(memoarr[0], memoarr[2], imemo);
                    break;
                case 300:
                    imemo="转款";
                    memo=new Memo(imemo);
                    break;

                case 302:
                case 303:
                    imemo = "补派奖金";
                    memo = new Memo(imemo);
                    break;
                case 304:
                    imemo = "网站赔偿";
                    memo = new Memo(imemo);
                    break;
                case 107:
                    if (memoarr.length==5){
                        memo=new Memo(HUODONGJIAJIAN.get(memoarr[0]));
                    }
                    break;
                case 216:
                    if (memoarr.length==4){
                        memo=new Memo(HUODONGJIAJIAN.get(memoarr[0]));
                    }
                    break;
                case 213:
                    imemo = "提现撤销返款";
                    memo = new Memo(imemo);
                    break;
                case 214:
                    imemo = " 提款失败转款";
                    memo = new Memo(imemo);
                    break;
                default:
                    break;
            }
        }
        return memo;
    }
}
