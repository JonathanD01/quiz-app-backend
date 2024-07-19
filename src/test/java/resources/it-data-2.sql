-- Insert users
INSERT INTO users (id, firstname, lastname, email, password, account_locked, enabled, created_at, updated_at, role)
VALUES
(1000, 'John', 'Doe', 'john.doe@example.com', 'password123', false, true, '2023-06-10T10:00:00', '2023-06-10T10:00:00', 'USER'),
(2000, 'Jane', 'Smith', 'jane.smith@example.com', 'password456', true, false, '2023-06-11T11:00:00', '2023-06-11T11:00:00', 'ADMIN')
ON CONFLICT DO NOTHING;


-- Insert quizzes
INSERT INTO quizzes (id, link, status, creator_id, title, description, is_shared, created_at, updated_at)
VALUES
(100, '550e8400-e29b-41d4-a716-446655440000', 'COMPLETED', 1000, '50% smart', 'A quiz about general knowledge.', false, '2023-06-12T10:00:00', '2023-06-12T10:00:00'),
(200, '550e8400-e29b-41d4-a716-446655440001', 'PENDING', 1000, 'Math Quiz', 'A quiz about basic mathematics.', false, '2023-06-13T11:00:00', '2023-06-13T11:00:00'),
(300, '550e8400-e29b-41d4-a716-446655440002', 'COMPLETED', 2000, 'History Quiz I', 'A quiz about world history. I', false, '2023-07-14T12:00:00', '2023-07-14T12:00:00'),
(400, '550e8400-e29b-41d4-a716-446655440003', 'COMPLETED', 2000, 'History Quiz II', 'A quiz about world history. II', false, '2023-08-14T12:00:00', '2023-08-14T12:00:00'),
(500, '550e8400-e29b-41d4-a716-446655440004', 'COMPLETED', 1000, 'History Quiz II', 'A quiz about world history. III', false, '2023-09-14T12:00:00', '2023-09-14T12:00:00'),
(600, '550e8400-e29b-41d4-a716-446655440005', 'COMPLETED', 1000, 'Shared Quiz', 'A shared quiz', true, '2023-10-14T12:00:00', '2023-10-14T12:00:00'),
(700, '550e8400-e29b-41d4-a716-446655440006', 'COMPLETED', 1000, 'Shared Quiz II', 'A shared quiz II', false, '2023-11-14T12:00:00', '2023-11-14T12:00:00'),
(800, '550e8400-e29b-41d4-a716-446655440008', 'COMPLETED', 1000, 'Shared Quiz III', 'A shared quiz III', false, '2023-12-14T12:00:00', '2023-12-14T12:00:00')
 ON CONFLICT DO NOTHING;

-- Insert quiz questions
INSERT INTO quiz_questions (id, quiz_id, question_text, created_at, updated_at)
VALUES
(100, 100, 'What is the capital of France?', '2023-06-12T10:05:00', '2023-06-12T10:05:00'),
(200, 100, 'Who wrote "To Kill a Mockingbird"?', '2023-06-12T10:10:00', '2023-06-12T10:10:00'),
(300, 200, 'What is 5 + 7?', '2023-06-13T11:05:00', '2023-06-13T11:05:00'),
(400, 300, 'Who was the first president of the United States?', '2023-06-14T12:05:00', '2023-06-14T12:05:00'),
(600, 700, 'Who was the first president of the United States!!', '2023-06-14T12:05:00', '2023-06-14T12:05:00'),
(700, 700, 'Who was the first president of the United States!!!', '2023-06-14T12:05:00', '2023-06-14T12:05:00'),
(800, 700, 'Who was the first president of the United States!!!!', '2023-06-14T12:05:00', '2023-06-14T12:05:00'),
(900, 700, 'Who was the first president of the United States!!!!!', '2023-06-14T12:05:00', '2023-06-14T12:05:00')
ON CONFLICT DO NOTHING;

-- Insert quiz answer options
INSERT INTO quiz_answer_options (id, quiz_question_id, answer_text, correct, created_at, updated_at)
VALUES
(100, 100, 'Paris', true, '2023-06-12T10:06:00', '2023-06-12T10:06:00'),
 (200, 100, 'London', false, '2023-06-12T10:07:00', '2023-06-12T10:07:00'),
 (300, 100, 'Berlin', false, '2023-06-12T10:08:00', '2023-06-12T10:08:00'),
 (400, 200, 'Harper Lee', true, '2023-06-12T10:11:00', '2023-01-12T10:11:00'),
 (500, 200, 'Mark Twain', false, '2023-07-12T10:12:00', '2023-02-12T10:12:00'),
 (600, 200, 'J.K. Rowling', false, '2023-08-12T10:13:00', '2023-03-12T10:13:00'),
 (700, 300, '12', true, '2023-06-13T11:06:00', '2023-06-13T11:06:00'),
 (800, 300, '10', false, '2023-06-13T11:07:00', '2023-06-13T11:07:00'),
 (900, 300, '14', false, '2023-06-13T11:08:00', '2023-06-13T11:08:00'),
 (1000, 400, 'George Washington', true, '2023-06-14T12:06:00', '2023-06-14T12:06:00'),
 (1100, 400, 'Thomas Jefferson', false, '2023-06-14T12:07:00', '2023-06-14T12:07:00'),
 (1200, 400, 'Abraham Lincoln', false, '2023-06-14T12:08:00', '2023-06-14T12:08:00'),
 (1300, 700, 'Abraham Lincoln', false, '2023-06-14T12:08:00', '2023-06-14T12:08:00'),
 (1400, 700, 'Abraham Lincoln', false, '2023-06-14T12:08:00', '2023-06-14T12:08:00'),
 (1500, 700, 'Abraham Lincoln', false, '2023-06-14T12:08:00', '2023-06-14T12:08:00'),
 (1600, 700, 'Abraham Lincoln', false, '2023-06-14T12:08:00', '2023-06-14T12:08:00')
 ON CONFLICT DO NOTHING;

-- Insert quiz attempts
INSERT INTO quiz_attempts (id, link, quiz_id, user_id, start_time, end_time, max_score, score, created_at, updated_at)
VALUES
(1000, '550e8400-e29b-41d4-a716-446655440010', 100, 1000, '2023-06-15T10:00:00', '2023-06-15T10:30:00', 10, 8, '2023-06-15T10:00:00', '2023-06-15T10:30:00'),
(2000, '550e8400-e29b-41d4-a716-446655440011', 100, 1000, '2023-06-15T11:00:00', '2023-06-15T11:30:00', 10, 9, '2023-06-15T11:00:00', '2023-06-15T11:30:00'),
(3000, '550e8400-e29b-41d4-a716-446655440012', 200, 1000, '2023-06-16T12:00:00', '2023-06-16T12:15:00', 10, 7, '2023-06-16T12:00:00', '2023-06-16T12:15:00'),
(4000, '550e8400-e29b-41d4-a716-446655440013', 300, 1000, '2023-06-17T13:00:00', '2023-06-17T13:30:00', 10, 6, '2023-06-17T13:00:00', '2023-06-17T13:30:00'),
(5000, '550e8400-e29b-41d4-a716-446655440015', 700, 2000, '2023-06-17T13:00:00', '2023-06-17T13:30:00', 10, 6, '2023-06-17T13:00:00', '2023-06-17T13:30:00'),
(6000, '550e8400-e29b-41d4-a716-446655440016', 700, 2000, '2023-06-17T13:00:00', '2023-06-17T13:30:00', 10, 6, '2023-06-17T13:00:00', '2023-06-17T13:30:00'),
(7000, '550e8400-e29b-41d4-a716-446655440017', 700, 2000, '2023-06-17T13:00:00', '2023-06-17T13:30:00', 10, 6, '2023-06-17T13:00:00', '2023-06-17T13:30:00'),
(8000, '550e8400-e29b-41d4-a716-446655440018', 700, 1000, '2023-06-17T13:00:00', '2023-06-17T13:30:00', 10, 6, '2023-06-17T13:00:00', '2023-06-17T13:30:00'),
(8000, '550e8400-e29b-41d4-a716-446655440019', 100, 1000, '2023-06-17T13:00:00', '2023-06-17T13:30:00', 10, 6, '2023-06-17T13:00:00', '2023-06-17T13:30:00'),
(9000, '550e8400-e29b-41d4-a716-446655440020', 100, 2000, '2023-06-17T13:00:00', '2023-06-17T13:30:00', 10, 6, '2023-06-17T13:00:00', '2023-06-17T13:30:00'),
(10000, '550e8400-e29b-41d4-a716-446655440021', 100, 1000, '2023-06-17T13:00:00', '2023-06-17T13:30:00', 10, 6, '2023-06-17T13:00:00', '2023-06-17T13:30:00')
ON CONFLICT DO NOTHING;