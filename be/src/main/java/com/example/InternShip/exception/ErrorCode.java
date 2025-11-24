package com.example.InternShip.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //INVALID
    ROLE_INVALID("Vai trò không hợp lệ."),
    TOKEN_INVALID("Token không hợp lệ"),
    USERNAME_INVALID("Tên đăng nhập không hợp lệ"),
    EMAIL_INVALID("Email không hợp lệ"),
    PASSWORD_INVALID("Mật khấu không hợp lệ"),
    FULL_NAME_INVALID("Họ tên không hợp lệ"),
    PHONE_INVALID("Số điện thoại không hợp lệ"),
    VERIFICATION_CODE_INVALID("Mã xác thực không hợp lệ"),
    STATUS_INVALID("trạng thái không hợp lệ "),
    STATUS_INTERNSHIP_PROGRAM_INVALID("trạng thái kì thực tập không hợp lệ "),
    MAJOR_INVALID ("Chuyên ngành không hợp lệ"),
    UNIVERSITY_INVALID ("Trường học không hợp lệ"),
    TIME_APPLY_INVALID("Đã quá hạn nộp đơn thực tập"),
    STATUS_APPLICATION_INVALID("Trạng thái cập nhật không hợp lệ cho: "),
    ACTION_INVALID("Hành động không hợp lệ"),
    EDIT_USER_INVALID("Không thể sửa thông tin admin"),
    LIST_APPLICATION_INVALID("Phải chọn ít nhất 1 hồ sơ"),
    INTERN_INVALID("Thực tập sinh không hợp lệ"),
    INTERNSHIP_PROGRAM_INVALID("Tên kì thực tập không hợp lệ"),
    TIME_INVALID("Thời gian không hợp lệ"),
    DEPARTMENT_INVALID("Phòng ban không hợp lệ"),
    MENTOR_INVALID("Mentor không hợp lệ"),
    PROGRAM_INVALID("Kì thực tập không hợp lệ"),
    TEAM_NAME_INVALID("Tên nhóm không hợp lệ"),
    LIST_INTERN_INVALID("Danh sách thực tập sinh không hợp lệ"),
    LIST_TASK_INVALID("Danh sách nhiệm vụ không hợp lệ"),
    NAME_INTERN_PROGRAM_INVALID("Tên kì thực tập không hợp lệ"),
    TIME_START_INVALID("Thời gian bắt đầu không hợp lệ"),
    TIME_END_INVALID("Thời gian kết thúc không hợp lệ"),
    TYPE_LEAVE_APPLICATION_INVALID("Loại đơn nghỉ phép không hợp lệ"),
    SPRINT_INVALID("Sprint không hợp lệ"),
    FILE_INVALID("Tệp quá lớn! Kích thước tối đa: 10MB"),
    TYPE_FILE_INVALID("Chỉ chấp nhận file .pdf, .docx, .doc, png, jpg, jpeg"),
    TYPE_AVATAR_FILE_INVALID("Chỉ chấp nhận file ảnh (PNG, JPG, GIF)!"),
    SUPPORT_REQUEST_STATUS_INVALID("Trạng thái không hợp lệ để thực hiện!"),

    //EXISTED
    USERNAME_EXISTED("Tên đăng nhập đã tồn tại."),
    EMAIL_EXISTED("Email đã tồn tại."),
    APPLICATION_EXISTED("Hồ sơ đã tồn tại"),
    TEAM_NAME_EXISTED("Tên nhóm đã tồn tại"),
    
    //NOT_EXISTED
    USER_NOT_EXISTED("Người dùng không tồn tại."),
    VERIFICATION_CODE_NOT_EXISTED("Mã xác thực không tồn tại."),
    INTERN_NOT_EXISTED("Thực tập sinh không tồn tại."),
    UNIVERSITY_NOT_EXISTED ("Trường học không tồn tại"),
    MAJOR_NOT_EXISTED ("Ngành học không tồn tại"),
    INTERNSHIP_TERM_NOT_EXISTED("Kì thực tập không tồn tại"),
    INTERNSHIP_APPLICATION_NOT_EXISTED("Đơn đăng ký không tồn tại"),
    PROGRAM_NOT_EXISTED("Chương trình không tồn tại"),
    TEAM_NOT_EXISTED("Nhóm không tồn tại"),
    MENTOR_NOT_EXISTED("Mentor không tồn tại"),
    DEPARTMENT_NOT_EXISTED("Phòng ban không tồn tại."),
    INTERNSHIP_PROGRAM_NOT_EXISTED("Kì thực tập không tồn tại"),
    WORK_SCHEDULE_NOT_EXISTED("Lịch làm việc không tồn tại"),
    HR_NOT_EXISTS("HR không tồn tại"),
    LEAVE_APPLICATION_NOT_EXISTS("Đơn xin nghỉ phép không tồn tại"),
    SPRINT_NOT_EXISTS("Sprint không tồn tại"),
    TASK_NOT_EXISTS("Task không tồn tại"),
    CONVERSATION_NOT_EXISTS("Cuộc hội thoại không tồn tại"),
    SUPPORT_REQUEST_NOT_EXISTS("Đơn yêu cầu hỗ trợ không tồn tại"),

    //NOT_NULL
    UNIVERSITY_NOT_NULL ("Trường học không được để trống"),
    TEAM_NOT_NULL ("Nhóm không được để trống"),
    MAJOR_NOT_NULL ("Ngành học không được để trống"),
    STATUS_NOT_BLANK("Trạng thái không được để trống"),
    DAY_OF_WEEK_NOT_NULL("Ngày trong tuần không được để trống"),
    LEAVE_APPLICATION_TYPE_NOT_NULL("Loại đơn không được để trống"),
    DATE_LEAVE_NOT_NULL("Ngày nghỉ phép không được để trống"),
    REASON_NOT_NULL("Lý do nghỉ không được để trống"),
    HR_NOT_NULL("Phải chọn ít nhất 1 HR"),
    ID_NOT_NULL("Mày, mày tính làm gì?"),
    REASON_REJECT_NOT_NULL("Phải có ít nhất 1 lý do chứ?"),
    FILE_NOT_NULL("Tệp không được để trống"),
    SUPPORT_REQUEST_TITLE_NOT_NULL("Tiêu đề yêu cầu hỗ trợ không được để trống"),
    SUPPORT_REQUEST_DESCRIPTION_NOT_NULL("Nội dung yêu cầu hỗ trợ không được để trống"),
    
    //FAILED
    VERIFICATION_FAILED("Xác thực thất bại"),
    VERIFICATION_CODE_SEND_FAILED("Gửi mã xác thực thất bại"),
    SUBMIT_FAILED("Gửi thất bại"),
    WITHDRAWAL_FAILED("Rút đơn thất bại"),
    SCHEDULER_FAILED("Lập lịch thất bại"),
    UPDATE_SPRINT_FAILED("Sửa thông tin sprint thất bại"),
    DELETE_SPRINT_FAILED("Xóa sprint thất bại"),
    UPDATE_TASK_FAILED("Không thể sửa task"),
    DELETE_TASK_FAILED("Không thể xóa task"),
    CANCEL_REQUEST_FAILED("Không thể hủy yêu cầu"),
    UPLOAD_FILE_FAILED("Tải lên tệp thất bại"),

    //UNAUTHENTICATED
    UNAUTHENTICATED("Đăng nhập thất bại"),
    USER_INACTIVE("Tài khoản đã bị vô hiệu hóa"), 
    UNAUTHORIZED_ACTION("Hành động không được phép"),
    NOT_PERMISSION("Bạn không có quyền thực hiện hành động này"),

    //OTHER
    INTERN_NOT_IN_TEAM("Thực tập sinh hiện không thuộc nhóm nào"),
    INTERN_NOT_FOUND("Không tìm thấy thông tin thực tập sinh"),
    ALREADY_CHECKED_IN_TODAY("Bạn đã check-in hôm nay rồi"),
    ALREADY_CHECKED_OUT_TODAY("Bạn đã check-out hôm nay rồi"),
    NOT_CHECKED_IN_TODAY("Bạn chưa check-in hôm nay"),
    INTERN_NOT_TEAM("Bạn chưa được gán vào nhóm nào, không thể chấm công"),
    SCHEDULE_NOT_SET_TODAY("Lịch chưa được thiết lập hôm nay"),
    CANNOT_CHECK_IN("Không thể check-in"),
    CANNOT_EVALUATE_SPRINT("Chưa có báo cáo chưa thể đánh giá sprint"),
    INTERN_NOT_IN_THIS_TEAM("Thực tập sinh hiện không thuộc nhóm này"),
    EVALUATION_BY_MENTOR_ONLY("Chỉ mentor mới có thể đánh giá"),
    NOT_SUPPORT_REQUEST("Không tìm thây yêu cầu"),
    ;
    private final String message;
}
