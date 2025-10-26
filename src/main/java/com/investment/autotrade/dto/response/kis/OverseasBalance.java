package com.investment.autotrade.dto.response.kis;

import com.fasterxml.jackson.databind.JsonNode;
import com.investment.autotrade.enums.ExchangeCode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class OverseasBalance extends Response {
    private Integer cano;    // #종합계좌번호
    private String acntPrdtCd;    // #계좌상품코드
    private String prdtTypeCd;    // #상품유형코드
    private String ovrsPdno;    // #해외상품번호
    private String ovrsItemName;    // #해외종목명
    private double pchsAvgPric;    // #매입평균가격
    private int ovrsCblcQty;    // #해외잔고수량
    private int ordPsblQty;    // #주문가능수량
    private ExchangeCode ovrsExcgCd;    // #해외거래소코드

    public static OverseasBalance from(JsonNode response) {
        if (response == null) {
            return null;
        }

        if (!response.isObject()) {
            return null;
        }

        return response.get("output1").asOptional().map(OverseasBalance::create).orElse(null);
    }

    private static OverseasBalance create(JsonNode output) {
        return OverseasBalance.builder()
                .cano(extractInteger(output, "cano"))
                .acntPrdtCd(extractString(output, "acnt_prdt_cd"))
                .prdtTypeCd(extractString(output, "prdt_type_cd"))
                .ovrsPdno(extractString(output, "ovrs_pdno"))
                .ovrsItemName(extractString(output, "ovrs_item_name"))
                .pchsAvgPric(extractDouble(output, "pchs_avg_pric"))
                .ovrsCblcQty(extractInt(output, "ovrs_cblc_qty"))
                .ordPsblQty(extractInt(output, "ord_psbl_qty"))
                .ovrsExcgCd(extractEnum(output, "ovrs_excg_cd"))
                .build();
    }
}

