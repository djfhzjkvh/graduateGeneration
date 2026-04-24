-- Widen file MIME type storage so standard xlsx uploads can persist successfully.
ALTER TABLE sys_file
    MODIFY COLUMN file_type VARCHAR(100) NULL;
