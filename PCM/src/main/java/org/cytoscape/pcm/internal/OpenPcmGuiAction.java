package org.cytoscape.pcm.internal;


import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.pcm.internal.PcmGui.PanelState;

/**
 * // -------------------------------------------------------------------------
 * /** Menu option to open the PathLinker plugin
 *
 * @author Daniel Gil
 * @version Nov 4, 2015
 */
public class OpenPcmGuiAction
    extends AbstractCyAction
{
    private PcmGui _panel;


    /**
     * Constructor for the menu option
     *
     * @param panel
     *            the panel to be opened
     * @param applicationManager
     *            the application manager to add this option into the menu
     */
    public OpenPcmGuiAction(
        PcmGui panel,
        CyApplicationManager applicationManager)
    {
        super("Open", applicationManager, null, null);
        setPreferredMenu("Apps.PCM");

        _panel = panel;
    }


    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        // opens the panel
        _panel.setPanelState(PanelState.OPEN);
    }
}
