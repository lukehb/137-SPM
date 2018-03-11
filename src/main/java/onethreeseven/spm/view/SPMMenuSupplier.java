package onethreeseven.spm.view;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import onethreeseven.common.util.FileUtil;
import onethreeseven.jclimod.CLIProgram;
import onethreeseven.spm.command.LoadSequences;
import onethreeseven.trajsuitePlugin.model.BaseTrajSuiteProgram;
import onethreeseven.trajsuitePlugin.view.AbstractMenuBarPopulator;
import onethreeseven.trajsuitePlugin.view.MenuSupplier;
import onethreeseven.trajsuitePlugin.view.TrajSuiteMenu;
import onethreeseven.trajsuitePlugin.view.TrajSuiteMenuItem;

import java.io.File;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

/**
 * Menus supplied from this module
 * @author Luke Bermingham
 */
public class SPMMenuSupplier implements MenuSupplier {
    @Override
    public void supplyMenus(AbstractMenuBarPopulator populator, BaseTrajSuiteProgram program, Stage primaryStage) {

        TrajSuiteMenu menu = new TrajSuiteMenu("File", -99);
        menu.addChild(makeLoadSequencesItem(primaryStage));
        populator.addMenu(menu);

    }

    private static final String initSeqsDir = "initSeqsDir";
    private static final File defaultDir = new File(System.getProperty("user.home"));

    private TrajSuiteMenuItem makeLoadSequencesItem(Stage primaryStage){

        return new TrajSuiteMenuItem("Load Sequences", 2, ()->{

            Preferences prefs = Preferences.userNodeForPackage(this.getClass());
            String folderPath = prefs.get(initSeqsDir, defaultDir.getAbsolutePath());

            File initDir = new File(folderPath);
            if(!initDir.exists() || !initDir.isDirectory()){
                initDir = defaultDir;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(initDir);
            fileChooser.setTitle("Select sequences file");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if(FileUtil.fileOkayToRead(selectedFile)){
                //store new init dir for opening sequences
                String newInitDir = Paths.get(selectedFile.toURI()).getParent().toAbsolutePath().toString();
                prefs.put(initSeqsDir, newInitDir);
                //do the command
                CLIProgram program = new CLIProgram();
                program.addCommand(new LoadSequences());
                program.doCommand(new String[]{"loadSequences", "-i", selectedFile.getAbsolutePath()});
            }

        });
    }

}
