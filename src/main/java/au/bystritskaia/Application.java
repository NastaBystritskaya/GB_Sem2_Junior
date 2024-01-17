package au.bystritskaia;

import au.bystritskaia.actors.Animal;
import au.bystritskaia.actors.Cat;
import au.bystritskaia.actors.Dog;
import au.bystritskaia.service.QueryBuilder;

import java.awt.*;
import java.util.UUID;

public class Application {
    public static void main(String[] args) {
        try {
            Animal first = Cat
                    .class
                    .getConstructor(String.class, int.class, int.class)
                    .newInstance("Васька", 2, 6);

            Animal second = Cat
                    .class
                    .getConstructor(String.class, int.class, int.class)
                    .newInstance("Мурка", 5, 4);

            Animal three = Dog
                    .class
                    .getConstructor(String.class, int.class, Color.class)
                    .newInstance("Шарик", 2, Color.ORANGE);

            Animal fourth = Dog
                    .class
                    .getConstructor(String.class, int.class, Color.class)
                    .newInstance("Трезор", 5, Color.GRAY);

            Animal[] animals = new Animal[] {first, second, three, fourth};

            System.out.println("*****************Объекты*****************");
            for (Animal animal: animals) {
                System.out.println(animal.toString());
            }
            System.out.println("**********************************");

            System.out.println("*****************Метод makeSound*****************");
            for (Animal animal: animals) {
                System.out.println(animal.makeSound());
            }
            System.out.println("**********************************");

            System.out.println("*****************Запросы*****************");
            QueryBuilder<Animal> query = new QueryBuilder<>();
            System.out.println(query.select(Cat.class, UUID.randomUUID().toString()));
            System.out.println(query.save(first));
            System.out.println(query.delete(first));
            System.out.println("**********************************");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Не удалось создать животное");
        }
    }
}
