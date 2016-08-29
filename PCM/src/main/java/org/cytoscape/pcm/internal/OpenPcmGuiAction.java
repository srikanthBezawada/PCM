package org.cytoscape.pcm.internal;


import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.pcm.internal.PcmGui.PanelState;


public class OpenPcmGuiAction
    extends AbstractCyAction
{
    private PcmGui _panel;

    public OpenPcmGuiAction(
        PcmGui panel,
        CyApplicationManager applicationManager)
    {
        super("Open PCM", applicationManager, null, null);
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
