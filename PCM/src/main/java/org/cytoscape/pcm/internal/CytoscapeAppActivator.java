package org.cytoscape.pcm.internal;

import java.util.Properties;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.pcm.internal.PcmGui.PanelState;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.osgi.framework.BundleContext;

/**
 * @author SrikanthB
 *  This class is the entry point to Cytoscape app
 */


public class CytoscapeAppActivator extends AbstractCyActivator {
    private BundleContext context;
    public static final String APPNAME = "PCM";
    
    public CytoscapeAppActivator() {
        super();
    }
    
    public void start(BundleContext context) throws Exception {
        this.context = context;
        CyApplicationManager cyApplicationManager = getService(context, CyApplicationManager.class);
        PcmGui panel = new PcmGui();
        CyNetworkManager networkManager = getService(context, CyNetworkManager.class);
        CyNetworkViewFactory networkViewFactory = getService(context, CyNetworkViewFactory.class);
        CyNetworkViewManager networkViewManager = getService(context, CyNetworkViewManager.class);
        CyAppAdapter adapter = getService(context, CyAppAdapter.class);
        CySwingApplication cySwingApp = getService(context, CySwingApplication.class);
        registerService(context, panel, CytoPanelComponent.class, new Properties());
        
        OpenPcmGuiAction oplaction = new OpenPcmGuiAction(panel, cyApplicationManager);
        ClosePcmGuiAction cplaction = new ClosePcmGuiAction(panel, cyApplicationManager);
        HelpPcmAction helpAction = new HelpPcmAction(cyApplicationManager);
        
        registerAllServices(context, oplaction, new Properties());
        registerAllServices(context, helpAction, new Properties());
        registerAllServices(context, cplaction, new Properties());
        
        // intializes panel
        panel.initialize(
            this,
            cySwingApp,
            cyApplicationManager,
            networkManager,
            networkViewFactory,
            networkViewManager,
            adapter);

        // starts off the panel in a closed state
        panel.setPanelState(PanelState.CLOSED);
    }
    
    /**
     * Returns the service registered with the given class.
     */
    public <S> S getService(Class<S> cls) {
            return this.getService(context, cls);
    }
	
    /**
     * Returns the service registered with the given class.
     */
    public <S> S getService(Class<S> cls, String properties) {
            return this.getService(context, cls, properties);
    }
    
    /**
     * Registers an object as a service in the Cytoscape Swing application.
     * 
     * @param  object      the object to register
     * @param  cls         the class of the object
     * @param  properties  additional properties to use for registering
     */
    public <S> void registerService(S object, Class<S> cls) {
            registerService(object, cls, new Properties());
    }
	
    /**
     * Registers an object as a service in the Cytoscape Swing application.
     * 
     * @param  object      the object to register
     * @param  cls         the class of the object
     * @param  properties  additional properties to use for registering
     */
    public void registerService(Object object, Class<?> cls, Properties properties) {
            CyServiceRegistrar registrar = this.getService(CyServiceRegistrar.class);
            registrar.registerService(object, cls, properties);
    }
        
        
    /**
     * Unregisters an object as a service in the Cytoscape Swing application.
     * 
     * @param  object      the object to register
     * @param  cls         the class of the object
     */
    public <S> void unregisterService(S object, Class<S> cls) {
            CyServiceRegistrar registrar = this.getService(CyServiceRegistrar.class);
            registrar.unregisterService(object, cls);
    }
}

