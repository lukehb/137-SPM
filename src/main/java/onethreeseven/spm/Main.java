package onethreeseven.spm;

import javafx.stage.Stage;
import onethreeseven.trajsuitePlugin.model.BaseTrajSuiteProgram;
import onethreeseven.trajsuitePlugin.view.BasicFxApplication;

/**
 * Entry point for this sequential pattern mining module.
 * @author Luke Bermingham
 */
public class Main extends BasicFxApplication {

    @Override
    protected BaseTrajSuiteProgram preStart(Stage stage) {
        return BaseTrajSuiteProgram.getInstance();
    }

    @Override
    public String getTitle() {
        return "SPM module";
    }

    @Override
    public int getStartWidth() {
        return 640;
    }

    @Override
    public int getStartHeight() {
        return 480;
    }

    @Override
    protected void afterStart(Stage stage) {
        BaseTrajSuiteProgram.getInstance().getCLI().startListeningForInput();
        System.out.println("Listening for input, type lc to list commands.");
    }
}
