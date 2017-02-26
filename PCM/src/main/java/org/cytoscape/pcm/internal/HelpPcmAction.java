package org.cytoscape.pcm.internal;


import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.util.swing.OpenBrowser;

public class HelpPcmAction
    extends AbstractCyAction
{
    private OpenBrowser openBrowser;
    /**
     * Constructor for the menu option
     * @param applicationManager
     *            the application manager to add this option into the menu
     */
    public HelpPcmAction(
        CyApplicationManager applicationManager, OpenBrowser openBrowser)
    {
        super("Help", applicationManager, null, null);
        this.openBrowser = openBrowser;
        setPreferredMenu("Apps.PCM");
    }


    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        
        openBrowser.openURL("apps.cytoscape.org/pcm");
       
    }
}
