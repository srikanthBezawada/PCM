package org.cytoscape.pcm.internal;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.events.NetworkDestroyedEvent;
import org.cytoscape.model.events.NetworkDestroyedListener;
import org.cytoscape.pcm.internal.logic.Complex;
import org.cytoscape.pcm.internal.logic.PcmLogic;
import org.cytoscape.pcm.internal.results.CytoscapeResultViewerPanel;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;

/**
 * @author SrikanthB
 * GUI of the app, Control goes to logic from here
 */
public class PcmGui extends javax.swing.JPanel implements CytoPanelComponent, NetworkAddedListener, NetworkDestroyedListener {
    
    /** State of the panel. Initially null b/c it isn't open or closed yet */
    private PanelState _state = null;
    
    /** Parent container of the panel to re add to when we call open */
    private Container _parent;
    private CyApplicationManager _applicationManager;
    private CyNetworkManager _networkManager;
    private CyNetworkViewFactory _networkViewFactory;
    private CyNetworkViewManager _networkViewManager;
    private CyAppAdapter _adapter;
    private CySwingApplication _cySwingApp;
    private CytoscapeAppActivator _cyactivator;
    
    private PcmLogic logicThread; 

    
            
    /** The state of the panel */
    public enum PanelState
    {
        /** The panel is hidden */
        CLOSED,
        /** The panel is visible */
        OPEN
    };


    /**
     * Sets the state of the panel (open or closed).
     *
     * @param newState
     *            the new state
     */
    public void setPanelState(PanelState newState)
    {
        if (newState == _state)
            return;

        if (newState == PanelState.CLOSED)
        {
            _state = PanelState.CLOSED;
            _parent.remove(this);
            deactivate();
        }
        else if (newState == PanelState.OPEN)
        {
            _state = PanelState.OPEN;
            ((JTabbedPane)_parent).addTab(this.getTitle(), this);
            activate();
        }

        this.revalidate();
        this.repaint();
    }
    
    public PcmGui() {
        initComponents();
    }
    
    
    public void initialize(
        CytoscapeAppActivator cyactivator,    
        CySwingApplication cySwingApp,    
        CyApplicationManager applicationManager,
        CyNetworkManager networkManager,
        CyNetworkViewFactory networkViewFactory,
        CyNetworkViewManager networkViewManager,
        CyAppAdapter adapter)
    {
        _cyactivator = cyactivator;
        _cySwingApp = cySwingApp;
        _applicationManager = applicationManager;
        _networkManager = networkManager;
        _networkViewFactory = networkViewFactory;
        _networkViewManager = networkViewManager;
        _adapter = adapter;
        _parent = this.getParent();
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jScrollPane1 = new javax.swing.JScrollPane();
        mainPanel = new javax.swing.JPanel();
        headingLabel = new javax.swing.JLabel();
        mainUIPanel = new javax.swing.JPanel();
        networkLabel = new javax.swing.JLabel();
        networkComboBox = new javax.swing.JComboBox();
        pewccLabel = new javax.swing.JLabel();
        startB = new javax.swing.JButton();
        statusPanel = new javax.swing.JPanel();
        statusBar = new javax.swing.JProgressBar();
        statusLabel = new javax.swing.JLabel();
        stopButton = new javax.swing.JButton();
        cmcLabel = new javax.swing.JLabel();
        pewcc = new javax.swing.JCheckBox();
        cmc = new javax.swing.JCheckBox();
        mcodeLabel = new javax.swing.JLabel();
        mcode = new javax.swing.JCheckBox();
        mclLabel = new javax.swing.JLabel();
        mcl = new javax.swing.JCheckBox();
        cOneLabel = new javax.swing.JLabel();
        cOne = new javax.swing.JCheckBox();
        thresholdLabel = new javax.swing.JLabel();
        overlapThresholdTf = new javax.swing.JTextField();
        clustersThresholdLabel = new javax.swing.JLabel();
        clusterThresholdTf = new javax.swing.JTextField();
        similarityLabel = new javax.swing.JLabel();
        similarityComboBox = new javax.swing.JComboBox();

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        mainPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        headingLabel.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        headingLabel.setForeground(new java.awt.Color(255, 0, 51));
        headingLabel.setText("Protein Complexes Merger  (PCM)");

        mainUIPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select the network"));

        networkLabel.setText("Network");

        networkComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                networkComboBoxActionPerformed(evt);
            }
        });
        //networkComboBox.setEditable(false);

        pewccLabel.setText("PEWCC");

        startB.setText("Run PCM on Selected Network");
        startB.setToolTipText("");
        startB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        startB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBActionPerformed(evt);
            }
        });

        statusPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Status bar"));

        statusLabel.setText("status");

        stopButton.setBackground(new java.awt.Color(255, 102, 102));
        stopButton.setText("Stop");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                        .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stopButton)))
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusLabel)
                    .addComponent(stopButton))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        cmcLabel.setText("CMC");

        mcodeLabel.setText("MCODE");

        mclLabel.setText("MCL");

        cOneLabel.setText("ClusterOne");

        thresholdLabel.setText("Overlap Threshold");

        overlapThresholdTf.setText("0.8");

        clustersThresholdLabel.setText("Min. clusters in a component");

        clusterThresholdTf.setText("3");

        similarityLabel.setText("Similarity measure");

        similarityComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Match coefficient", "Simpson coefficient", "Jaccard similarity", "Dice similarity" }));

        javax.swing.GroupLayout mainUIPanelLayout = new javax.swing.GroupLayout(mainUIPanel);
        mainUIPanel.setLayout(mainUIPanelLayout);
        mainUIPanelLayout.setHorizontalGroup(
            mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainUIPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainUIPanelLayout.createSequentialGroup()
                        .addComponent(networkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(mainUIPanelLayout.createSequentialGroup()
                        .addGroup(mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(statusPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(startB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(mainUIPanelLayout.createSequentialGroup()
                                .addComponent(thresholdLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
                                .addGroup(mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(networkComboBox, 0, 132, Short.MAX_VALUE)
                                    .addGroup(mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(clusterThresholdTf, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(overlapThresholdTf, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                                    .addComponent(similarityComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(28, 28, 28))
                    .addGroup(mainUIPanelLayout.createSequentialGroup()
                        .addGroup(mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(similarityLabel)
                            .addComponent(clustersThresholdLabel)
                            .addGroup(mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainUIPanelLayout.createSequentialGroup()
                                    .addComponent(cmcLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cmc))
                                .addGroup(mainUIPanelLayout.createSequentialGroup()
                                    .addComponent(pewccLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pewcc))
                                .addGroup(mainUIPanelLayout.createSequentialGroup()
                                    .addComponent(mcodeLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(mcode))
                                .addGroup(mainUIPanelLayout.createSequentialGroup()
                                    .addComponent(mclLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(mcl))
                                .addGroup(mainUIPanelLayout.createSequentialGroup()
                                    .addComponent(cOneLabel)
                                    .addGap(18, 18, 18)
                                    .addComponent(cOne))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        mainUIPanelLayout.setVerticalGroup(
            mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainUIPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(networkLabel)
                    .addComponent(networkComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainUIPanelLayout.createSequentialGroup()
                        .addGroup(mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainUIPanelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(overlapThresholdTf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(similarityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(mainUIPanelLayout.createSequentialGroup()
                                .addComponent(thresholdLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(similarityLabel)))
                        .addGap(18, 18, 18)
                        .addGroup(mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(clustersThresholdLabel)
                            .addComponent(clusterThresholdTf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(41, 41, 41)
                        .addGroup(mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(mainUIPanelLayout.createSequentialGroup()
                                .addComponent(cmcLabel)
                                .addGap(25, 25, 25)
                                .addComponent(pewccLabel))
                            .addGroup(mainUIPanelLayout.createSequentialGroup()
                                .addComponent(cmc)
                                .addGap(18, 18, 18)
                                .addComponent(pewcc)))
                        .addGap(21, 21, 21)
                        .addGroup(mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mcodeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(mcode, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(25, 25, 25)
                        .addComponent(mclLabel))
                    .addGroup(mainUIPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(mcl)))
                .addGap(25, 25, 25)
                .addGroup(mainUIPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cOneLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cOne, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(33, 33, 33)
                .addComponent(startB, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(headingLabel)
                    .addComponent(mainUIPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mainUIPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(mainPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jScrollPane1)
                .addGap(11, 11, 11))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
    }// </editor-fold>//GEN-END:initComponents
   
    private void startBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startBActionPerformed
        CyNetwork network = getSelectedNetwork();
        CyNetworkView networkview;
        
        if(network != null){
            networkview = _applicationManager.getCurrentNetworkView();
            Double mergeThreshold;
            Integer clusterThreshold;
            mergeThreshold = overlapValueValidate(overlapThresholdTf);
            if(mergeThreshold == null) {
                JOptionPane.showMessageDialog(null, "Not a valid merge threshold ", "Try a different input ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(mergeThreshold == 0.0) {
                JOptionPane.showMessageDialog(null, "Overlap Threshold should be greater than zero ! ", "Not A valid Overlap Threshold ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            clusterThreshold = clusterThresholdValidate(clusterThresholdTf);
            if(clusterThreshold == null) {
                JOptionPane.showMessageDialog(null, "Not a valid cluster threshold ", "Try a different input ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if((pewcc.isSelected() || cmc.isSelected() || mcode.isSelected() ||  mcl.isSelected() || cOne.isSelected()) == false){
                JOptionPane.showMessageDialog(null, "Select atleast one method to run ", "Try a different input ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Parameters parameters = new Parameters(mergeThreshold, similarityComboBox.getSelectedIndex(),clusterThreshold, pewcc.isSelected(), cmc.isSelected(), mcode.isSelected(), mcl.isSelected(), cOne.isSelected());
            logicThread = new PcmLogic(this, network, networkview, parameters); 
            logicThread.start();
            
        } else{
            JOptionPane.showMessageDialog(null, "IMPORT a network first! ", "No Network found ", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_startBActionPerformed
    
    public Double overlapValueValidate(javax.swing.JTextField jtf) {
        double overlapValue = 0.0;
        try{
            overlapValue = Double.parseDouble(jtf.getText());
        } catch(NumberFormatException e){
            System.out.println("Number format exception");
            return null;
        } catch(NullPointerException e){
            System.out.println("String is null");
            return null;
        }
        return overlapValue;
    }
    
    public Integer clusterThresholdValidate(javax.swing.JTextField jtf) {
        int clusterThreshold = 0;
        try{
            clusterThreshold = Integer.parseInt(jtf.getText());
        } catch(NumberFormatException e){
            System.out.println("Number format exception");
            return null;
        } catch(NullPointerException e){
            System.out.println("String is null");
            return null;
        }
        return clusterThreshold;
    }
    
    public void resultsCalculated(Collection<Complex> finalResultMerged, CyNetwork network) {
        
        if(finalResultMerged.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No results found for given parameters ", "No results ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        CytoscapeResultViewerPanel resultsPanel = new CytoscapeResultViewerPanel(_cyactivator, network);
        resultsPanel.setResult(new ArrayList<Complex>(finalResultMerged));
        resultsPanel.addToCytoscapeResultPanel();
    }
    
    
    
    
    
    
    
    
    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        if(logicThread != null){
            logicThread.end();
        }
        //if(pewccapp.getPEWCClogic().isAlive()) {
        stopcalculus(null);
        networkComboBox.setEnabled(true);
        similarityComboBox.setEnabled(true);
        clusterThresholdTf.setEnabled(true);
        overlapThresholdTf.setEnabled(true);
                
        pewcc.setEnabled(true);
        cmc.setEnabled(true);
        mcode.setEnabled(true);
        mcl.setEnabled(true);
        cOne.setEnabled(true);
        
        startB.setEnabled(true);
        stopButton.setEnabled(false);
    }//GEN-LAST:event_stopButtonActionPerformed

    private void networkComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_networkComboBoxActionPerformed
        statusLabel.setText("status");
    }//GEN-LAST:event_networkComboBoxActionPerformed
    
    public void startComputation(){
        
        startB.setEnabled(false);
        stopButton.setEnabled(true);
        statusBar.setIndeterminate(true);
        statusBar.setVisible(true);
        networkComboBox.setEnabled(false);
        similarityComboBox.setEnabled(false);
        clusterThresholdTf.setEnabled(false);
        overlapThresholdTf.setEnabled(false);
        
        pewcc.setEnabled(false);
        cmc.setEnabled(false);
        mcode.setEnabled(false);
        mcl.setEnabled(false);
        cOne.setEnabled(false);
        
        statusLabel.setText("PCM is running ......");
    }
    
    public void endComputation(){
        statusBar.setIndeterminate(false);
        statusLabel.setText("<html>Completed! Check Results Panel <html>");
        startB.setEnabled(true);
        networkComboBox.setEnabled(true);
        similarityComboBox.setEnabled(true);
        clusterThresholdTf.setEnabled(true);
        overlapThresholdTf.setEnabled(true);
        
        pewcc.setEnabled(true);
        cmc.setEnabled(true);
        mcode.setEnabled(true);
        mcl.setEnabled(true);
        cOne.setEnabled(true);
        
        stopButton.setEnabled(false);
       
    }
    
    public void calculatingresult(String msg){
        statusLabel.setText(msg);
    }
    
    public void stopcalculus(String message) {
        statusBar.setIndeterminate(false);
        if(message == null) {
            statusLabel.setText("Interrupted by user, click run to restart");
        }
        else {
            statusLabel.setText(message);
        }
    }
    
    protected void updateNetworkList() {
        final Set<CyNetwork> networks = _networkManager.getNetworkSet();
        final SortedSet<String> networkNames = new TreeSet<String>();

        for (CyNetwork net : networks)
                networkNames.add(net.getRow(net).get("name", String.class));

        // Clear the comboBox
        networkComboBox.setModel(new DefaultComboBoxModel());

        for (String name : networkNames)
                networkComboBox.addItem(name);

        CyNetwork currNetwork = _applicationManager.getCurrentNetwork();
        if (currNetwork != null) {
                String networkTitle = currNetwork.getRow(currNetwork).get("name", String.class);
                networkComboBox.setSelectedItem(networkTitle);			
        }
        
    }
    
    
    public void addItemListener(final ItemListener newListener) { // huh ?
        networkComboBox.addItemListener(newListener);
    }
    
    public CyNetwork getSelectedNetwork() {
        
        for (CyNetwork net : _networkManager.getNetworkSet()) {
                String networkTitle = net.getRow(net).get("name", String.class);
                if (networkTitle.equals(networkComboBox.getSelectedItem()))
                        return net;
        }
        return null;
    }
   
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cOne;
    private javax.swing.JLabel cOneLabel;
    private javax.swing.JTextField clusterThresholdTf;
    private javax.swing.JLabel clustersThresholdLabel;
    private javax.swing.JCheckBox cmc;
    private javax.swing.JLabel cmcLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel headingLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel mainUIPanel;
    private javax.swing.JCheckBox mcl;
    private javax.swing.JLabel mclLabel;
    private javax.swing.JCheckBox mcode;
    private javax.swing.JLabel mcodeLabel;
    protected javax.swing.JComboBox networkComboBox;
    private javax.swing.JLabel networkLabel;
    private javax.swing.JTextField overlapThresholdTf;
    private javax.swing.JCheckBox pewcc;
    private javax.swing.JLabel pewccLabel;
    private javax.swing.JComboBox<String> similarityComboBox;
    private javax.swing.JLabel similarityLabel;
    private javax.swing.JButton startB;
    private javax.swing.JProgressBar statusBar;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton stopButton;
    private javax.swing.JLabel thresholdLabel;
    // End of variables declaration//GEN-END:variables
    @Override
    public void handleEvent(NetworkAddedEvent e){
        CyNetwork net = e.getNetwork();
        String title = net.getRow(net).get(CyNetwork.NAME, String.class);
        ((DefaultComboBoxModel)this.networkComboBox.getModel()).addElement(title);
    }

    @Override
    public void handleEvent(NetworkDestroyedEvent e){
        updateNetworkList();
    }
    
    
    public void activate() {
        _cyactivator.registerService(this, NetworkAddedListener.class);
        _cyactivator.registerService(this, NetworkDestroyedListener.class);
        
        CytoPanel cytoPanel = _cySwingApp.getCytoPanel(getCytoPanelName());
        
        if (cytoPanel.getState() == CytoPanelState.HIDE) {
                cytoPanel.setState(CytoPanelState.DOCK);
        }
        setVisible(true);
        cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(getComponent()));
        updateNetworkList();
        //Reset the values here
    }
    
    /**
     * Deactivates and hides the control panel.
     */
    public void deactivate() {
        _cyactivator.unregisterService(this, NetworkAddedListener.class);
        _cyactivator.unregisterService(this, NetworkDestroyedListener.class);
    }
    
    @Override
    public Icon getIcon() {
        return null;
    }
    
    @Override
    public String getTitle() {
        return CytoscapeAppActivator.APPNAME;
    }
    
    @Override
    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.WEST;
    }
    
    public Component getComponent() {
        return this;
    }
    
         
}
