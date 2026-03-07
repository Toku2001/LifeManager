CREATE TABLE IF NOT EXISTS sleep_analysis_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    metric_name TEXT NOT NULL,
    metric_units TEXT NOT NULL,
    source TEXT,
    date TEXT NOT NULL,
    sleep_start TEXT,
    sleep_end TEXT,
    in_bed_start TEXT,
    in_bed_end TEXT,
    asleep REAL,
    awake REAL,
    rem REAL,
    total_sleep REAL,
    core REAL,
    in_bed REAL,
    deep REAL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS heart_rate_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    metric_name TEXT NOT NULL,
    metric_units TEXT NOT NULL,
    source TEXT,
    date TEXT NOT NULL,
    max REAL,
    min REAL,
    avg REAL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
