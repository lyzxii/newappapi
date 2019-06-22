package pay.pojo;


import lombok.Data;

@Data
public class BankCard {
    private String bankCode;
    private String bankName;

    public BankCard(String bankCode, String bankName) {
        this.bankCode = bankCode;
        this.bankName = bankName;

    }
}
