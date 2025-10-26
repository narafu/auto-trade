package com.investment.autotrade.dto.response.kis;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class OverseasPsamount extends Response {
    private String trCrcyCd; // #거래통화코드
    private double ordPsblFrcrAmt; // #주문가능외화금액
    private double sllRusePsblAmt; // #매도재사용가능금액
    private double ovrsOrdPsblAmt; // #해외주문가능금액
    private int maxOrdPsblQty; // #최대주문가능수량
    private double echmAfOrdPsblAmt; // #환전이후주문가능금액
    private int echmAfOrdPsblQty; // #환전이후주문가능수량
    private int ordPsblQty; // #주문가능수량
    private double exrt; // #환율
    private double frcrOrdPsblAmt1; // #외화주문가능금액1
    private int ovrsMaxOrdPsblQty; // #해외최대주문가능수량

    public static OverseasPsamount from(JsonNode response) {
        if (response == null) {
            return null;
        }

        if (!response.isObject()) {
            return null;
        }

        return response.get("output").asOptional().map(OverseasPsamount::create).orElse(null);
    }

    private static OverseasPsamount create(JsonNode output) {
        return OverseasPsamount.builder()
                .trCrcyCd(extractString(output, "trCrcyCd"))
                .ordPsblFrcrAmt(extractDouble(output, "ord_psbl_frcr_amt"))
                .sllRusePsblAmt(extractDouble(output, "sll_ruse_psbl_amt"))
                .ovrsOrdPsblAmt(extractDouble(output, "ovrs_ord_psbl_amt"))
                .maxOrdPsblQty(extractInt(output, "max_ord_psbl_qty"))
                .echmAfOrdPsblAmt(extractDouble(output, "echm_af_ord_psbl_amt"))
                .echmAfOrdPsblQty(extractInt(output, "echm_af_ord_psbl_qty"))
                .ordPsblQty(extractInt(output, "ord_psbl_qty"))
                .exrt(extractDouble(output, "exrt"))
                .frcrOrdPsblAmt1(extractDouble(output, "frcr_ord_psbl_amt1"))
                .ovrsMaxOrdPsblQty(extractInt(output, "ovrs_max_ord_psbl_qty"))
                .build();
    }
}
