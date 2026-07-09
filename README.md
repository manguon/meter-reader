# Đọc Chỉ Số Đồng Hồ (Water/Electricity Meter Reader)

Ứng dụng Android chụp ảnh đồng hồ nước/điện và **tự động đọc số bằng OCR chạy hoàn toàn offline** (Google ML Kit Text Recognition — model được đóng gói sẵn trong app, không cần internet, không cần API key).

## Tính năng
- Chụp ảnh đồng hồ nước hoặc đồng hồ điện bằng camera (CameraX).
- Tự động nhận diện dãy số chỉ số bằng ML Kit (chạy trên máy, offline).
- Cho phép sửa lại số nếu app đọc sai trước khi lưu.
- Lưu lịch sử các lần đọc (loại đồng hồ, chỉ số, ngày giờ) bằng Room database.
- Lọc lịch sử theo loại đồng hồ (nước/điện), xoá từng dòng.
- Xuất lịch sử ra file CSV và chia sẻ qua email/Zalo/Drive...

## Yêu cầu
- Android Studio (bản Koala 2024.1 trở lên khuyến nghị).
- JDK 17 (đi kèm Android Studio).
- Thiết bị Android thật (khuyến nghị) hoặc máy ảo có camera, chạy Android 8.0 (API 26) trở lên.

## Cách chạy
1. Mở Android Studio → **Open** → chọn thư mục `MeterReader` (thư mục chứa file `settings.gradle.kts`).
2. Chờ Gradle sync xong (lần đầu cần internet để tải các thư viện).
3. Cắm điện thoại Android qua cáp USB, bật **USB debugging** trong Developer Options.
4. Bấm nút **Run ▶** trong Android Studio, chọn thiết bị của bạn.
5. Trong app: chọn "Chụp đồng hồ nước" hoặc "Chụp đồng hồ điện" → cấp quyền Camera → chụp ảnh mặt số đồng hồ → app tự đọc số → sửa lại nếu cần → **Lưu**.
6. Vào "Xem lịch sử" để xem lại các lần đọc, lọc theo loại, xoá, hoặc bấm biểu tượng chia sẻ để xuất CSV.

## Mẹo để đọc số chính xác hơn
- Chụp thẳng góc, đủ sáng, tránh loá/phản chiếu trên mặt kính đồng hồ.
- Đưa toàn bộ dãy số vào giữa khung hình, camera càng gần càng rõ càng tốt (nhưng vẫn nét, không bị mờ).
- App tự động chọn dãy số dài nhất tìm được trong ảnh làm chỉ số — nếu ảnh có thêm số khác (VD: mã sê-ri) gần chỉ số chính, hãy che bớt hoặc chụp cận cảnh riêng phần số đếm.
- Nếu đọc sai, luôn có thể sửa tay trước khi lưu.

## Cấu trúc project
```
MeterReader/
├── app/
│   └── src/main/java/com/example/meterreader/
│       ├── MainActivity.kt          # Điều hướng + màn hình chính
│       ├── camera/                  # Chụp ảnh bằng CameraX
│       ├── ocr/                     # Nhận diện số bằng ML Kit
│       ├── data/                    # Room entity/DAO/database
│       ├── viewmodel/               # ViewModel lưu/đọc lịch sử
│       ├── util/                    # Xuất CSV
│       └── ui/                      # Màn hình kết quả + lịch sử
```

## Mở rộng trong tương lai (gợi ý)
- Thêm biểu đồ tiêu thụ theo tháng.
- Nhắc lịch ghi chỉ số định kỳ (WorkManager + notification).
- Tự tính tiền điện/nước dựa trên biểu giá bậc thang.
- Đồng bộ dữ liệu lên Google Sheets/Drive tự động.
