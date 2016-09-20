package onethreeseven.spm;

import onethreeseven.spm.command.SPMCLI;

/**
 * Entry point for this sequential pattern mining module.
 * @author Luke Bermingham
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("SPM module - Type lc to list all the commands.");
        SPMCLI cli = new SPMCLI();
        //do any command if it was starting with args
        cli.doCommand(args);
        //otherwise, listen for args
        cli.listenForInput();
    }

}
