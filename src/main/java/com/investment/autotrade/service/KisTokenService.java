package com.investment.autotrade.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.investment.autotrade.common.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 한국투자증권(KIS) API 접근 토큰 발급 및 관리를 담당하는 서비스.
 * WebClient를 사용하여 비동기 REST API 통신을 수행합니다.
 */
@Slf4j
@Service
public class KisTokenService {

    // application.yml 등에서 설정하여 주입받아야 함
    @Value("${kis.api.app-key}")
    private String appKey;

    @Value("${kis.api.app-secret}")
    private String appSecret;

    private final WebClient webClient;

    // 메모리에 접근 토큰과 만료 시각을 저장하는 변수
    private final AtomicReference<String> accessToken = new AtomicReference<>();
    private final AtomicReference<LocalDateTime> tokenExpiration = new AtomicReference<>(LocalDateTime.MIN);

    public KisTokenService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(Const.BASE_URL).build();
    }

    /**
     * 현재 유효한 접근 토큰을 반환합니다.
     * 토큰이 만료되었거나 곧 만료될 경우, 토큰을 재발급(갱신)합니다.
     *
     * @return 유효한 접근 토큰 문자열
     */
    public String getAccessToken() {
        // 토큰이 없거나, 만료 예정이라면 갱신 로직 실행
        if (accessToken.get() == null || isTokenExpiredOrExpiringSoon()) {
            log.info("KIS Access Token is expired or expiring soon. Attempting renewal...");
            issueToken(); // 동기적으로 토큰 발급/갱신 시도
        }
        return accessToken.get();
    }

    /**
     * 현재 토큰이 만료되었거나 갱신 여유 시간(RENEWAL_LEEWAY_SECONDS) 이내로 남았는지 확인합니다.
     *
     * @return 토큰 갱신이 필요한 경우 true
     */
    private boolean isTokenExpiredOrExpiringSoon() {
        // 만료 시각이 현재 시각 + 여유 시간보다 앞서 있다면 true
        return tokenExpiration.get().isBefore(LocalDateTime.now().plusSeconds(Const.RENEWAL_LEEWAY_SECONDS));
    }

    /**
     * KIS API에 접근 토큰 발급을 요청하고 결과를 메모리에 저장합니다.
     * WebClient의 Mono.block()을 사용하여 동기적으로 실행합니다.
     */
    private void issueToken() {
        // 요청 본문 JSON 데이터 (올바른 필드명 사용)
        String requestBody = String.format(
                "{\"grant_type\": \"client_credentials\", \"appsecret\": \"%s\", \"appkey\": \"%s\"}",
                appSecret, appKey);

        try {
            log.info("Requesting KIS token with appKey: {}", appKey);
            log.debug("Token request body: {}", requestBody);

            JsonNode responseJson = webClient.post()
                    .uri(Const.TOKEN_API_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Accept", "text/plain")
                    .header("charset", "UTF-8")
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block(); // 비동기 WebClient를 동기적으로 사용 (토큰 발급은 필수 선행 작업이므로 block 사용)

            log.debug("Token response: {}", responseJson != null ? responseJson.toPrettyString() : "null");

            if (responseJson == null) {
                throw new RuntimeException("Failed to issue KIS token. Response is null.");
            }

            if (responseJson.has("error_code")) {
                // API 에러 발생
                String errorCode = responseJson.get("error_code").asText();
                String errorMessage = responseJson.has("message") ? responseJson.get("message").asText()
                        : "Unknown Error";
                throw new RuntimeException("Failed to issue KIS token. Code: " + errorCode + ", Msg: " + errorMessage);
            }

            // 응답 필드 검증
            if (!responseJson.has("access_token")) {
                log.error("Token response missing access_token field. Response: {}", responseJson.toPrettyString());
                throw new RuntimeException("Token response missing access_token field");
            }

            String token = responseJson.get("access_token").asText();

            // expires_in_seconds 필드가 있는지 확인
            int expiresIn = 86400; // 기본값 24시간
            if (responseJson.has("expires_in_seconds")) {
                expiresIn = responseJson.get("expires_in_seconds").asInt();
            } else if (responseJson.has("expires_in")) {
                expiresIn = responseJson.get("expires_in").asInt();
            } else {
                log.warn("Token response missing expires_in field, using default 24 hours. Response: {}",
                        responseJson.toPrettyString());
            }

            // 메모리에 토큰 및 만료 시각 저장
            accessToken.set(token);
            tokenExpiration.set(LocalDateTime.now().plusSeconds(expiresIn));

            log.info("KIS Access Token successfully issued/renewed.");
            log.info("Token expires at: {}", tokenExpiration.get());

        } catch (Exception e) {
            log.error("Exception during KIS token issuance: {}", e.getMessage());
            // 토큰 발급 실패 시 시스템 중단을 고려할 수 있음 (자동매매의 핵심 기능)
            accessToken.set(null);
            tokenExpiration.set(LocalDateTime.MIN);
        }
    }
}