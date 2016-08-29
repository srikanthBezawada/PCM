package org.cytoscape.pcm.internal;


import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.pcm.internal.PcmGui.PanelState;


public class ClosePcmGuiAction
    extends AbstractCyAction
{
    private static PcmGui _panel;

    public ClosePcmGuiAction(
        PcmGui panel,
        CyApplicationManager applicationManager)
    {
        super("Close PCM", applicationManager, null, null);
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
