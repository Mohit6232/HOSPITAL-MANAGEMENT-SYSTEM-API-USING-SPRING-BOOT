package com.system.HospitalManagementSystem.serviceImpl;

import com.system.HospitalManagementSystem.exception.InvalidResourceException;
import com.system.HospitalManagementSystem.exception.ResourceNotFoundException;
import com.system.HospitalManagementSystem.model.Appointment;
import com.system.HospitalManagementSystem.model.Doctor;
import com.system.HospitalManagementSystem.model.Patient;
import com.system.HospitalManagementSystem.repository.AppointmentRepository;
import com.system.HospitalManagementSystem.service.AppointmentService;
import com.system.HospitalManagementSystem.service.DoctorService;
import com.system.HospitalManagementSystem.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Override
    public Appointment saveAppointment(Appointment appointment) throws InvalidResourceException {
        try {
            Optional<Doctor> doctor = doctorService.getDoctorById(appointment.getDoctor().getDoctorId());
            Optional<Patient> patient = patientService.getPatientById(appointment.getPatient().getPatientId());

            if (doctor.isPresent() && patient.isPresent()) {
                appointment.setDoctor(doctor.get());
                appointment.setPatient(patient.get());
            }

            return appointmentRepository.save(appointment);
        } catch (Exception e) {
            throw new InvalidResourceException("Invalid Appointment.");
        }
    }

    @Override
    public void deleteAppointment(Long id) throws ResourceNotFoundException {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Appointment with id : " + id + " not found.");
        }
    }

    @Override
    public Appointment updateAppointment(Long id, Appointment updatedAppointment) throws InvalidResourceException {
        return appointmentRepository.findById(id)
                .map(existingAppointment -> {
                    existingAppointment.setDate(updatedAppointment.getDate());
                    Optional<Doctor> updatedDoctor = doctorService.getDoctorById(
                            updatedAppointment.getDoctor().getDoctorId());
                    Optional<Patient> updatedPatient = patientService.getPatientById(
                            updatedAppointment.getPatient().getPatientId());

                    if (updatedDoctor.isPresent() && updatedPatient.isPresent()) {
                        existingAppointment.setDoctor(updatedDoctor.get());
                        existingAppointment.setPatient(updatedPatient.get());
                    }
                    return appointmentRepository.save(existingAppointment);
                })
                .orElseThrow(() ->
                        new InvalidResourceException("Appointment with id: " + id + " could not be updated.")
                );
    }
}