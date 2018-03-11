package onethreeseven.spm;

import onethreeseven.jclimod.CLIProgram;

/**
 * Entry point for this sequential pattern mining module.
 * @author Luke Bermingham
 */
public class Main {

    public static void main(String[] args) {
        CLIProgram program = new CLIProgram();
        System.out.println("SPM module - Type lc to list all the commands.");
        program.startListeningForInput();
    }

}
