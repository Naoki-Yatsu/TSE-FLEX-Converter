package ny2.flex.message;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

/**
 * Issue classification code in user data section
 */
@Getter
public enum IssueClassificationType {

    ST0111("0111", "First section(Domestic stock)"),
    ST0112("0112", "Second Section(Domestic stock)"),
    ST0113("0113", "First Section (Foreign stock)"),
    ST0114("0114", "Mothers(Domestic Stock)"),
    ST0115("0115", "Domestic beneficiary certificate of investment trust"),
    ST0116("0116", "Foreign beneficiary certificate of investment trust"),
    ST0117("0117", "Preferred share"),
    ST0118("0118", "Mothers(Foreign stock)"),
    ST0119("0119", "Investment certificate"),
    ST0121("0121", "Second Section(Foreign stock)"),
    ST0122("0122", "TOKYO PRO Market(Domestic stock)"),
    ST0123("0123", "TOKYO PRO Market(Foreign stock)"),
    ST0124("0124", "JASDAQ Standard(Domestic stock)"),
    ST0125("0125", "JASDAQ Standard(Foreign stock)"),
    ST0126("0126", "JASDAQ Growth(Domestic stock)"),
    ST0127("0127", "JASDAQ Growth(Foreign stock)"),

    CB0211("0211", "CB"),
    CB0212("0212", "Not used"),
    CB0213("0213", "SW"),
    CB0214("0214", "Exchangeable bond"),
    CB0215("0215", "Reserved"),

    OTHER("", "");

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private String code;

    private String description;

    // //////////////////////////////////////
    // Field (final)
    // //////////////////////////////////////

    public static final Set<IssueClassificationType> SET_STOCK_1ST = new HashSet<>(Arrays.asList(ST0111));
    public static final Set<IssueClassificationType> SET_STOCK_2ND = new HashSet<>(Arrays.asList(ST0112));
    public static final Set<IssueClassificationType> SET_STOCK_OTHRES = new HashSet<>(Arrays.asList(
            ST0111, ST0112, ST0113, ST0114, ST0115, ST0116, ST0117, ST0118, ST0119,
            ST0121, ST0122, ST0123, ST0124, ST0125, ST0126, ST0127));

    public static final Set<IssueClassificationType> SET_CB = new HashSet<>(Arrays.asList(
            CB0211, CB0212, CB0213, CB0214, CB0215));

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    private IssueClassificationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    public static IssueClassificationType parseCode(String code) {
        for (IssueClassificationType classificationType : values()) {
            if (classificationType.getCode().equals(code)) {
                return classificationType;
            }
        }
        return OTHER;
    }
}
