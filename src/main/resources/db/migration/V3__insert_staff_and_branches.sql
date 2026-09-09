-- ==========================================
-- V3: Seed de Sucursales y Staff de prueba
-- ==========================================

-- sucursal
INSERT INTO branches (id, name, address, phone, timezone, is_active)
VALUES ('44444444-4444-4444-4444-444444444444', 'Sucursal Centro', 'Av. Belgrano 123', '3854444444', 'America/Argentina/Buenos_Aires', true);

-- miembro del staff vinculado al usuario barbero de V2
INSERT INTO staff (id, user_id, first_name, last_name, phone, email, role_title, is_active)
VALUES ('55555555-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222222', 'Juan', 'Pérez', '3855555555', 'barbero@test.com', 'Barbero Senior', true);

-- Vincular el staff a la sucursal en la tabla intermedia (staff_branches)
INSERT INTO staff_branches (staff_id, branch_id)
VALUES ('55555555-5555-5555-5555-555555555555', '44444444-4444-4444-4444-444444444444');