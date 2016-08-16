package org.cytoscape.pcm.internal;


import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.pcm.internal.PcmGui.PanelState;

/**
 * // -------------------------------------------------------------------------
 * /** Menu option to close the PathLinker plugin
 *
 * @author Daniel Gil
 * @version Nov 4, 2015
 */
public class ClosePcmGuiAction
    extends AbstractCyAction
{
    private static PcmGui _panel;


    /**
     * Constructor for the menu option
     *
     * @param panel
     *            the panel to close
     * @param applicationManager
     *            the application manager to add this option into the menu
     */
    public ClosePcmGuiAction(
        PcmGui panel,
        CyApplicationManager applicationManager)
    {
        super("Close", applicationManager, null, null);
        setPreferredMenu("Apps.PCM");

        _panel = panel;
    }


    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        // closes the panel
        _panel.setPanelState(PanelState.CLOSED);
        // Deactivate methods here
    }
}
