import onethreeseven.jclimod.AbstractCommandsListing;
import onethreeseven.spm.command.SPMCommandsListing;
import onethreeseven.trajsuitePlugin.model.EntitySupplier;
import onethreeseven.trajsuitePlugin.model.TransactionProcessor;

module onethreeseven.spm{
    requires spmf;
    requires onethreeseven.jclimod;
    requires jcommander;
    requires onethreeseven.collections;
    requires onethreeseven.common;
    requires onethreeseven.trajsuitePlugin;

    exports onethreeseven.spm;
    exports onethreeseven.spm.model;
    exports onethreeseven.spm.command;
    exports onethreeseven.spm.algorithm;
    exports onethreeseven.spm.data;

    //expose commands
    provides AbstractCommandsListing with SPMCommandsListing;

    uses TransactionProcessor;
    uses EntitySupplier;

}