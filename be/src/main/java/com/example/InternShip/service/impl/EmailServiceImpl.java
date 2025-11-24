package com.example.InternShip.service.impl;

import com.example.InternShip.entity.InternshipApplication;
import com.example.InternShip.entity.Sprint;
import com.example.InternShip.entity.Task;
import com.example.InternShip.entity.Team;
import com.example.InternShip.entity.User;
import com.example.InternShip.service.EmailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Override
    @Async // Gửi email bất đồng bộ để không làm chậm request chính của HR
    public void sendApplicationStatusEmail(InternshipApplication application) {
        User applicant = application.getUser();
        InternshipApplication.Status status = application.getStatus();

        String subject = "";
        String htmlContent = "";

        if (status == InternshipApplication.Status.APPROVED) {
            subject = "Chúc mừng! Hồ sơ ứng tuyển thực tập của bạn đã được duyệt";
            htmlContent = getApprovalEmailTemplate(applicant.getFullName(), application.getInternshipProgram().getName());
        } else if (status == InternshipApplication.Status.REJECTED) {
            subject = "Thông báo kết quả ứng tuyển thực tập";
            htmlContent = getRejectionEmailTemplate(applicant.getFullName(), application.getInternshipProgram().getName());
        } else {
            return; // Không gửi mail cho các trạng thái khác
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(fromMail, "Ban Nhân Sự - Internship Management"));
            helper.setTo(applicant.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true để kích hoạt nội dung HTML

            mailSender.send(message);
            log.info("Đã gửi email thông báo trạng thái hồ sơ tới {}", applicant.getEmail());
        } catch (Exception e) {
            log.error("Lỗi khi gửi email tới {}: {}", applicant.getEmail(), e.getMessage());
        }
    }

    // EMAIL CHẤP THUẬN
    private String getApprovalEmailTemplate(String applicantName, String programName) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap');
                        * { box-sizing: border-box; margin: 0; padding: 0; }
                        body { font-family: 'Inter', sans-serif; background-color: #f4f7fc; }
                        .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 8px 24px rgba(0,0,0,0.1); }
                        .header { background: linear-gradient(135deg, #4f46e5, #7c3aed); color: #ffffff; padding: 40px 20px; text-align: center; }
                        .header h1 { font-size: 28px; font-weight: 700; }
                        .content { padding: 32px 24px; color: #1f2a44; }
                        .content h2 { font-size: 20px; font-weight: 600; color: #4f46e5; margin-bottom: 16px; }
                        .content p { font-size: 16px; line-height: 1.7; margin-bottom: 20px; }
                        .button { display: inline-block; background: #4f46e5; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px; transition: background 0.3s ease; }
                        .button:hover { background: #3b82f6; }
                        .footer { background: #f1f5f9; text-align: center; padding: 20px; font-size: 12px; color: #64748b; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Chúc mừng bạn!</h1>
                        </div>
                        <div class="content">
                            <h2>Thân gửi %s,</h2>
                            <p>
                                Chúng tôi rất vui mừng thông báo rằng hồ sơ ứng tuyển của bạn cho vị trí <strong>Thực tập sinh</strong> trong chương trình <strong>%s</strong> đã được <strong>CHẤP THUẬN</strong>.
                            </p>
                            <p>
                                Chào mừng bạn gia nhập đội ngũ của chúng tôi! Bộ phận Nhân sự sẽ sớm liên hệ để hướng dẫn bạn các bước tiếp theo, bao gồm việc hoàn thiện thủ tục và lịch trình bắt đầu.
                            </p>
                            <p>Nếu có bất kỳ câu hỏi nào, vui lòng liên hệ qua email này.</p>
                            <p style="margin-top: 24px;">Trân trọng,<br><strong>Ban Nhân sự</strong></p>
                        </div>
                        <div class="footer">
                            &copy; 2025 Internship Management. All rights reserved.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(applicantName, programName);
    }

    // EMAIL TỪ CHỐI
    private String getRejectionEmailTemplate(String applicantName, String programName) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap');
                        * { box-sizing: border-box; margin: 0; padding: 0; }
                        body { font-family: 'Inter', sans-serif; background-color: #f4f7fc; }
                        .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 8px 24px rgba(0,0,0,0.1); }
                        .header { background: linear-gradient(135deg, #6b7280, #4b5563); color: #ffffff; padding: 40px 20px; text-align: center; }
                        .header h1 { font-size: 28px; font-weight: 700; }
                        .content { padding: 32px 24px; color: #1f2a44; }
                        .content h2 { font-size: 20px; font-weight: 600; color: #374151; margin-bottom: 16px; }
                        .content p { font-size: 16px; line-height: 1.7; margin-bottom: 20px; }
                        .footer { background: #f1f5f9; text-align: center; padding: 20px; font-size: 12px; color: #64748b; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Thông báo kết quả ứng tuyển</h1>
                        </div>
                        <div class="content">
                            <h2>Thân gửi %s,</h2>
                            <p>
                                Ban Nhân sự xin chân thành cảm ơn bạn đã dành thời gian ứng tuyển vào vị trí <strong>Thực tập sinh</strong> trong chương trình <strong>%s</strong>.
                            </p>
                            <p>
                                Sau quá trình xem xét kỹ lưỡng, chúng tôi rất tiếc phải thông báo rằng hồ sơ của bạn chưa phù hợp với các tiêu chí của chương trình lần này. Quyết định này không làm giảm giá trị năng lực và kinh nghiệm của bạn.
                            </p>
                            <p>
                                Chúng tôi sẽ lưu hồ sơ của bạn và sẽ liên hệ ngay khi có cơ hội phù hợp trong tương lai.
                            </p>
                            <p style="margin-top: 24px;">Chúc bạn sớm tìm được cơ hội phù hợp!</p>
                            <p style="margin-top: 24px;">Trân trọng,<br><strong>Ban Nhân sự</strong></p>
                        </div>
                        <div class="footer">
                            &copy; 2025 Internship Management. All rights reserved.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(applicantName, programName);
    }

    @Override
    @Async
    public void sendSprintCompletionEmail(Sprint sprint, List<Task> completedTasks, List<Task> incompleteTasks) {
        Team team = sprint.getTeam();
        if (team == null) {
            log.error("Sprint ID {} không có team, không thể gửi email.", sprint.getId());
            return;
        }

        List<String> recipients = new ArrayList<>();
        // Thêm email của mentor
        if (team.getMentor() != null && team.getMentor().getUser() != null) {
            recipients.add(team.getMentor().getUser().getEmail());
        }
        // Thêm email của các intern
        if (team.getInterns() != null) {
            team.getInterns().forEach(intern -> {
                if (intern.getUser() != null) {
                    recipients.add(intern.getUser().getEmail());
                }
            });
        }

        if (recipients.isEmpty()) {
            log.warn("Team ID {} không có thành viên nào để gửi email tổng kết sprint.", team.getId());
            return;
        }

        String subject = String.format("Tổng kết Sprint: %s (%s - %s)", sprint.getName(), sprint.getStartDate(), sprint.getEndDate());
        String htmlContent = getSprintCompletionEmailTemplate(sprint, completedTasks, incompleteTasks);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(fromMail, "Internship Management Bot"));
            helper.setTo(recipients.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Đã gửi email tổng kết sprint '{}' tới team '{}'", sprint.getName(), team.getName());
        } catch (Exception e) {
            log.error("Lỗi khi gửi email tổng kết sprint cho team {}: {}", team.getName(), e.getMessage());
        }
    }

     // Email báo cáo tổng quan cho thành viên trong nhóm về spirnt vừa làm 
    private String getSprintCompletionEmailTemplate(Sprint sprint, List<Task> completedTasks, List<Task> incompleteTasks) {
        int totalTasks = completedTasks.size() + incompleteTasks.size();
        long percentage = (totalTasks == 0) ? 100 : Math.round((double) completedTasks.size() / totalTasks * 100);

        StringBuilder incompleteTasksHtml = new StringBuilder();
        if (incompleteTasks.isEmpty()) {
            incompleteTasksHtml.append("<li>Không có công việc nào còn dang dở.</li>");
        } else {
            for (Task task : incompleteTasks) {
                incompleteTasksHtml.append(String.format("<li>%s (Trạng thái: %s)</li>", task.getName(), task.getStatus()));
            }
        }

        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Inter', sans-serif; background-color: #f4f7fc; color: #1f2a44; }
                        .container { max-width: 680px; margin: 20px auto; background: #ffffff; border-radius: 16px; box-shadow: 0 8px 24px rgba(0,0,0,0.1); }
                        .header { background: linear-gradient(135deg, #007BFF, #0056b3); color: #ffffff; padding: 40px 20px; text-align: center; border-radius: 16px 16px 0 0; }
                        .header h1 { font-size: 28px; }
                        .content { padding: 32px 24px; }
                        .summary { text-align: center; margin-bottom: 32px; }
                        .summary .percentage { font-size: 48px; font-weight: 700; color: #007BFF; }
                        .summary .details { font-size: 16px; color: #64748b; }
                        .section { margin-bottom: 24px; }
                        .section h2 { font-size: 20px; color: #0056b3; border-bottom: 2px solid #f1f5f9; padding-bottom: 8px; margin-bottom: 16px; }
                        .section ul { list-style-type: none; padding-left: 0; }
                        .section li { background-color: #f8f9fa; border-left: 4px solid #ffc107; margin-bottom: 8px; padding: 12px 16px; border-radius: 4px; }
                        .footer { text-align: center; padding: 20px; font-size: 12px; color: #64748b; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Báo cáo tổng kết Sprint</h1>
                            <p>%s: %s đến %s</p>
                        </div>
                        <div class="content">
                            <div class="summary">
                                <div class="percentage">%d%%</div>
                                <div class="details">Tỷ lệ hoàn thành</div>
                                <p>%d / %d công việc đã được hoàn thành</p>
                            </div>
                            <div class="section">
                                <h2>Các công việc chưa hoàn thành</h2>
                                <ul>
                                    %s
                                </ul>
                            </div>
                            <p>Đây là email tự động. Vui lòng không trả lời.</p>
                        </div>
                        <div class="footer">
                            &copy; 2025 Internship Management Bot
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(sprint.getName(), sprint.getStartDate(), sprint.getEndDate(), percentage, completedTasks.size(), totalTasks, incompleteTasksHtml.toString());
    }

}
