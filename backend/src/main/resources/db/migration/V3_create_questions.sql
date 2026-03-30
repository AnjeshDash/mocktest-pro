-- V3_create_questions.sql
CREATE TABLE questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    section_id UUID NOT NULL REFERENCES sections(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    question_type VARCHAR(30) NOT NULL,
    options TEXT,
    correct_answer VARCHAR(500) NOT NULL,
    solution TEXT NOT NULL,
    solution_image_url VARCHAR(500),
    marks DECIMAL(4,2) NOT NULL DEFAULT 1.00,
    negative_marks DECIMAL(4,2) NOT NULL DEFAULT 0.00,
    difficulty VARCHAR(20) DEFAULT 'MEDIUM',
    topic VARCHAR(200),
    image_url VARCHAR(500),
    order_index INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_questions_section ON questions(section_id, order_index);
CREATE INDEX idx_questions_topic ON questions(topic);