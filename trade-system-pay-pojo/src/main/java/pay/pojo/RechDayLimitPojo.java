package pay.pojo;

import lombok.Data;

@Data
public class RechDayLimitPojo {
   private String statday;
   private String channel;
   private String product;
   private String bankcode;
   private String cardno;
   private int cardtype;
   private double rechargemoney;
   private String applyids;
   private String csafekey;
   private String applyid;
   private double addmoney;
}
