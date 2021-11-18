package com.github.lfbr0.ssh_bruteforce;

import com.github.lfbr0.ssh_bruteforce.exceptions.ArgumentsException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Class responsible for parsing arguments passed when executing application
 */
public class ArgumentsParser {

    private final CommandLine commandLine;
    final Options options;

    /**
     * valid args:
     * --ip $IP_ADDR
     * --port $SSH_PORT
     * --user $USER_NAME_TO_TRY
     * --dict $FILE_PATH_OF_PASSWORDS_DICTIONARY
     * -v [verbose option to print passwords being tested, no arg]
     * @param args -> args passed by array of command line env
     * @throws ParseException -> if can't parse one arg
     */
    public ArgumentsParser(String[] args) throws ParseException {

        options = new Options();
        options.addOption("ip", true, "IP address to try");
        options.addOption("port", true, "Port for SSH attempt");
        options.addOption("dict", true, "Dictionary file");
        options.addOption("user", true, "User to try and use");
        options.addOption("v", false, "Verbose option to print passwords being tested, no arg");

        DefaultParser parser = new DefaultParser();
        this.commandLine = parser.parse(options, args);

    }

    /**
     * Returns an optional wrapper of the target IP address
     * @return optional wrapper of the target IP address
     */
    public Optional<String> getIPAddress() {
        return Optional.ofNullable( commandLine.getOptionValue("ip") );
    }

    /**
     * Returns an optional wrapper of the target port
     * @return optional wrapper of the target port
     */
    public Optional<String> getPort() { return Optional.ofNullable( commandLine.getOptionValue("port") ); }

    /**
     * Returns an optional wrapper of the dictionary path
     * @return optional wrapper of the dictionary path
     */
    public Optional<String> getDictionaryFilePath() {
        return Optional.ofNullable( commandLine.getOptionValue("dict") );
    }

    /**
     * Returns an optional wrapper of the target username
     * @return optional wrapper of the target username
     */
    public Optional<String> getUsername() {
        return Optional.ofNullable( commandLine.getOptionValue("user") );
    }

    /**
     * Process dictionary file line by line in a lazy fashion, no buffering since we can end prematurely
     * @return a stream of passwords (if they're seperated by \n)
     * @throws IOException if file can't be opened
     * @throws ArgumentsException if arguments are invalid
     */ //TODO -> fix encoding doom error here
    public Stream<String> getDictionaryStream() throws IOException, ArgumentsException {
        return Files.lines(
                Path.of(
                        getDictionaryFilePath().orElseThrow(
                                () -> new ArgumentsException("NO DICTIONARY FILE PASSED!")
                        )
                )
        );
    }

    /**
     * Checks if user has activated verbose mode
     * @return true if verbose flag on
     */
    public boolean verboseMode() {
        return commandLine.hasOption("v");
    }

}
