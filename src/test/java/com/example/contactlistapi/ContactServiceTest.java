package com.example.contactlistapi;

import com.example.contactlistapi.entity.Contact;
import com.example.contactlistapi.repository.ContactRepository;
import com.example.contactlistapi.service.ContactService;
import dto.ContactDTO;
import exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock ContactRepository repo;
    @Mock ModelMapper mapper;
    @InjectMocks ContactService service;

    @Test
    void create_maps_setsCreatedAt_and_saves() {
        ContactDTO dto = new ContactDTO();
        dto.setName("Ana");
        dto.setEmail("ana@mail.com");

        // Devolvemos una entidad válida: usar ctor (name, email, createdAt) o no-args + setters
        Contact mapped = new Contact("Ana", "ana@mail.com", LocalDateTime.now()); // el service igual pisará createdAt
        when(mapper.map(eq(dto), eq(Contact.class))).thenReturn(mapped);

        when(repo.save(any(Contact.class))).thenAnswer(inv -> {
            Contact c = inv.getArgument(0, Contact.class);
            c.setId(1); // simula que la BD asigna el id
            return c;
        });

        Contact saved = service.create(dto);

        assertNotNull(saved.getId());
        assertEquals("Ana", saved.getName());
        assertEquals("ana@mail.com", saved.getEmail());
        assertNotNull(saved.getCreatedAt());
        verify(repo).save(any(Contact.class));
    }

    @Test
    void findById_returns_entity_or_throws() {
        Contact found = new Contact("Ana", "ana@mail.com", LocalDateTime.now());
        found.setId(1);
        when(repo.findById(1)).thenReturn(Optional.of(found));

        assertEquals("Ana", service.findById(1).getName());

        when(repo.findById(999)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(999));
    }

    @Test
    void update_maps_onto_existing_and_saves() {
        Contact existing = new Contact("Old", "old@mail.com", LocalDateTime.now());
        existing.setId(5);
        when(repo.findById(5)).thenReturn(Optional.of(existing));

        ContactDTO dto = new ContactDTO();
        dto.setName("New");
        dto.setEmail("new@mail.com");

        // mapear sobre la misma instancia existente (simula ModelMapper)
        doAnswer(inv -> {
            ContactDTO src = inv.getArgument(0);
            Contact target = inv.getArgument(1);
            target.setName(src.getName());
            target.setEmail(src.getEmail());
            return null;
        }).when(mapper).map(eq(dto), eq(existing));

        when(repo.save(existing)).thenReturn(existing);

        Contact updated = service.update(5, dto);

        assertEquals(5, updated.getId());
        assertEquals("New", updated.getName());
        assertEquals("new@mail.com", updated.getEmail());
        assertNotNull(updated.getCreatedAt());
        verify(repo).save(existing);
    }

    @Test
    void delete_finds_and_deletes() {
        Contact existing = new Contact("Del", "d@mail.com", LocalDateTime.now());
        existing.setId(7);
        when(repo.findById(7)).thenReturn(Optional.of(existing));

        service.delete(7);

        verify(repo).delete(existing);
    }

    @Test
    void update_throws_when_id_not_found() {
        // Arrange
        ContactDTO dto = new ContactDTO();
        dto.setName("New");
        dto.setEmail("new@mail.com");

        when(repo.findById(999)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.update(999, dto));

        // No debe intentar guardar si no existe
        verify(repo, never()).save(any());
    }
    @Test
    void delete_throws_when_id_not_found() {
        // Arrange
        when(repo.findById(999)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.delete(999));

        // No debe intentar borrar si no existe
        verify(repo, never()).delete(any());
        verify(repo, never()).deleteById(any());
    }

}
