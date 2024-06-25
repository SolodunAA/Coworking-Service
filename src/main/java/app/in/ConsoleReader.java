package app.in;

import java.util.Scanner;

public class ConsoleReader implements Reader{
    @Override
    public String read() {
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }
}