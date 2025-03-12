package com.example.contactlistapi.repository;

import com.example.contactlistapi.entity.Contact;
import org.springframework.data.repository.CrudRepository;

public interface ContactRepository extends CrudRepository <Contact, Integer> {
}
