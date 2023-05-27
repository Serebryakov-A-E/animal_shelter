package me.serebryakov.animal_shelter.exception;

public class OwnerNotExistException extends RuntimeException {
    public OwnerNotExistException() {
        super("Owner not exist!");
    }
}
