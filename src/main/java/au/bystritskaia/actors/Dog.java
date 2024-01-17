package au.bystritskaia.actors;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.awt.*;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dog extends Animal {

    @NonNull
    Color hairColor;

    public Dog(@NonNull String name, @NonNull int age, @NonNull Color hairColor) {
        super(UUID.randomUUID().toString(), name, age);
        this.hairColor = hairColor;
    }

    @Override
    public String makeSound() {
        return "Гав";
    }

    @Override
    public String toString() {
        return "Собака с шерьстью цветом " + this.hairColor;
    }
}
