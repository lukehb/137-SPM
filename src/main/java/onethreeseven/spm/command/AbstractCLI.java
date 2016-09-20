package onethreeseven.spm.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.Parameters;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract CLI, any modules wanting a CLI extend this.
 * @author Luke Bermingham
 */
public abstract class AbstractCLI {

    protected final JCommander jc;

    public AbstractCLI(){
        this.jc = new JCommander();
        registerCommands(getCommands());
    }

    private void registerCommands(Collection<AbstractCommand> commands){
        for (AbstractCommand command : commands){
            Parameters p = command.getClass().getAnnotation(Parameters.class);
            String commandName = p.commandNames()[0];
            //use all command names after the first one as aliases
            if(p.commandNames().length > 1){
                String[] aliases = new String[p.commandNames().length-1];
                System.arraycopy(p.commandNames(), 1, aliases, 0, p.commandNames().length-1);
                jc.addCommand(commandName, command, aliases);
                jc.getCommands().get(commandName).setAllowParameterOverwriting(true);
            }
            else{
                jc.addCommand(commandName, command);
                jc.getCommands().get(commandName).setAllowParameterOverwriting(true);
            }
        }
    }

    public void listenForInput(){
        ExecutorService exec = Executors.newSingleThreadExecutor();
        final AtomicBoolean keepReadingInput = new AtomicBoolean(true);
        final Scanner scanner = new Scanner(System.in);

        exec.execute(()->{
            while(keepReadingInput.get()){
                while(scanner.hasNextLine()){
                    String[] args = scanner.nextLine().split(" ");
                    try{
                        this.doCommand(args);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            scanner.close();
        });
    }

    public void doCommand(String... args){
        if(args == null || args.length == 0){
            return;
        }

        try{
            jc.parse(args);
        }catch (MissingCommandException e){
            System.out.println("Could not find any command in " + Arrays.toString(args));
            return;
        }

        String commandName = jc.getParsedCommand();
        if(commandName == null){
            return;
        }

        if(jc.getCommands().containsKey(commandName)){
            List<Object> commandObjs = jc.getCommands().get(commandName).getObjects();
            for (Object commandObj : commandObjs) {
                if(commandObj instanceof AbstractCommand){
                    AbstractCommand cmd = (AbstractCommand) commandObj;
                    if(cmd.askedForHelp()){
                        StringBuilder sb = new StringBuilder();
                        jc.usage(commandName, sb);
                        System.out.println(sb.toString());
                    }
                    else{
                        cmd.run();
                    }
                    cmd.resetAll();
                }
            }
        }
        else{
            System.err.println("Could not find a command called: " + commandName);
        }
    }

    public abstract Collection<AbstractCommand> getCommands();

}
