package com.investment.autotrade.dto.response.kis;

import com.fasterxml.jackson.databind.JsonNode;
import com.investment.autotrade.enums.ExchangeCode;

import java.time.LocalDateTime;

public class Response {

    protected static double extractDouble(JsonNode output, String field) {
        return output.optional(field).map(JsonNode::asDouble).orElse(0.0);
    }

    protected static int extractInt(JsonNode output, String field) {
        return output.optional(field).map(JsonNode::asInt).orElse(0);
    }

    protected static Integer extractInteger(JsonNode output, String field) {
        return output.optional(field).map(JsonNode::asInt).orElse(null);
    }

    protected static String extractString(JsonNode output, String field) {
        return output.optional(field).map(JsonNode::asText).orElse(null);
    }

    protected static LocalDateTime extractLocalDateTime(JsonNode output, String field) {
        return output.optional(field).map(JsonNode::asText).map(LocalDateTime::parse).orElse(null);
    }

    protected static ExchangeCode extractEnum(JsonNode output, String field) {
        return output.optional(field).map(j -> ExchangeCode.fromCode(j.asText())).orElse(null);
    }
}
