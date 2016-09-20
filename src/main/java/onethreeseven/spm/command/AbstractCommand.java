package onethreeseven.spm.command;

import com.beust.jcommander.Parameter;

/**
 * Todo: write documentation
 *
 * @author Luke Bermingham
 */
public abstract class AbstractCommand {

    @Parameter(names = {"-h", "--help"}, description = "Get usage of this command", help = true)
    private boolean doHelp;

    public boolean askedForHelp() {
        return doHelp;
    }

    public void resetAll(){
        this.doHelp = false;
        resetCommandParameters();
    }

    protected abstract void resetCommandParameters();

    public abstract void run();

}
