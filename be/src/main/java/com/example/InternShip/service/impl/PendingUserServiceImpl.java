package com.example.InternShip.service.impl;

import com.example.InternShip.entity.PendingUser;
import com.example.InternShip.entity.User;
import com.example.InternShip.entity.enums.Role;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.repository.PendingUserRepository;
import com.example.InternShip.repository.UserRepository;
import com.example.InternShip.service.PendingUserService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PendingUserServiceImpl implements PendingUserService {
    private final PendingUserRepository pendingUserRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromMail;

    public void verify(String token) {
        try {
            PendingUser pendingUser = pendingUserRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException(ErrorCode.VERIFICATION_CODE_NOT_EXISTED.getMessage()));
            if (pendingUser.getExpiryDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException(ErrorCode.VERIFICATION_CODE_INVALID.getMessage());
            }

            User user = modelMapper.map(pendingUser, User.class);
            user.setRole(Role.VISITOR);
            userRepository.save(user);
            pendingUserRepository.delete(pendingUser);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(ErrorCode.VERIFICATION_FAILED.getMessage());
        }
    }

    public void verifyForgetPassword(String token) {
        try {
            PendingUser pendingUser = pendingUserRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException(ErrorCode.VERIFICATION_CODE_NOT_EXISTED.getMessage()));
            if (pendingUser.getExpiryDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException(ErrorCode.VERIFICATION_CODE_INVALID.getMessage());
            }

            User user = userRepository.findByUsernameOrEmail(pendingUser.getEmail())
                    .orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_EXISTED.getMessage()));
            user.setPassword(pendingUser.getPassword());
            userRepository.save(user);
            pendingUserRepository.delete(pendingUser);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(ErrorCode.VERIFICATION_FAILED.getMessage());
        }
    }

    public void sendVerification(String email, String verifyLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(fromMail));
            helper.setTo(email);
            helper.setSubject("Xác thực email của bạn");

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
            String formattedNow = now.format(formatter);

            String emailContent = """
                    <html>
                      <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4;">
                        <div style="max-width: 600px; margin: 20px auto; padding: 20px; background: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">

                          <h2 style="color: #1a73e8; border-bottom: 2px solid #1a73e8; padding-bottom: 10px; margin-top: 0;">
                            Xin chào!
                          </h2>

                                <p>Cảm ơn bạn đã đăng ký. Vui lòng hoàn tất xác thực email để kích hoạt tài khoản.</p>

                          <p style="text-align: center; margin: 30px 0;">
                            <a href="%s"
                               style="display: inline-block; padding: 12px 25px; color: #ffffff; background-color: #1a73e8; border-radius: 5px; text-decoration: none; font-weight: bold;">
                              Xác thực ngay
                            </a>
                          </p>

                                <p>Liên kết xác thực sẽ **hết hạn sau 20 phút** tính từ: %s</p>

                          <p style="font-size: 12px; color: #777;">
                            Nếu bạn không yêu cầu xác thực email này, vui lòng bỏ qua thư này.
                          </p>

                            </div>
                            <div style="text-align: center; padding: 10px 0; font-size: 12px; color: #aaa;">
                                Bản quyền &copy; [Tên Công ty của bạn]
                            </div>
                        </body>
                    </html>
                    """.formatted(verifyLink,formattedNow);
            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(ErrorCode.VERIFICATION_CODE_SEND_FAILED.getMessage());
        }
    }
}
