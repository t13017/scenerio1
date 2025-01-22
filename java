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



import axios from "axios";

const API_URL = "http://localhost:8080/api/cards";

export const getAllCards = () => axios.get(API_URL);
export const createCard = (card) => axios.post(API_URL, card);
export const updateCard = (id, card) => axios.put(`${API_URL}/${id}`, card);
export const deleteCard = (id) => axios.delete(`${API_URL}/${id}`);



import { useEffect, useState } from "react";
import { Container, Row, Col } from "react-bootstrap";
import GetCard from "./GetCard";
import { getAllCards, deleteCard } from "../api";

function HomePage() {
    const [cards, setCards] = useState([]);

    useEffect(() => {
        fetchCards();
    }, []);

    const fetchCards = async () => {
        const response = await getAllCards();
        setCards(response.data);
    };

    const handleDelete = async (id) => {
        if (window.confirm("Are you sure you want to delete this card?")) {
            await deleteCard(id);
            fetchCards();
        }
    };

    return (
        <Container>
            <h2 className="my-4 text-center">Card Collection</h2>
            <Row>
                {cards.map((card) => (
                    <Col key={card.id} md={4}>
                        <GetCard {...card} onDelete={handleDelete} />
                    </Col>
                ))}
            </Row>
        </Container>
    );
}

export default HomePage;




<dependencies>
    <!-- Spring Boot Starter Web (For REST API) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot Starter Data JPA (For Database Interaction) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- MySQL Driver -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Spring Boot Starter Validation (For Input Validation) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Spring Boot DevTools (For Live Reload during Development) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>

    <!-- Spring Boot Starter Test (For Testing) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>


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

    // ✅ Constructor
    public Card() {}

    public Card(String title, String description, String image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }

    // ✅ Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    // ✅ Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }
}


@PutMapping("/{id}")
public Card updateCard(@PathVariable Long id, @RequestBody Card card) {
    Card existingCard = cardService.getCardById(id);
    if (existingCard != null) {
        existingCard.setTitle(card.getTitle());
        existingCard.setDescription(card.getDescription());
        existingCard.setImage(card.getImage());
        return cardService.saveCard(existingCard);
    } else {
        throw new RuntimeException("Card not found with id: " + id);
    }
}



 import { Card, Button, Container, Row, Col } from "react-bootstrap";
import { Link } from "react-router-dom";

function GetCard({ cards, onDelete }) {
    return (
        <Container>
            <Row className="justify-content-center">
                {cards.map((card) => (
                    <Col key={card.id} md={4} className="mb-4">
                        <Card style={{ width: "100%" }}>
                            {card.image && <Card.Img variant="top" src={card.image} />}
                            <Card.Body>
                                <Card.Title>{card.title}</Card.Title>
                                <Card.Text>{card.description}</Card.Text>
                                <div className="d-flex justify-content-between">
                                    <Link to="/create" state={{ ...card }}>
                                        <Button variant="primary">Update</Button>
                                    </Link>
                                    <Button variant="danger" onClick={() => onDelete(card.id)}>Delete</Button>
                                </div>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>
        </Container>
    );
}

export default GetCard;




import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Container, Button, Form, Alert } from "react-bootstrap";
import { createCard, updateCard } from "../api";

function Create() {
    const location = useLocation();
    const navigate = useNavigate();

    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [image, setImage] = useState("");
    const [imagePreview, setImagePreview] = useState("");
    const [id, setId] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        if (location.state) {
            setId(location.state.id);
            setTitle(location.state.title);
            setDescription(location.state.description);
            setImage(location.state.image);
            setImagePreview(location.state.image); // Show preview if editing
        }
    }, [location.state]);

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setImage(reader.result);
                setImagePreview(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (!title || !description) {
                setError("Title and Description are required.");
                return;
            }
            if (id) {
                await updateCard(id, { title, description, image });
            } else {
                await createCard({ title, description, image });
            }
            navigate("/");
        } catch (err) {
            setError("An error occurred while saving the card.");
            console.error(err);
        }
    };

    return (
        <Container>
            <h2 className="my-4">{id ? "Edit Card" : "Create Card"}</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                    <Form.Label>Title</Form.Label>
                    <Form.Control
                        type="text"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Description</Form.Label>
                    <Form.Control
                        as="textarea"
                        rows={3}
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Image</Form.Label>
                    <Form.Control type="file" accept="image/*" onChange={handleImageChange} />
                    {imagePreview && (
                        <div className="mt-3">
                            <img src={imagePreview} alt="Preview" width="100%" style={{ maxWidth: "300px" }} />
                        </div>
                    )}
                </Form.Group>

                <Button variant="primary" type="submit">
                    {id ? "Update" : "Create"}
                </Button>
            </Form>
        </Container>
    );
}

export default Create;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {
    
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // Create a new card with an image
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Card> createCard(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            byte[] imageBytes = (imageFile != null) ? imageFile.getBytes() : null;
            Card card = new Card(null, title, description, imageBytes);
            return ResponseEntity.status(HttpStatus.CREATED).body(cardService.saveCard(card));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Retrieve all cards
    @GetMapping
    public ResponseEntity<List<Card>> getAllCards() {
        return ResponseEntity.ok(cardService.getAllCards());
    }

    // Update a card (title, description, image)
    @PutMapping("/{id}")
    public ResponseEntity<Card> updateCard(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            Card existingCard = cardService.getCardById(id);
            if (existingCard == null) {
                return ResponseEntity.notFound().build();
            }
            existingCard.setTitle(title);
            existingCard.setDescription(description);

            if (imageFile != null) {
                existingCard.setImage(imageFile.getBytes());
            }

            return ResponseEntity.ok(cardService.saveCard(existingCard));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
