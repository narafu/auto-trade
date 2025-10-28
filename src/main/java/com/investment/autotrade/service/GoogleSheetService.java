package com.investment.autotrade.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.investment.autotrade.common.Const;
import com.investment.autotrade.dto.response.excel.Privacy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static com.investment.autotrade.common.Const.APPLICATION_NAME;


@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleSheetService {

    private final ResourceLoader resourceLoader; // ResourceLoader 주입

    @Value("${google.sheets.credentials-path}")
    private String credentialsPath;

    @Value("${google.sheets.spreadsheet-id}")
    private String SPREADSHEET_ID;

    public Privacy getPrivacyExcel(String rangeName) {
        var result = this.readData(rangeName);

        return Privacy.from(result);
    }

    /**
     * Google Sheets API 서비스 객체를 초기화하고 서비스 계정을 통해 인증합니다.
     */
    private Sheets getSheetsService() throws IOException {
        // 시트 읽기/쓰기 권한(Scope) 정의
        List<String> SCOPES = Collections.singletonList(Const.GOOGLE_SHEETS_SCOPE);

        // Spring의 ResourceLoader를 사용하여 클래스패스 또는 파일 시스템에서 키 파일 로드
        Resource resource = resourceLoader.getResource(credentialsPath);
        InputStream inputStream = resource.getInputStream();

        // GoogleCredentials를 사용하여 서비스 계정 인증 정보를 생성하고 Scope를 적용
        GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(SCOPES);

        // Sheets Service 빌드 (1.x 버전대 호환)
        return new Sheets.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * 지정된 범위의 데이터를 읽어옵니다.
     *
     * @param rangeName 예: "Sheet1!A1:B10"
     * @return 셀 값 목록
     */
    public List<List<Object>> readData(String rangeName) {
        try {
            Sheets sheetsService = getSheetsService();

            // API 호출: 지정된 시트 ID와 범위의 값을 가져옵니다.
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, rangeName)
                    .execute();

            List<List<Object>> values = response.getValues();

            if (values == null || values.isEmpty()) {
                log.info("No data found in range: {}", rangeName);
                return Collections.emptyList();
            }
            log.info("Data successfully read from range: {}", rangeName);
            return values;

        } catch (IOException e) {
            log.error("Error reading data. Check ID, Range, and Permissions: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 지정된 셀 또는 범위에 새로운 데이터를 덮어씁니다.
     *
     * @param rangeName 예: "Sheet1!C1"
     * @param newValue  새로운 값 (List<List<Object>> 형태)
     */
    public void updateData(String rangeName, List<List<Object>> newValue) {
        try {
            Sheets sheetsService = getSheetsService();

            ValueRange body = new ValueRange()
                    .setValues(newValue);

            // API 호출
            sheetsService.spreadsheets().values()
                    .update(SPREADSHEET_ID, rangeName, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();

            log.info("Data successfully updated in range: {}", rangeName);

        } catch (IOException e) {
            log.error("Error updating data. Check ID, Range, and Permissions: {}", e.getMessage());
        }
    }
}