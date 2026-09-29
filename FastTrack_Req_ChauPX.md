# REQ: Quiz Lite Backend (Hệ Thống Thi Trắc Nghiệm Hiệu Suất Cao)

| Field | Nội dung |
|---|---|
| Tác giả | ChauPX |
| Ngày | 2026-09-29 |
| Trạng thái | Approved |

## 1. Bối cảnh
- Vì sao chọn app này: Ứng dụng thi trắc nghiệm đòi hỏi khả năng xử lý đồng thời cực cao trong các kỳ thi tập trung. Ứng dụng sử dụng đủ các kỹ thuật thực tế để chịu tải: JWT Stateless, JPA + JSONB linh hoạt, Caching/Auto-save với Redis tốc độ cao (tránh tắc nghẽn Database), xử lý bất đồng bộ (Event-driven) với RabbitMQ, và Real-time Notification bằng WebSocket STOMP. Bên cạnh đó, ứng dụng tích hợp công nghệ AI (Gemini) thực tế.

## 2. Mục tiêu
- [x] Skill 1: REST API + Validation + DTO Pattern
- [x] Skill 2: JPA + N+1 Fix (@EntityGraph) + PostgreSQL JSONB
- [x] Skill 3: Auth JWT Stateless + @PreAuthorize + Filters
- [x] Skill 4: High-Throughput & Async Processing (Redis Hash + RabbitMQ Consumer)
- [x] Skill 5: AI Integration (Gọi LLM API + Xử lý JSON response + Retry logic)
- [x] Không phải mục tiêu: Kubernetes (K8s), Microservices phức tạp.

## 3. Techstack 
- Java: 17 / Spring Boot: 3.x
- DB: PostgreSQL / Cache (Auto-save): Redis (Spring Data Redis)
- Message Broker (Chấm điểm): RabbitMQ
- Build: Gradle / Deploy demo: Docker + docker-compose
- AI LLM: Google Gemini (Generative Language API)
- Frontend tích hợp: React + Vite + WebSocket Client

## 4. Phạm vi
- Must (bắt buộc chạy): Auth (Login/Register), CRUD Quizzes, Thực hiện bài thi (Take Quiz), Lưu tạm đáp án bằng Redis siêu tốc, Nộp bài qua Queue.
- Should (có càng tốt): AI Generate Questions hàng loạt, Giới hạn thời gian (Time Limit + Auto-submit), Gửi điểm realtime qua WebSocket (STOMP).
- Out-scope (nói rõ không làm): Tích hợp cổng thanh toán (Payment), Phân tích thống kê BI phức tạp, K8s deployment.

## 5. Domain / Entities
| Entity | Field chính | Quan hệ |
|---|---|---|
| User | id, username, password, roles | 1-N ExamResult |
| Quiz | id, title, description, isActive, timeLimit, startTime, endTime | 1-N Question |
| Question | id, type, details (JSONB), correctAnswer | N-1 Quiz |
| ExamResult | id, score, evidence (JSONB), submittedAt | N-1 User, N-1 Quiz |
| SystemConfig | key, value | (Dùng lưu AI API Key) |

- Data Modeling: 
  - `details` trong Question được lưu bằng JSONB `@JdbcTypeCode(SqlTypes.JSON)` giúp mở rộng nhiều cấu trúc câu hỏi.
  - `evidence` trong ExamResult lưu toàn bộ bằng chứng đáp án của học sinh cũng bằng JSONB.

## 6. API (tối thiểu)
| # | Method + Path | Auth | Mục đích |
|---|---|---|---|
| 1 | POST /api/v1/auth/login, /register | public | Xác thực và cấp JWT |
| 2 | POST /api/v1/quizzes | ADMIN | Quản lý đề thi + Tránh N+1 |
| 3 | POST /api/v1/ai/generate-questions | ADMIN | Tích hợp gọi AI LLM tạo đề |
| 4 | POST /api/v1/exams/{quizId}/.../answers | USER | Auto-save bằng Redis Hash (O(1)) |
| 5 | POST /api/v1/exams/{quizId}/submit | USER | Đẩy event sang RabbitMQ (202 ACCEPTED) |
| 6 | GET /api/v1/exams/my-results, /results | USER/ADMIN | Xem điểm, RBAC (Data Isolation) |

## 7. Business Rule 
- BR-01 (Time Validation): Bài thi chưa đến `startTime` hoặc đã qua `endTime` thì chặn thi. Thời gian đếm ngược (Time Limit) báo về frontend tự động nộp bài khi hết giờ.
- BR-02 (High Concurrency): Khi hàng ngàn học sinh click chọn đáp án cùng lúc, tuyệt đối không gọi `UPDATE` xuống DB. Ghi thẳng vào bộ nhớ đệm RAM thông qua Redis Hash.
- BR-03 (Data Isolation): Role USER chỉ có thể xem `my-results`, ADMIN được xem tất cả kết quả của mọi người.

## 8. Yêu cầu kỹ thuật
- Auth: JWT Stateless, password encode Bcrypt.
- Phân quyền: @PreAuthorize role ADMIN/USER.
- Data: Lấy nguyên đề thi cùng hàng trăm câu hỏi mà không bị N+1 bằng `@EntityGraph`. Tận dụng PostgreSQL JSONB.
- Kiến trúc xử lý: Nộp bài không xử lý đồng bộ, dùng `RabbitTemplate` đẩy event vào queue và `ExamProcessorService` lắng nghe `@RabbitListener` để chấm điểm ngầm.
- Real-time: Sau khi chấm xong, bắn thông báo về qua WebSocket để hiện Toast trên màn hình User.
- Bảo mật thông tin: Không hardcode API Key của AI trên mã nguồn, bắt buộc load từ DB (`SystemConfig`).

## 9. Tiêu chí Pass / Fail 
- [x] Toàn bộ Endpoints core chạy trơn tru, JWT phân quyền chuẩn xác.
- [x] Docker compose up thành công (PostgreSQL + Redis + RabbitMQ).
- [x] API Auto-save đáp án thành công vào bộ nhớ đệm Redis, đọc được từ Redis.
- [x] API Submit nộp bài vào Queue, Consumer chấm điểm đúng và lưu kết quả (evidence).
- [x] Tích hợp AI (Gemini) gọi và parse kết quả tạo câu hỏi trắc nghiệm hoàn hảo.
- [x] Code không bị lỗi N+1 Query. Lịch sử bài làm hiển thị JSON chính xác.
