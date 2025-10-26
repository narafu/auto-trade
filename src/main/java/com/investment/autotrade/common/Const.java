package com.investment.autotrade.common;

public class Const {
    public static final String APPLICATION_NAME = "Autotrade Investment Application";
    // KIS API 엔드포인트 (실전투자 기준)
    public static final String BASE_URL = "https://openapi.koreainvestment.com:9443";
    // 토큰 발급/갱신 API URI
    public static final String TOKEN_API_URI = "/oauth2/tokenP";
    // 토큰 갱신 여유 시간 (초): 토큰 만료 1분 전에 갱신 시도
    public static final long RENEWAL_LEEWAY_SECONDS = 60;
    // [R] 해외 주식 잔고 조회 API
    public static final String INQUIRE_OVERSEAS_BALANCE_URI = "/uapi/overseas-stock/v1/trading/inquire-balance";
    // [R] 해외 주식 매수가능금액 조회 API
    public static final String INQUIRE_PSAMOUNT_URI = "/uapi/overseas-stock/v1/trading/inquire-psamount";
    // [U] 해외 주식 현금/지정가 주문 API
    public static final String OVERSEAS_ORDER_URI = "/uapi/overseas-stock/v1/trading/order";
    // [U] 해외 주식 예약주문접수 API
    public static final String OVERSEAS_RESERVATION_ORDER_URI = "/uapi/overseas-stock/v1/trading/order-resv";
    // Google Sheets API Scopes
    public static final String GOOGLE_SHEETS_SCOPE = "https://www.googleapis.com/auth/spreadsheets";
}
