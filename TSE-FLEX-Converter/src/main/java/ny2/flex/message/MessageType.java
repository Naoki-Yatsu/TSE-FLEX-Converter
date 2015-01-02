package ny2.flex.message;

import lombok.Getter;

/**
 *  Message Type in user data section
 */
@Getter
public enum MessageType {

    NO("NO", "Update No"),
    CP("1P", "Current Price"),
    VL("VL", "Trading Volume"),
    VA("VA", "Turnover"),
    QS("QS", "Ask quotes"),
    QB("QB", "Bid quotes"),
    SC("SC", "Sell orders effective only during the closing auction"),
    BC("BC", "Buy orders effective only during the closing auction"),

    MG("MG", "Multicast group number information"),

    LC("QB", "Control"),
    TC("QB", "TCP control");


    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private String tagId;

    private String description;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    private MessageType(String tagId, String description) {
        this.tagId = tagId;
        this.description = description;
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    public static MessageType parseTagId(String tagId) {
        // For 1P(Current Price) it is needed special parser.
        if (tagId.equals(CP.getTagId())) {
            return CP;
        } else {
            try {
                return MessageType.valueOf(tagId);
            } catch (Exception e) {
                System.out.println("Error of convert tag : " + tagId);
                e.printStackTrace();
                return null;
            }
        }
    }

}
