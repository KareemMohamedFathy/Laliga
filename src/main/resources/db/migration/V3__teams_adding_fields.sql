ALTER TABLE teams
ADD COLUMN  provider_team_id bigint;
ALTER TABLE teams
ADD CONSTRAINT unique_team_id UNIQUE (provider_team_id);

INSERT INTO teams (name, provider_team_id)
VALUES
    -- 2025/26 teams
    ('Girona', 298),
    ('Vallecano', 87),
    ('Villarreal', 94),
    ('Oviedo', 1048),
    ('Mallorca', 89),
    ('Barcelona', 81),
    ('Alaves', 263),
    ('Levante', 88),
    ('Valencia', 95),
    ('Sociedad', 92),
    ('Celta', 558),
    ('Getafe', 82),
    ('Ath Bilbao', 77),
    ('Sevilla', 559),
    ('Espanol', 80),
    ('Ath Madrid', 78),
    ('Elche', 285),
    ('Betis', 90),
    ('Real Madrid', 86),
    ('Osasuna', 79),

    -- New teams in 2026/27
    ('Málaga CF', 84),
    ('RC Deportivo La Coruña', 560),
    ('Real Racing Club de Santander', 5335)

ON CONFLICT (name)
    DO UPDATE SET provider_team_id = EXCLUDED.provider_team_id;