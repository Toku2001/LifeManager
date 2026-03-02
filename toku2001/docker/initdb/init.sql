CREATE TABLE IF NOT EXISTS health_auto_exports (
    id INTEGER PRIMARY KEY,
    setting_key TEXT NOT NULL UNIQUE,
    status Integer NOT NULL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT OR IGNORE INTO health_auto_exports (id, setting_key, status)
VALUES (1, 'dummyKey', 1);