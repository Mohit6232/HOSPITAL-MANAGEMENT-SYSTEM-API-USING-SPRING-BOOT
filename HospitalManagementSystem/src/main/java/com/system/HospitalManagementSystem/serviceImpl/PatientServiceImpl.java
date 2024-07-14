package com.system.HospitalManagementSystem.serviceImpl;

import com.system.HospitalManagementSystem.exception.InvalidResourceException;
import com.system.HospitalManagementSystem.exception.ResourceNotFoundException;
import com.system.HospitalManagementSystem.model.Patient;
import com.system.HospitalManagementSystem.repository.PatientRepository;
import com.system.HospitalManagementSystem.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    @Override
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public void deletePatient(Long id) throws ResourceNotFoundException {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }
    }

    @Override
    public Patient updatePatient(Long id, Patient updatedPatient) throws InvalidResourceException {
        return patientRepository.findById(id)
                .map(existingPatient -> {
                    existingPatient.setName(updatedPatient.getName());
                    existingPatient.setGender(updatedPatient.getGender());
                    existingPatient.setAge(updatedPatient.getAge());
                    existingPatient.setContactInfo(updatedPatient.getContactInfo());
                    return patientRepository.save(existingPatient);
                })
                .orElseThrow(() ->
                        new InvalidResourceException("Patient with id: " + id + " could not be updated.")
                );
    }
}