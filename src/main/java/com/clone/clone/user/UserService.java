package com.clone.clone.user;

import com.clone.clone.security.ExceptionHandler.SignExeption;
import com.clone.clone.security.dto.LoginRequestDto;
import com.clone.clone.security.dto.SignupRequestDto;
import com.clone.clone.security.dto.ToekenResponseDto;
import com.clone.clone.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    //사용자로부터 회원 가입 요청 정보를 담은 DTO를 인자로 받아 처리합니다.
    @Transactional
    public ResponseEntity<?> signup(@Valid SignupRequestDto requestDto) throws SignExeption {
        //정보 가져옴
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String email = requestDto.getEmail();
        boolean marketing = requestDto.isMarketing();

        //메일, 비번 유효성 검사
        //의존성에 : implementation 'commons-validator:commons-validator:1.7'
        if(requestDto.getPassword().length()<8 || !EmailValidator.getInstance().isValid(requestDto.getEmail())){
            throw new SignExeption("E-mail and password have different formats.","auth_001");
        }

        // email 중복 유효성 검사
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if(checkEmail.isPresent()){
            throw new SignExeption("E-mail and password have different formats.","auth_001");
        }
        log.info("email 중복 체크 완료");

        if(requestDto.getEmail().isEmpty() || requestDto.getPassword().isEmpty()){
            throw new SignExeption("Nullable=false","auth_002");
        }

        // 마켓팅 수집 동의 여부 약관 유효성 검사
        if(!marketing){
            throw new SignExeption("E-mail and password have different formats.","auth_001");
        }
        log.info("마켓팅 동의 수집 여부 확인 (=true)");

        //user repo에 저장
        User user = new User(username, password, email,marketing);
        userRepository.save(user);

        String token = jwtUtil.createToken(email);
        token = jwtUtil.substringToken(token);

        ToekenResponseDto toekenResponseDto = new ToekenResponseDto(token);


        log.info("회원 가입 완료");
        //Jackson에 의해 자동으로 json형태로 나감.
        return ResponseEntity.ok(toekenResponseDto);
    }

    @Transactional
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
