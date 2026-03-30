-- V8__create_answers.sql
CREATE TABLE answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exam_session_id UUID NOT NULL REFERENCES exam_sessions(id) ON DELETE CASCADE,
    question_id UUID NOT NULL REFERENCES questions(id),
    selected_answer VARCHAR(500),
    is_correct BOOLEAN,
    marks_awarded DECIMAL(4,2) DEFAULT 0,
    time_spent_seconds INTEGER NOT NULL DEFAULT 0,
    answered_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(exam_session_id, question_id)
);

CREATE INDEX idx_answers_session ON answers(exam_session_id);
CREATE INDEX idx_answers_question ON answers(question_id, is_correct);