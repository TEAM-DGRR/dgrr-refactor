package live.dgrr.domain.kakao.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import live.dgrr.domain.member.dto.response.MemberResponse;
import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.member.repository.MemberRepository;
import live.dgrr.global.config.kakao.KakaoConfig;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
public class KakaoService {

    private final MemberRepository memberRepository;
    private final KakaoConfig kakaoConfig;

    public KakaoService(MemberRepository memberRepository, KakaoConfig kakaoConfig) {
        this.memberRepository = memberRepository;
        this.kakaoConfig = kakaoConfig;
    }

    public String getKakaoAccessToken (String code) {
        String access_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            String sb = "grant_type=authorization_code" +
                    "&client_id=" + kakaoConfig.getClientId() + // REST_API_KEY 입력
                    "&redirect_uri=" + kakaoConfig.getRedirectUri() + // 인가코드 받은 redirect_uri 입력
                    "&code=" + code;
            bw.write(sb);
            bw.flush();

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result.toString());

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }

    public String createKakaoUser(String token) {
        String id = "";
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result.toString());

            id = element.getAsJsonObject().get("id").getAsString();

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    public MemberResponse getMemberByKakaoId(String kakaoId) {
        Optional<Member> memberOpt = memberRepository.findByKakaoId(kakaoId);

        return memberOpt.map(member -> MemberResponse.builder()
                        .memberId(member.getMemberId())
                        .nickname(member.getNickname())
                        .profileImage(member.getProfileImage())
                        .description(member.getDescription())
                        .build())
                .orElse(null);
    }

}
