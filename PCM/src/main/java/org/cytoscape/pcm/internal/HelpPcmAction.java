package org.cytoscape.pcm.internal;


import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;

public class HelpPcmAction
    extends AbstractCyAction
{
    /**
     * Constructor for the menu option
     * @param applicationManager
     *            the application manager to add this option into the menu
     */
    public HelpPcmAction(
        CyApplicationManager applicationManager)
    {
        super("Help", applicationManager, null, null);
        setPreferredMenu("Apps.PCM");
    }


    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        // Open help tab
       
    }
}
