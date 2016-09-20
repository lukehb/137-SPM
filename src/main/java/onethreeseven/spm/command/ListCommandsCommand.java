package onethreeseven.spm.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;
import java.util.Map;

/**
 * List all commands.
 * @author Luke Bermingham
 */
@Parameters(commandNames = {"listCommands", "lc"},
        commandDescription = "List all currently registered commands in the application.")
public class ListCommandsCommand extends AbstractCommand {

    private final JCommander jc;
    public ListCommandsCommand(JCommander jc){
        this.jc = jc;
    }

    @Override
    protected void resetCommandParameters() {
        //do nothing
    }

    @Override
    public void run() {

        //print each command
        for (Map.Entry<String, JCommander> entry : jc.getCommands().entrySet()) {

            for (Object commandObj : entry.getValue().getObjects()) {
                if(commandObj instanceof AbstractCommand){
                    AbstractCommand command = (AbstractCommand) commandObj;
                    Parameters p = command.getClass().getAnnotation(Parameters.class);
                    String commandName = p.commandNames()[0];
                    System.out.println(commandName);
                }
            }
        }

        System.out.println("For more info on a command type the command name followed by -h.");

    }
}
