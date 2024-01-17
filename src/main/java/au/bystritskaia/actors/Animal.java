package au.bystritskaia.actors;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Animal {

    @Id
    final String ID;

    @NonNull
    String name;

    @NonNull
    int age;

    public abstract String makeSound();
}
