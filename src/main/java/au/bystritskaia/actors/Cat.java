package au.bystritskaia.actors;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cat extends Animal {

    @NonNull
    int mustacheLength;

    public Cat(@NonNull String name, @NonNull int age, @NonNull int mustache) {
        super(UUID.randomUUID().toString(), name, age);
        this.mustacheLength = mustache;
    }

    @Override
    public String makeSound() {
        return "Мяу";
    }

    @Override
    public String toString() {
        return "Кошка с усами длииной " + this.mustacheLength;
    }
}
