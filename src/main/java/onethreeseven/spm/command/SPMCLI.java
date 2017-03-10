package onethreeseven.spm.command;

import java.util.Arrays;
import java.util.Collection;

/**
 * CLI for this module.
 * @author Luke Bermingham
 */
public class SPMCLI extends AbstractCLI {

    @Override
    public Collection<AbstractCommand> getCommands() {
        return Arrays.asList(
                new ListCommandsCommand(jc),
                new GraspMinerCommand(),
                new CCSpanCommand(),
                new CalculateSPMFStatsCommand()
        );
    }
}
