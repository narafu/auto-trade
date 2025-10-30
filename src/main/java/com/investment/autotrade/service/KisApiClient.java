package com.investment.autotrade.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.investment.autotrade.common.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * KIS API 호출을 담당하는 공통 클라이언트
 */
@Slf4j
@Component
public class KisApiClient {

    @Value("${kis.api.app-key}")
    private String appKey;

    @Value("${kis.api.app-secret}")
    private String appSecret;

    private final WebClient webClient;
    private final KisTokenService tokenService;

    public KisApiClient(WebClient.Builder webClientBuilder, KisTokenService tokenService) {
        this.webClient = webClientBuilder.baseUrl(Const.BASE_URL).build();
        this.tokenService = tokenService;
    }

    /**
     * KIS API 호출에 필요한 공통 HTTP 헤더를 생성합니다.
     *
     * @param trId 호출할 API의 TR ID
     * @return HttpHeaders 객체
     */
    public HttpHeaders createCommonHeaders(String trId) {
        HttpHeaders headers = new HttpHeaders();
        // 토큰 서비스에서 유효한 토큰을 가져옵니다. (만료 시 자동 갱신)
        String accessToken = tokenService.getAccessToken();

        headers.setBearerAuth(accessToken);
        headers.set("appkey", appKey);
        headers.set("appsecret", appSecret);
        headers.set("tr_id", trId); // API별 고유 TR ID
        headers.set("custtype", "P"); // 고객 타입 (P:개인)

        return headers;
    }

    /**
     * KIS API 응답을 처리하는 공통 로직입니다.
     *
     * @param response   API 응답 JSON
     * @param operation  수행한 작업명 (로깅용)
     * @param successMsg 성공 시 로그 메시지
     * @return 성공 시 응답 JSON, 실패 시 null
     */
    public JsonNode processApiResponse(JsonNode response, String operation, String successMsg) {
        if (response != null && "0".equals(response.get("rt_cd").asText())) {
            log.info("{} Success. Response: {}", successMsg, response.get("msg1").asText());
            return response;
        } else {
            log.error("{} Failed. Response: {}", successMsg,
                    response != null ? response.toPrettyString() : "No response");
            return null;
        }
    }

    /**
     * GET 요청을 수행하는 공통 메서드입니다.
     *
     * @param uri       요청 URI
     * @param trId      TR ID
     * @param operation 작업명 (로깅용)
     * @return API 응답 JSON
     */
    public JsonNode performGetRequest(String uri, String trId, String operation) {
        try {
            log.info("Attempting to {}...", operation);
            log.debug("Request URI: {}", uri);

            JsonNode response = webClient.get()
                    .uri(uri)
                    .headers(httpHeaders -> httpHeaders.addAll(createCommonHeaders(trId)))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            log.debug("{} response: {}", operation, response != null ? response.toPrettyString() : "null");
            return processApiResponse(response, operation, operation);
        } catch (Exception e) {
            log.error("Exception during {}: {}", operation, e.getMessage());
            return null;
        }
    }

    /**
     * POST 요청을 수행하는 공통 메서드입니다.
     *
     * @param uri         요청 URI
     * @param trId        TR ID
     * @param requestBody 요청 본문
     * @param operation   작업명 (로깅용)
     * @return API 응답 JSON
     */
    public JsonNode performPostRequest(String uri, String trId, String requestBody, String operation) {
//        try {
        log.info("Attempting to {}...", operation);
        log.debug("Request body: {}", requestBody);

        JsonNode response = webClient.post()
                .uri(uri)
                .headers(httpHeaders -> httpHeaders.addAll(createCommonHeaders(trId)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        log.debug("{} response: {}", operation, response != null ? response.toPrettyString() : "null");
        return processApiResponse(response, operation, operation);

//        } catch (Exception e) {
//            log.error("Exception during {}: {}", operation, e.getMessage());
//            return null;
//        }
    }
}
