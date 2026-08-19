INSERT INTO users (id, email, password_hash, is_active) VALUES
                                                            ('11111111-1111-1111-1111-111111111111', 'admin@test.com', '$2a$10$p6Su0nJe5dCQ8Fad1Ej1keaHHnMcojaSD4XpiWxVijLcsEVTEiBaq', true),
                                                            ('22222222-2222-2222-2222-222222222222', 'barbero@test.com', '$2a$10$p6Su0nJe5dCQ8Fad1Ej1keaHHnMcojaSD4XpiWxVijLcsEVTEiBaq', true),
                                                            ('33333333-3333-3333-3333-333333333333', 'sinrol@test.com', '$2a$10$p6Su0nJe5dCQ8Fad1Ej1keaHHnMcojaSD4XpiWxVijLcsEVTEiBaq', true);

INSERT INTO user_roles (user_id, role_id)
SELECT '11111111-1111-1111-1111-111111111111', id FROM roles WHERE code = 'ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT '22222222-2222-2222-2222-222222222222', id FROM roles WHERE code = 'BARBER';