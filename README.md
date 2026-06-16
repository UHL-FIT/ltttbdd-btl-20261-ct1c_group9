# StudySync
Ứng dụng quản lý học tập dành cho sinh viên
Phiên bản 1.0.0 — Android — Jetpack Compose — Material Design 3


# Giới thiệu

StudySync là ứng dụng di động trên nền tảng Android giúp sinh viên ghi chép, theo dõi và quản lý lịch học cũng như lịch thi một cách khoa học và trực quan. Ứng dụng cho phép người dùng tổ chức thời khóa biểu hàng tuần, nhận nhắc nhở thi cử kịp thời và theo dõi tiến độ học tập qua các thống kê chi tiết.

Toàn bộ tính năng hoạt động hoàn toàn Offline, không cần kết nối Internet.


# Tính năng chính

Quản lý thời khóa biểu
Thêm, chỉnh sửa và xóa các buổi học theo từng ngày trong tuần với đầy đủ thông tin: tên môn học, giảng viên, phòng học, giờ bắt đầu, giờ kết thúc và màu sắc nhận diện.

Nhắc nhở lịch thi
Quản lý danh sách các kỳ thi sắp tới với hệ thống đếm ngược trực quan. Ứng dụng tự động lên lịch thông báo (AlarmManager) khi thêm kỳ thi và tự động hủy thông báo khi kỳ thi bị xóa.

Trang chủ thông minh
Hiển thị lịch học trong ngày, top 3 kỳ thi sắp tới và thống kê tổng quan, tất cả cập nhật theo thời gian thực.

Thống kê học tập
Biểu đồ cột phân bổ lịch học theo từng ngày trong tuần, đếm ngược kỳ thi gần nhất và danh sách tổng hợp kỳ thi theo mức độ ưu tiên.

Quản lý nhập liệu
Form thêm mới môn học và lịch thi với đầy đủ validation, tích hợp DatePicker và TimePicker trực quan.


# Yêu cầu thiết bị

    Hệ điều hành   : Android 8.0 (API level 26) trở lên
    Dung lượng     : Tối thiểu 20 MB dung lượng trống
    Kết nối mạng   : Không bắt buộc (hoạt động hoàn toàn Offline)


# Cài đặt và Chạy ứng dụng

Bước 1. Clone repository về máy:

    git clone https://github.com/hatoblade-dev/StudySync-Nhom9.git

Bước 2. Mở thư mục dự án bằng Android Studio.

Bước 3. Chờ Android Studio đồng bộ các tệp cấu hình Gradle hoàn tất.

Bước 4. Chuẩn bị thiết bị:
    Khởi chạy máy ảo Android (Emulator), hoặc
    Kết nối điện thoại thật qua USB và bật USB Debugging.

Bước 5. Nhấn nút Run app hoặc tổ hợp phím Shift + F10.

Bước 6. Ứng dụng khởi động và hiển thị thẳng màn hình Trang chủ.


# Cấu trúc màn hình

    Trang chủ    —  Lịch học hôm nay, kỳ thi sắp tới, thống kê nhanh
    Lịch học     —  Thời khóa biểu theo tuần (Thứ 2 đến Chủ nhật)
    Lịch thi     —  Danh sách kỳ thi, xem chi tiết, chỉnh sửa, xóa
    Quản lý      —  Form thêm mới môn học và lịch thi
    Thống kê     —  Biểu đồ và phân tích dữ liệu học tập
    Giới thiệu   —  Thông tin ứng dụng và danh sách thành viên nhóm


# Công nghệ sử dụng

    Jetpack Compose    —  Xây dựng toàn bộ giao diện UI
    Material Design 3  —  Hệ thống thiết kế, màu sắc, typography
    Room Database      —  Lưu trữ dữ liệu cục bộ (SQLite)
    ViewModel          —  Quản lý trạng thái, data flow một chiều (UDF)
    StateFlow          —  Phát dữ liệu từ ViewModel xuống UI
    Navigation Compose —  Điều hướng giữa các màn hình
    AlarmManager       —  Lên lịch thông báo nhắc thi
    Kotlin Coroutines  —  Xử lý bất đồng bộ


# Kiến trúc ứng dụng

Ứng dụng tuân theo mô hình MVVM (Model - View - ViewModel) kết hợp Unidirectional Data Flow (UDF):

    Room Database  →  StudyViewModel  →  StateFlow  →  Composable UI
                               ↑                              ↓
                       ViewModel Functions  ←  User Events (click, input)


# Thành viên nhóm

    Hoàng Trần Khánh Vinh   MSSV: 23DH201145   Team Leader
    Vũ Gia Phong             MSSV: 23DH201133   Android Dev
    Lê Hoàng Hải             MSSV: 23DH201117   Tester
    Vũ Đăng Duy              MSSV: 23DH201109   Android Dev


# Thông tin dự án

    Môn học    : Lập trình thiết bị di động
    Năm học    : 2025 – 2026
    Phiên bản  : 1.0.0
    Nền tảng   : Android (Jetpack Compose)
    Giao diện  : Material Design 3
