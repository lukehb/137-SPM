import onethreeseven.jclimod.AbstractCommandsListing;
import onethreeseven.spm.command.SPMCommandsListing;
import onethreeseven.spm.view.SPMMenuSupplier;
import onethreeseven.trajsuitePlugin.model.EntitySupplier;
import onethreeseven.trajsuitePlugin.model.TransactionProcessor;
import onethreeseven.trajsuitePlugin.view.MenuSupplier;

module onethreeseven.spm{
    //requires spmf;
    requires onethreeseven.jclimod;
    requires jcommander;
    requires onethreeseven.collections;
    requires onethreeseven.common;
    requires onethreeseven.trajsuitePlugin;
    requires java.prefs;

    exports onethreeseven.spm;
    exports onethreeseven.spm.model;
    exports onethreeseven.spm.command;
    exports onethreeseven.spm.algorithm;
    exports onethreeseven.spm.data;

    //for commands to work
    opens onethreeseven.spm.command to jcommander, onethreeseven.jclimod;

    //expose commands
    provides AbstractCommandsListing with SPMCommandsListing;
    provides MenuSupplier with SPMMenuSupplier;

    uses TransactionProcessor;
    uses EntitySupplier;

}