-- Extensiones necesarias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "btree_gist";

-- ==========================================
-- 1. SUCURSALES (Branches)
-- ==========================================
CREATE TABLE branches (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          name VARCHAR(255) NOT NULL,
                          address TEXT,
                          phone VARCHAR(50),
                          timezone VARCHAR(50) DEFAULT 'America/Argentina/Buenos_Aires',
                          map_iframe_url TEXT,
                          google_maps_url TEXT,
                          is_active BOOLEAN DEFAULT true,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 2. SERVICIOS (Services)
-- ==========================================
CREATE TABLE services (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
                          category VARCHAR(100) NOT NULL DEFAULT 'Cortes',
                          duration_minutes INTEGER NOT NULL,
                          buffer_minutes INTEGER NOT NULL DEFAULT 0, -- tiempo de limpieza/descanso post-turno
                          is_recommended BOOLEAN DEFAULT false,
                          is_active BOOLEAN DEFAULT true,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 3. USUARIOS DEL SISTEMA (Admins / Staff logins)
-- ==========================================
-- NOTA: la autorizacion (que puede hacer cada usuario) NO vive aca.
-- Esta tabla es pura autenticacion (quien es la persona). Ver seccion RBAC mas abajo.
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       is_active BOOLEAN DEFAULT true,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       last_login_at TIMESTAMP WITH TIME ZONE
);

-- ==========================================
-- 4. BARBEROS / PERSONAL (Staff)
-- ==========================================
CREATE TABLE staff (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       user_id UUID UNIQUE REFERENCES users(id) ON DELETE SET NULL,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       phone VARCHAR(50),
                       email VARCHAR(255),
                       role_title VARCHAR(100) DEFAULT 'Barbero',
                       image_url VARCHAR(255),
                       instagram_url VARCHAR(255),
                       is_active BOOLEAN DEFAULT true,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE staff_branches (
                                staff_id UUID NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
                                branch_id UUID NOT NULL REFERENCES branches(id) ON DELETE CASCADE,
                                PRIMARY KEY (staff_id, branch_id)
);

CREATE TABLE staff_services (
                                staff_id UUID NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
                                service_id UUID NOT NULL REFERENCES services(id) ON DELETE CASCADE,
                                PRIMARY KEY (staff_id, service_id)
);

-- ==========================================
-- 5. HORARIOS Y EXCEPCIONES (Schedules)
-- ==========================================
CREATE TABLE schedules (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           staff_id UUID NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
                           branch_id UUID NOT NULL REFERENCES branches(id) ON DELETE CASCADE,
                           day_of_week INTEGER NOT NULL CHECK (day_of_week BETWEEN 0 AND 6), -- 0=Domingo
                           start_time TIME NOT NULL,
                           end_time TIME NOT NULL,
                           is_active BOOLEAN DEFAULT true,
                           CONSTRAINT check_valid_hours CHECK (end_time > start_time)
);

CREATE TABLE schedule_exceptions (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                     staff_id UUID NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
                                     branch_id UUID REFERENCES branches(id) ON DELETE CASCADE,
                                     exception_date DATE NOT NULL,
                                     is_unavailable BOOLEAN DEFAULT true,
                                     start_time TIME,
                                     end_time TIME,
                                     reason VARCHAR(255)
);

-- ==========================================
-- 6. CLIENTES (Customers)
-- ==========================================
CREATE TABLE customers (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100),
                           phone VARCHAR(50) UNIQUE NOT NULL,
                           email VARCHAR(255),
                           notes TEXT,
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 7. TURNOS / CITAS (Appointments)
-- ==========================================
CREATE TABLE appointments (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              branch_id UUID NOT NULL REFERENCES branches(id),
                              staff_id UUID NOT NULL REFERENCES staff(id),
                              service_id UUID NOT NULL REFERENCES services(id),
                              customer_id UUID NOT NULL REFERENCES customers(id),
                              start_at TIMESTAMP WITH TIME ZONE NOT NULL,
                              end_at TIMESTAMP WITH TIME ZONE NOT NULL,
                              status VARCHAR(50) DEFAULT 'PENDING', -- 'PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'NO_SHOW'
                              notes TEXT,
                              cancel_reason VARCHAR(255),
                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT check_appointment_times CHECK (end_at > start_at),
                              CONSTRAINT prevent_staff_overbooking EXCLUDE USING gist (
        staff_id WITH =,
        tstzrange(start_at, end_at, '[)') WITH &&
    ) WHERE (status IN ('PENDING', 'CONFIRMED'))
);

-- ==========================================
-- 8. VERIFICACIONES OTP Y LOGS
-- ==========================================
CREATE TABLE otp_verifications (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   phone_number VARCHAR(50) NOT NULL,
                                   otp_code VARCHAR(10) NOT NULL,
                                   expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                   is_verified BOOLEAN NOT NULL DEFAULT false,
                                   created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE appointment_history (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                     appointment_id UUID NOT NULL REFERENCES appointments(id) ON DELETE CASCADE,
                                     previous_status VARCHAR(50),
                                     new_status VARCHAR(50) NOT NULL,
                                     changed_by VARCHAR(100) NOT NULL,
                                     action_description VARCHAR(255),
                                     created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notification_logs (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   appointment_id UUID REFERENCES appointments(id) ON DELETE SET NULL,
                                   recipient VARCHAR(255) NOT NULL,
                                   type VARCHAR(50) NOT NULL,
                                   channel VARCHAR(50) NOT NULL,
                                   content TEXT NOT NULL,
                                   status VARCHAR(50) NOT NULL,
                                   created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 9. RBAC: AUTORIZACION (separada de la autenticacion de la seccion 3)
-- ==========================================
CREATE TABLE roles (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       code VARCHAR(50) UNIQUE NOT NULL,   -- 'ADMIN', 'BARBER', 'RECEPTIONIST', etc.
                       name VARCHAR(100) NOT NULL,
                       description TEXT,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE permissions (
                             id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             code VARCHAR(100) UNIQUE NOT NULL,  -- 'APPOINTMENTS_CANCEL_ANY', 'STAFF_MANAGE', etc.
                             description TEXT
);

CREATE TABLE role_permissions (
                                  role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
                                  permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
                                  PRIMARY KEY (role_id, permission_id)
);

-- Un usuario puede tener mas de un rol (ej: barbero que ademas administra)
CREATE TABLE user_roles (
                            user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                            role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
                            PRIMARY KEY (user_id, role_id)
);

-- Seed de roles
INSERT INTO roles (code, name, description) VALUES
                                                ('ADMIN', 'Administrador', 'Acceso total al sistema'),
                                                ('BARBER', 'Barbero', 'Gestiona su propia agenda y turnos');

-- Seed de permisos
INSERT INTO permissions (code, description) VALUES
                                                ('APPOINTMENTS_VIEW_ANY',    'Ver turnos de cualquier barbero'),
                                                ('APPOINTMENTS_VIEW_OWN',    'Ver los propios turnos'),
                                                ('APPOINTMENTS_CREATE',      'Crear turnos'),
                                                ('APPOINTMENTS_CANCEL_ANY',  'Cancelar turnos de cualquier barbero'),
                                                ('APPOINTMENTS_CANCEL_OWN',  'Cancelar los propios turnos'),
                                                ('APPOINTMENTS_MANAGE_ANY',  'Confirmar/completar/marcar no-show en turnos de cualquier barbero'),
                                                ('APPOINTMENTS_MANAGE_OWN',  'Confirmar/completar/marcar no-show en los propios turnos'),
                                                ('STAFF_MANAGE',             'Crear/editar/desactivar barberos'),
                                                ('BRANCH_MANAGE',            'Crear/editar/desactivar sucursales'),
                                                ('SERVICE_MANAGE',           'Crear/editar/desactivar servicios'),
                                                ('SCHEDULE_MANAGE_ANY',      'Editar el horario de cualquier barbero'),
                                                ('SCHEDULE_MANAGE_OWN',      'Editar el propio horario'),
                                                ('REPORTS_VIEW',             'Ver reportes y estadisticas');

-- ADMIN: todos los permisos
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r CROSS JOIN permissions p WHERE r.code = 'ADMIN';

-- BARBER: solo lo propio
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.code = 'BARBER'
  AND p.code IN (
                 'APPOINTMENTS_VIEW_OWN', 'APPOINTMENTS_CREATE',
                 'APPOINTMENTS_CANCEL_OWN', 'APPOINTMENTS_MANAGE_OWN',
                 'SCHEDULE_MANAGE_OWN'
    );

-- ==========================================
-- 10. INDICES DE RENDIMIENTO
-- ==========================================
CREATE INDEX idx_appointments_staff_range
    ON appointments(staff_id, start_at, end_at)
    WHERE status != 'CANCELLED';

CREATE INDEX idx_appointments_customer
    ON appointments(customer_id, start_at);

CREATE INDEX idx_customers_phone ON customers(phone);

CREATE INDEX idx_schedules_staff ON schedules(staff_id, day_of_week);
CREATE INDEX idx_exceptions_staff_date ON schedule_exceptions(staff_id, exception_date);
CREATE INDEX idx_otp_phone ON otp_verifications(phone_number);