package com.example.contactlistapi;

import com.example.contactlistapi.entity.Contact;
import com.example.contactlistapi.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ContactRepositoryTest {

    @Autowired ContactRepository repo;

    @Test
    void save_find_update_delete() {
        Contact c = new Contact("Ana", "ana@mail.com", LocalDateTime.now());
        Contact saved = repo.save(c);
        assertThat(saved.getId()).isNotNull();

        var found = repo.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("ana@mail.com");

        found.get().setName("Ana María");
        repo.save(found.get());
        assertThat(repo.findById(saved.getId()).get().getName()).isEqualTo("Ana María");

        repo.deleteById(saved.getId());
        assertThat(repo.findById(saved.getId())).isEmpty();
    }
}
