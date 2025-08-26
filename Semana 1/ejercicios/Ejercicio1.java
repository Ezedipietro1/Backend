import java.util.Scanner;
import java.io.*;

public class Ejercicio1 {
    public static void main(String[] args) {
        String character = "";
        System.out.println("Figura 1");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                character += "* ";
            }
            System.out.println(character);
            character = "";
        }

        System.out.println("Figura 2");
        character = "";
        int cant = 6;
        for (int i = 0; i < 4; i++) {
            if (i % 2 == 0) {
                character += "* ";
            }
            else {
                character += " *";
            }
            System.out.println(character.repeat(cant));
            character = "";
        }

        System.out.println("Figura 3");
        character = "";
        for (int i = 0; i < 5; i++) {
            character += "* ";
            System.out.println(character);
        }

        System.out.println("Figura 4");
        character = ""; 
        for (int i = 0; i < 5; i++) {
            character += "* ";
            System.out.println(character);
        }
        cant = 4;
        for (int i = 0; i < 4; i++) {
            character = "* ";
            System.out.println(character.repeat(cant));
            cant--;
        } 
    }
}
