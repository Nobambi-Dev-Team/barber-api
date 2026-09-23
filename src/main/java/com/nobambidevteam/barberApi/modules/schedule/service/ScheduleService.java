package com.nobambidevteam.barberApi.modules.schedule.service;

import com.nobambidevteam.barberApi.exceptions.BusinessRuleException;
import com.nobambidevteam.barberApi.exceptions.ResourceNotFoundException;
import com.nobambidevteam.barberApi.modules.appointment.entity.AppointmentEntity;
import com.nobambidevteam.barberApi.modules.appointment.repository.IAppointmentRepository;
import com.nobambidevteam.barberApi.modules.branch.entity.BranchEntity;
import com.nobambidevteam.barberApi.modules.branch.repository.IBranchRepository;
import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleCreateDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleUpdateDto;
import com.nobambidevteam.barberApi.modules.schedule.entity.DateExceptionEntity;
import com.nobambidevteam.barberApi.modules.schedule.entity.ScheduleEntity;
import com.nobambidevteam.barberApi.modules.schedule.mapper.ScheduleMapper;
import com.nobambidevteam.barberApi.modules.schedule.repository.IDateExceptionRepository;
import com.nobambidevteam.barberApi.modules.schedule.repository.IScheduleRepository;
import com.nobambidevteam.barberApi.modules.schedule.service.interfaz.IScheduleService;
import com.nobambidevteam.barberApi.modules.service.entity.ServiceEntity;
import com.nobambidevteam.barberApi.modules.service.repository.IServiceRepository;
import com.nobambidevteam.barberApi.modules.staff.entity.StaffEntity;
import com.nobambidevteam.barberApi.modules.staff.repository.IStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleService implements IScheduleService {

    private final IScheduleRepository scheduleRepository;
    private final IBranchRepository branchRepository;
    private final IServiceRepository serviceRepository;
    private final IStaffRepository staffRepository;
    private final IDateExceptionRepository dateExceptionRepository;
    private final IAppointmentRepository appointmentRepository;


    private static final ZoneId DEFAULT_ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    private record TimeBlock(LocalTime startTime, LocalTime endTime) {
    }

    private record DailyScheduleContext(List<TimeBlock> workingBlocks, List<DateExceptionEntity> parches) {
    }


    //-----------------------Save
    @Override
    @Transactional
    public ScheduleDto save(ScheduleCreateDto request) {

        // Obtener dependencias
        BranchEntity branchEntity = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la sucursal de id " + request.branchId()));

        StaffEntity staff = staffRepository.findById(request.staffId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró al barbero de id " + request.staffId()));

        // Validaciones
        validateTimeRange(request.startTime(), request.endTime());
        validateOverlap(request.staffId(), request.dayOfWeek(), request.startTime(), request.endTime());

        // Mapear y Guardar
        ScheduleEntity scheduleToSave = ScheduleMapper.toEntity(request, staff, branchEntity);
        ScheduleEntity savedSchedule = scheduleRepository.save(scheduleToSave);

        return ScheduleMapper.toDto(savedSchedule);
    }

    private void validateOverlap(UUID staffId, int dayOfWeek, LocalTime startTime, LocalTime endTime) {
        int count = scheduleRepository.countOverlappingSchedules(staffId, dayOfWeek, startTime, endTime);
        if (count > 0) {
            throw new BusinessRuleException("El horario elegido se solapa con otro turno existente del barbero.");
        }
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new BusinessRuleException("La hora de finalización debe ser posterior a la hora de inicio.");
        }
    }


    //---------------------Get schedules By Staff
    @Override
    public List<ScheduleDto> getSchedulesByStaff(UUID staffId) {

        StaffEntity staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró al barbero de id " + staffId));

        return scheduleRepository.findActiveSchedulesByStaff(staffId)
                .stream()
                .map(ScheduleMapper::toDto)
                .toList();
    }

    //---------------------Get availability
    @Override
    @Transactional(readOnly = true)
    public List<LocalTime> getAvailability(UUID staffId, UUID serviceId, LocalDate date) {

        long timeService = calculateServiceDuration(serviceId);

        DailyScheduleContext scheduleContext = determineDailySchedule(staffId, date);

        List<AppointmentEntity> appointments = fetchAppointmentsForDate(staffId, date);

        return generateAvailableSlots(scheduleContext, timeService, appointments, date);
    }

    // Obtener duración del servicio
    private long calculateServiceDuration(UUID serviceId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el servicio de id " + serviceId));
        return service.getDurationMinutes() + service.getBufferMinutes();
    }

    // Definir la jornada laboral (Reemplazos o Rutina normal)
    private DailyScheduleContext determineDailySchedule(UUID staffId, LocalDate date) {
        List<DateExceptionEntity> staffDateExceptions = dateExceptionRepository.findByStaffIdAndExceptionDate(staffId, date);

        List<DateExceptionEntity> reemplazos = staffDateExceptions.stream()
                .filter(e -> !e.isUnavailable())
                .toList();

        if (!reemplazos.isEmpty()) {
            List<TimeBlock> workingBlocks = reemplazos.stream()
                    .map(e -> new TimeBlock(e.getStartTime(), e.getEndTime()))
                    .toList();
            // Si hay reemplazo, no cruzamos contra parches, por eso mandamos una lista vacía
            return new DailyScheduleContext(workingBlocks, new ArrayList<>());
        }

        int dbDayOfWeek = date.getDayOfWeek() == DayOfWeek.SUNDAY ? 0 : date.getDayOfWeek().getValue();
        List<TimeBlock> workingBlocks = scheduleRepository.findSchedulesForAvailability(staffId, dbDayOfWeek).stream()
                .map(s -> new TimeBlock(s.getStartTime(), s.getEndTime()))
                .toList();

        List<DateExceptionEntity> parches = staffDateExceptions.stream()
                .filter(DateExceptionEntity::isUnavailable)
                .toList();

        return new DailyScheduleContext(workingBlocks, parches);
    }

    // Buscar turnos ya dados
    private List<AppointmentEntity> fetchAppointmentsForDate(UUID staffId, LocalDate date) {
        Instant startOfDay = date.atStartOfDay(DEFAULT_ZONE).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(DEFAULT_ZONE).toInstant();

        return appointmentRepository.findActiveAppointmentsByStaffAndDate(staffId, startOfDay, endOfDay);
    }

    // Calculamos los turnos disponibles
    private List<LocalTime> generateAvailableSlots(DailyScheduleContext context, long timeService,
                                                   List<AppointmentEntity> appointments, LocalDate date) {

        List<LocalTime> availableSlots = new ArrayList<>();
        LocalDate dateNow = LocalDate.now(DEFAULT_ZONE);
        LocalTime timeNow = LocalTime.now(DEFAULT_ZONE);
        boolean isToday = date.isEqual(dateNow);

        for (TimeBlock block : context.workingBlocks()) {
            LocalTime currentSlot = block.startTime();
            LocalTime currentSlotEnd = currentSlot.plusMinutes(timeService);

            // Condición 1: currentSlotEnd.isAfter(currentSlot) previene el bucle infinito si currentSlotEnd llega a las 00:00
            // Condición 2: No debe superar la hora de finalización del bloque
            while (currentSlotEnd.isAfter(currentSlot) && !currentSlotEnd.isAfter(block.endTime())) {

                if (isToday && currentSlot.isBefore(timeNow)) {
                    currentSlot = currentSlotEnd;
                    currentSlotEnd = currentSlot.plusMinutes(timeService);
                    continue;
                }

                boolean existsException = !context.parches().isEmpty() && verifyException(context.parches(), currentSlot, currentSlotEnd);
                boolean existsAppointment = verifyAppointment(appointments, currentSlot, currentSlotEnd);

                if (!existsException && !existsAppointment) {
                    availableSlots.add(currentSlot);
                }

                currentSlot = currentSlotEnd;
                currentSlotEnd = currentSlot.plusMinutes(timeService);
            }
        }
        return availableSlots;
    }

    private boolean verifyAppointment(List<AppointmentEntity> staffAppointments, LocalTime currentSlot, LocalTime currentSlotEnd) {
        return staffAppointments.stream().anyMatch(appo -> {
            // Convertimos el Instant (UTC) de la BD a LocalTime en la zona horaria correcta
            LocalTime appoStart = appo.getStartAt().atZone(DEFAULT_ZONE).toLocalTime();
            LocalTime appoEnd = appo.getEndAt().atZone(DEFAULT_ZONE).toLocalTime();

            return currentSlot.isBefore(appoEnd) && currentSlotEnd.isAfter(appoStart);
        });
    }

    private boolean verifyException(List<DateExceptionEntity> parches, LocalTime currentSlot, LocalTime currentSlotEnd) {
        return parches.stream().anyMatch(parche -> {
            // Si el parche no tiene horas, significa bloqueo de día completo
            if (parche.getStartTime() == null || parche.getEndTime() == null) return true;

            // Colisión: El turno inicia antes de que termine el parche, y termina después de que el parche inicia
            return currentSlot.isBefore(parche.getEndTime()) && currentSlotEnd.isAfter(parche.getStartTime());
        });
    }

    //-------------------------------Update
    @Override
    @Transactional
    public ScheduleDto update(UUID scheduleId, ScheduleUpdateDto request) {

        // Obtener la entidad original
        ScheduleEntity scheduleToUpdate = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la schedule de id " + scheduleId));

        // Aplicar los cambios de la request
        applyPartialUpdates(scheduleToUpdate, request);

        // Validar el estado resultante
        validateUpdatedSchedule(scheduleToUpdate);

        // Guardar y retornar
        ScheduleEntity updatedSchedule = scheduleRepository.save(scheduleToUpdate);
        return ScheduleMapper.toDto(updatedSchedule);
    }

    private void applyPartialUpdates(ScheduleEntity schedule, ScheduleUpdateDto request) {
        if (request.staffId() != null) {
            StaffEntity staff = staffRepository.findById(request.staffId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró al barbero de id " + request.staffId()));
            schedule.setStaff(staff);
        }

        if (request.branchId() != null) {
            BranchEntity branch = branchRepository.findById(request.branchId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró la sucursal de id " + request.branchId()));

//            if (!branch.isActive()) {
//                throw new BusinessRuleException("No se puede asignar un horario a una sucursal inactiva.");
//            }

            schedule.setBranch(branch);
        }

        if (request.dayOfWeek() != null) schedule.setDayOfWeek(request.dayOfWeek());
        if (request.startTime() != null) schedule.setStartTime(request.startTime());
        if (request.endTime() != null) schedule.setEndTime(request.endTime());
        if (request.isActive() != null) schedule.setActive(request.isActive());
    }

    private void validateUpdatedSchedule(ScheduleEntity schedule) {
        validateTimeRange(schedule.getStartTime(), schedule.getEndTime());

        if (schedule.isActive()) {
            validateOverlapForUpdate(
                    schedule.getId(),
                    schedule.getStaff().getId(),
                    schedule.getDayOfWeek(),
                    schedule.getStartTime(),
                    schedule.getEndTime()
            );
        }
    }

    private void validateOverlapForUpdate(UUID scheduleId, UUID staffId, int dayOfWeek, LocalTime startTime, LocalTime endTime) {
        int count = scheduleRepository.countOverlappingSchedulesForUpdate(scheduleId, staffId, dayOfWeek, startTime, endTime);
        if (count > 0) {
            throw new BusinessRuleException("El horario elegido se solapa con otro turno existente del barbero.");
        }
    }

    //---------------------------Delete
    @Override
    @Transactional
    public void delete(UUID scheduleId) {

        ScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró a la schedule de id " + scheduleId));

        schedule.setActive(false);
        scheduleRepository.save(schedule);
    }

}
