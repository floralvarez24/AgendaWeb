package com.example.contactlistapi.service;

import com.example.contactlistapi.entity.Contact;
import com.example.contactlistapi.repository.ContactRepository;
import dto.ContactDTO;
import exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
@AllArgsConstructor
@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final ModelMapper mapper;

    public Iterable<Contact> findAll() {
        return contactRepository.findAll();
    }

    public Contact findById(Integer id) {
        return contactRepository
                .findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public Contact create(ContactDTO contactDTO) {
        Contact contact = mapper.map(contactDTO, Contact.class);
        contact.setCreatedAt(LocalDateTime.now());
        return contactRepository.save(contact);
    }

    public Contact update(Integer id,
                         ContactDTO contactDTO) {
        Contact contactFromDb = findById(id);
        mapper.map(contactDTO, contactFromDb);
        return contactRepository.save(contactFromDb);
    }
    public void delete(Integer id) {
        Contact contactFromDb = findById(id);
        contactRepository.delete(contactFromDb);
    }
}
