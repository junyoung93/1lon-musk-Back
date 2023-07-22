package com.clone.clone.user;

import com.clone.clone.security.dto.LoginRequestDto;
import com.clone.clone.security.dto.SignupRequestDto;
import com.clone.clone.security.dto.ToekenResponseDto;
import com.clone.clone.security.jwt.JwtUtil;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    //사용자로부터 회원 가입 요청 정보를 담은 DTO를 인자로 받아 처리합니다.
    public ResponseEntity<?> signup(SignupRequestDto requestDto){
        //정보 가져옴
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String email = requestDto.getEmail();
        boolean marketing = requestDto.isMarketing();

        // email 중복 유효성 검사
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if(checkEmail.isPresent()){
            throw new IllegalArgumentException("중복된 email");
        }

        // 마켓팅 수집 동의 여부 약관 유효성 검사
        if(!marketing){
            throw new IllegalArgumentException("마케팅 약관에 동의 해주세요");
        }

        //user repo에 저장
        User user = new User(username, password, email);
        userRepository.save(user);

        String token = jwtUtil.createToken(email);
        ToekenResponseDto toekenResponseDto = new ToekenResponseDto(token);

        //Jackson에 의해 자동으로 json형태로 나감.
        return ResponseEntity.ok(toekenResponseDto);
    }


    public ResponseEntity<?> signin(LoginRequestDto requestDto, HttpServletResponse response){
        String email =requestDto.getEmail();
        String passowrd = requestDto.getPassword();

        //사용자 확인
        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new IllegalArgumentException("등록된 이메일이 없습니다.")
                );

        //비밀번호 유효성 검사
        if(!passwordEncoder.matches(passowrd, user.getPassword())){
            throw new IllegalArgumentException("비밀번호 불일치");
        }

        //Jwt 생성 및 바디로 전송할 Response 객체로 추가
        String token = jwtUtil.createToken(email);
        // 처음에 여기서 json으로 응답이 가지 않고 바로 클라이언트로 갔음.
        // 그래서 응답 본문에 넣고 싶어 JwtAuthenticationFilter의 successfulAuthentication를 수정함.
        return jwtUtil.addJwtBody(token,response);
    }

}
