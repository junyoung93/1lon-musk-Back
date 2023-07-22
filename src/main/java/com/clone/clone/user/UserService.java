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

    public ResponseEntity<?> signup(SignupRequestDto requestDto){
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
        String token = jwtUtil.createToken(user.getEmail());
        jwtUtil.addJwtBody(token,response);

        ToekenResponseDto toekenResponseDto = new ToekenResponseDto(token);

        return ResponseEntity.ok(toekenResponseDto);
    }

}
