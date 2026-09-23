-- ==========================================
-- V4: Seed de Servicios, Relaciones y Horarios Base
-- ==========================================

-- Servicio
INSERT INTO services (id, name, description, price, category, duration_minutes, buffer_minutes, is_active)
VALUES ('66666666-6666-6666-6666-666666666666', 'Corte Clásico', 'Corte tradicional a tijera o máquina', 10000.00, 'Cortes', 55, 5, true);

-- Vincular el servicio al barbero (ID de V3)
INSERT INTO staff_services (staff_id, service_id)
VALUES ('55555555-5555-5555-5555-555555555555', '66666666-6666-6666-6666-666666666666');

-- Rutinas de horario (Schedules) para tener disponibilidad que probar
INSERT INTO schedules (staff_id, branch_id, day_of_week, start_time, end_time)
VALUES
    ('55555555-5555-5555-5555-555555555555', '44444444-4444-4444-4444-444444444444', 2, '08:00', '12:00'),
    ('55555555-5555-5555-5555-555555555555', '44444444-4444-4444-4444-444444444444', 2, '16:00', '20:00');