package com.investment.autotrade.dto.response.kis;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class OverseasOrder extends Response {
    private String krxFwdgOrdOrgno;    // #한국거래소전송주문조직번호
    private String odno;    // #주문번호
    private LocalDateTime ordTmd;    // #주문시각

    public static OverseasOrder from(JsonNode response) {
        if (response == null) {
            return null;
        }

        if (!response.isObject()) {
            return null;
        }

        return response.get("output").asOptional().map(OverseasOrder::create).orElse(null);
    }

    private static OverseasOrder create(JsonNode output) {
        return OverseasOrder.builder()
                .krxFwdgOrdOrgno(extractString(output, "krx_fwdg_ord_orgno"))
                .odno(extractString(output, "odno"))
                .ordTmd(extractLocalDateTime(output, "ord_tmd"))
                .build();
    }
}

