package ny2.flex.data;

import java.util.StringJoiner;

import lombok.ToString;
import ny2.flex.message.IssueClassificationType;

@ToString
public class IssueInformation extends Data {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private String sym;

    private int exchangeCode;

    private IssueClassificationType issueClassificationType;


    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @Override
    public DataType getDataType() {
        return DataType.IssueInformation;
    }

    @Override
    public String csvOut() {
        StringJoiner sj = new StringJoiner(getCsvSeparator());
        sj.add(sym)
                .add(String.valueOf(exchangeCode))
                .add(issueClassificationType.getCode());
        return sj.toString();
    }

    public static String csvHeader() {
        StringJoiner sj = new StringJoiner(getCsvSeparator());
        sj.add("sym")
                .add("exchangeCode")
                .add("classificationCode");
        return sj.toString();
    }

    // //////////////////////////////////////
    // Getters and Setters
    // //////////////////////////////////////

    public String getSym() {
        return sym;
    }
    public void setSym(String sym) {
        this.sym = sym;
    }
    public int getExchangeCode() {
        return exchangeCode;
    }
    public void setExchangeCode(int exchangeCode) {
        this.exchangeCode = exchangeCode;
    }
    public IssueClassificationType getIssueClassificationType() {
        return issueClassificationType;
    }
    public void setIssueClassificationType(IssueClassificationType issueClassificationType) {
        this.issueClassificationType = issueClassificationType;
    }

}
