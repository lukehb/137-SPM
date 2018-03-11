package onethreeseven.spm.command;

import com.beust.jcommander.JCommander;
import onethreeseven.jclimod.AbstractCommandsListing;
import onethreeseven.jclimod.CLICommand;

/**
 * Commands exports by this module
 * @author Luke Bermingham
 */
public class SPMCommandsListing extends AbstractCommandsListing {
    @Override
    protected CLICommand[] createCommands(JCommander jc, Object... args) {
        return new CLICommand[]{
                new MineSequentialPatternsCommand(),
                new CalculateSPMFStatsCommand()
        };
    }
}
