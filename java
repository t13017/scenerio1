package com.example.cardapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Column(columnDefinition = "TEXT")
    private String image;

    // Getters and Setters
}


package com.example.cardapi.repository;

import com.example.cardapi.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> { }


package com.example.cardapi.service;

import com.example.cardapi.model.Card;
import com.example.cardapi.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CardService {
    @Autowired
    private CardRepository cardRepository;

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    public Card getCardById(Long id) {
        return cardRepository.findById(id).orElse(null);
    }

    public Card saveCard(Card card) {
        return cardRepository.save(card);
    }

    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }
}


package com.example.cardapi.controller;

import com.example.cardapi.model.Card;
import com.example.cardapi.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "http://localhost:3000")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    public List<Card> getAllCards() {
        return cardService.getAllCards();
    }

    @PostMapping
    public Card createCard(@RequestBody Card card) {
        return cardService.saveCard(card);
    }

    @PutMapping("/{id}")
    public Card updateCard(@PathVariable Long id, @RequestBody Card card) {
        Card existingCard = cardService.getCardById(id);
        existingCard.setTitle(card.getTitle());
        existingCard.setDescription(card.getDescription());
        existingCard.setImage(card.getImage());
        return cardService.saveCard(existingCard);
    }

    @DeleteMapping("/{id}")
    public String deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return "Card deleted successfully";
    }
}



spring.datasource.url=jdbc:mysql://localhost:3306/cardDB
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect




fe

import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Container, Button, Form } from "react-bootstrap";
import { createCard, updateCard } from "../api";

function Create() {
    const location = useLocation();
    const navigate = useNavigate();

    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [image, setImage] = useState("");
    const [id, setId] = useState(null);

    useEffect(() => {
        if (location.state) {
            setId(location.state.id);
            setTitle(location.state.title);
            setDescription(location.state.description);
            setImage(location.state.image);
        }
    }, [location.state]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (id) await updateCard(id, { title, description, image });
        else await createCard({ title, description, image });
        navigate("/");
    };

    return (
        <Container>
            <h2>{id ? "Edit Card" : "Create Card"}</h2>
            <Form onSubmit={handleSubmit}>
                <Form.Group>
                    <Form.Label>Title</Form.Label>
                    <Form.Control value={title} onChange={(e) => setTitle(e.target.value)} required />
                </Form.Group>
                <Form.Group>
                    <Form.Label>Description</Form.Label>
                    <Form.Control value={description} onChange={(e) => setDescription(e.target.value)} required />
                </Form.Group>
                <Button type="submit">{id ? "Update" : "Create"}</Button>
            </Form>
        </Container>
    );
}

export default Create;
