package me.serebryakov.animal_shelter.exception;

public class AnimalNotExistException extends RuntimeException {
    public AnimalNotExistException() {
        super("Animal not exist!");
    }
}
