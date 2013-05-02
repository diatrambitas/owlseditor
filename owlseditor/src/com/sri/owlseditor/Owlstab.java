/*****************************************************************************************
"The contents of this file are subject to the Mozilla Public License  Version 1.1 
(the "License"); you may not use this file except in compliance with the License.  
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, 
WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for the specific 
language governing rights and limitations under the License.

The Original Code is OWL-S Editor for Protege.

The Initial Developer of the Original Code is SRI International. 
Portions created by the Initial Developer are Copyright (C) 2004 the Initial Developer.  
All Rights Reserved.
 ******************************************************************************************/
package com.sri.owlseditor;

import java.awt.BorderLayout;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import com.sri.owlseditor.consistency.ConsistencyCheckAction;
import com.sri.owlseditor.graph.GraphDisplay;
import com.sri.owlseditor.graph.GraphViewAction;
import com.sri.owlseditor.iopr.IOPRManager;
import com.sri.owlseditor.iopr.IOPRManagerAction;
import com.sri.owlseditor.options.OptionsAction;
import com.sri.owlseditor.options.OptionsManager;
import com.sri.owlseditor.util.Cleaner;
import com.sri.owlseditor.wsdl.GenerateFromWSDLAction;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.widget.AbstractTabWidget;
import edu.stanford.smi.protege.widget.WidgetMapper;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
//import edu.stanford.smi.protegex.owl.jena.URIResolver;
//import edu.stanford.smi.protegex.owl.ui.jena.OntPolicyManager;

/** This is the main class of the OWL-S Editor. */
public class Owlstab extends AbstractTabWidget {
	public static final String TIME_URI = "http://www.isi.edu/~pan/damltime/time-entry.owl";
	public static final String SWRL_URI = "http://www.w3.org/2003/11/swrl";
	public static final String SWRLIMPORT_URI = "http://www.daml.org/rules/proposal/swrl.owl";
	public static final String SWRLB_URI = "http://www.w3.org/2003/11/swrlb";
	public static final String SWRLBIMPORT_URI = "http://www.daml.org/rules/proposal/swrlb.owl";

	/*
	 * public static final String GROUNDING_URI =
	 * "http://www.daml.org/services/owl-s/1.1/Grounding.owl"; public static
	 * final String PROFILE_URI =
	 * "http://www.daml.org/services/owl-s/1.1/Profile.owl"; public static final
	 * String PROCESS_URI =
	 * "http://www.daml.org/services/owl-s/1.1/Process.owl"; public static final
	 * String SERVICE_URI =
	 * "http://www.daml.org/services/owl-s/1.1/Service.owl"; public static final
	 * String LIST_URI =
	 * "http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl"; public
	 * static final String EXPR_URI =
	 * "http://www.daml.org/services/owl-s/1.1/generic/Expression.owl";
	 */

	public static final String GROUNDING_URI = "http://www.daml.org/services/owl-s/1.2/Grounding.owl";
	public static final String PROFILE_URI = "http://www.daml.org/services/owl-s/1.2/Profile.owl";
	public static final String PROCESS_URI = "http://www.daml.org/services/owl-s/1.2/Process.owl";
	public static final String SERVICE_URI = "http://www.daml.org/services/owl-s/1.2/Service.owl";
	public static final String LIST_URI = "http://www.daml.org/services/owl-s/1.2/generic/ObjectList.owl";
	public static final String EXPR_URI = "http://www.daml.org/services/owl-s/1.2/generic/Expression.owl";

	public URI groundingURI;
	public URI profileURI;
	public URI processURI;
	public URI serviceURI;
	public URI listURI;
	public URI exprURI;
	public URI timeURI;
	public URI swrlimportURI;
	public URI swrlbimportURI;

	private String GROUNDING_PREFIX = "grounding";
	private String PROFILE_PREFIX = "profile";
	private String PROCESS_PREFIX = "process";
	private String SERVICE_PREFIX = "service";
	private String LIST_PREFIX = "list";
	private String SWRL_PREFIX = "swrl";
	// private String SWRLIMPORT_PREFIX = "swrlimport";
	private String SWRLB_PREFIX = "swrlb";
	// private String SWRLBIMPORT_PREFIX = "swrlbimport";
	private String EXPR_PREFIX = "expr";
	private String TIME_PREFIX = "time";

	private JenaOWLModel okb;
	private JPanel mainpane;
	private JSplitPane splitpane;
	private ServiceEditor editor;
	private ServiceSelector selector;
	private IOPRManager ioprManager;
	private GraphDisplay graphDisplay; // graph-display
	private OptionsManager options;
	private JToolBar toolbar;

	// private JButton searchButton;
	private JButton ioprManagerButton;
	private JButton generateFromWSDLButton;
	private JButton optionsButton;
	private JButton graphViewButton;
	private JButton consistencyCheckButton;

	private Project project;
	private OWLOntology oi;
	private NamespaceManager nm;

	private WidgetMapper wm;

	// startup code
	public void initialize() {
		setLabel("OWL-S Editor");
		okb = (JenaOWLModel) getKnowledgeBase();
		oi = okb.getDefaultOWLOntology();
		nm = okb.getNamespaceManager();
		project = getProject();

		wm = new OWLSEditorWidgetMapper(project, okb);
		project.setWidgetMapper(wm);

		Iterator it = okb.getClses().iterator();
		while (it.hasNext()) {
			System.out.println("Class: " + it.next());
		}

		// We need to be in the "main" ontology when adding imports.
		TripleStoreModel tsm = okb.getTripleStoreModel();
		TripleStore top = tsm.getTopTripleStore();
		TripleStore current = tsm.getActiveTripleStore();
		if (current != top) {
			tsm.setActiveTripleStore(top);
			setupPrefixes();
			setupImports();
			tsm.setActiveTripleStore(current);
		} else {
			setupPrefixes();
			setupImports();
		}

		it = okb.getClses().iterator();
		while (it.hasNext()) {
			System.out.println("Class: " + it.next());
		}

		/* Set up the prefixes of the OWL-S ontologies the way we want them */

		// OWL-S imports fully loaded, so we can move on

		setLayout(new BorderLayout());

		mainpane = ComponentFactory.createPanel();
		mainpane.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		mainpane.setLayout(new BorderLayout());

		splitpane = ComponentFactory.createLeftRightSplitPane();
		editor = new ServiceEditor(project);
		splitpane.setRightComponent(editor);
		selector = new ServiceSelector(okb, project, editor); // ServiceSelector
																// Created
		splitpane.setLeftComponent(selector);
		mainpane.add(splitpane, BorderLayout.CENTER);
		add(mainpane, BorderLayout.CENTER);
		toolbar = ComponentFactory.createToolBar();

		ioprManager = new IOPRManager(okb);
		selector.addServiceSelectorListener(ioprManager);

		graphDisplay = new GraphDisplay(okb,
				(OWLIndividual) selector.getSelectedInstance()); // graph-display
		selector.addServiceSelectorListener(graphDisplay); // graph-display

		options = new OptionsManager(getProject());
		Cleaner.getInstance().registerCleanerListener(options);

		setupToolbar();
		add(toolbar, BorderLayout.NORTH);
		System.out.println("--- OWL-S Editor up and running ---");
	}

	/* Returns a String with the URI to our local version of the given file */
	private URL getLocalURL(String filename) {
		File tempfile = new File("plugins/com.sri.owlseditor/owl-s/" + filename);
		URL localURL = null;
		try {
			localURL = new URL(tempfile.toURI().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return localURL;
	}

	/**
	 * @return true if a reload is required
	 */
	private void setupImports() {
		ProtegeOWLParser	pop;
		OWLOntology			oo;

		try {
			groundingURI = new URI(GROUNDING_URI);
			profileURI = new URI(PROFILE_URI);
			processURI = new URI(PROCESS_URI);
			serviceURI = new URI(SERVICE_URI);
			listURI = new URI(LIST_URI);
			exprURI = new URI(EXPR_URI);
			timeURI = new URI(TIME_URI);
			swrlimportURI = new URI(SWRLIMPORT_URI);
			swrlbimportURI = new URI(SWRLBIMPORT_URI);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// URIResolver resolver = okb.getURIResolver();
		// resolver.setPhysicalURL(groundingURI, getLocalURL("Grounding.owl"));
		// resolver.setPhysicalURL(profileURI, getLocalURL("Profile.owl"));
		// resolver.setPhysicalURL(processURI, getLocalURL("Process.owl"));
		// resolver.setPhysicalURL(serviceURI, getLocalURL("Service.owl"));
		// resolver.setPhysicalURL(listURI, getLocalURL("ObjectList.owl"));
		// resolver.setPhysicalURL(exprURI, getLocalURL("Expression.owl"));
		// resolver.setPhysicalURL(timeURI, getLocalURL("time-entry.owl"));
		// resolver.setPhysicalURL(swrlimportURI, getLocalURL("swrl.owl"));
		// resolver.setPhysicalURL(swrlbimportURI, getLocalURL("swrlb.owl"));

		try {
			// OntPolicyManager.saveCurrentModel(project);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String declarations[] = new String [] {"Expression", "Process", "Grounding", "Profile", "Service",
					"ObjectList", "swrl", "time-entry", "swrlb"};
			for(int j=0;j<declarations.length;j++) {
				ImportHelper ih = new ImportHelper(okb);
				declareOntology(ih, declarations[j]);
				ih.importOntologies(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TripleStoreModel tsm = okb.getTripleStoreModel();
		tsm.updateEditableResourceState();
		// Iterator<Triple> it = tsm.getActiveTripleStore().listTriples();
		// while(it.hasNext())
		// {
		// System.out.println(it.next());
		// }
		// tsm.endTripleStoreChanges();
		
		java.util.Collection imports = oi.getImports();
		Iterator i = imports.iterator();

		while (i.hasNext()) {
			System.out.println(i.next());
		}

		i = okb.getAllImports().iterator();
		while (i.hasNext()) {
			System.out.println(i.next());
		}
	}

	private void declareOntology(ImportHelper ih, String file) throws URISyntaxException, OntologyLoadException {
//		OWLOntology			oo;
//		ProtegeOWLParser	pop;
//		OWLModel			om;
		
		System.out.println("Importing " + file + "... "
				+ getLocalURL(file + ".owl").toURI());
		//		om = ProtegeOWL.createJenaOWLModelFromURI(getLocalURL(file + ".owl").toURI().toString());
		ih.addImport(getLocalURL(file + ".owl").toURI());
//		oo = new DefaultOWLOntology();
//		pop = new ProtegeOWLParser(oo.getOWLModel());
//		pop.run(getLocalURL(file + ".owl").toURI());
//		oi.addImports(om.getDefaultOWLOntology());
		// oi.addImports(EXPR_URI);
	}

	private void setupPrefixes() {
		String prefix;

		prefix = nm.getPrefix(GROUNDING_URI + "#");
		if (prefix != null && !GROUNDING_PREFIX.equals(prefix))
			nm.setModifiable(prefix, true);
		nm.setPrefix(GROUNDING_URI + "#", GROUNDING_PREFIX);

		prefix = nm.getPrefix(PROFILE_URI + "#");
		if (prefix != null && !PROFILE_PREFIX.equals(prefix))
			nm.setModifiable(prefix, true);
		nm.setPrefix(PROFILE_URI + "#", PROFILE_PREFIX);

		prefix = nm.getPrefix(PROCESS_URI + "#");
		if (prefix != null && !PROCESS_PREFIX.equals(prefix))
			nm.setModifiable(prefix, true);
		nm.setPrefix(PROCESS_URI + "#", PROCESS_PREFIX);

		prefix = nm.getPrefix(SERVICE_URI + "#");
		if (prefix != null && !SERVICE_PREFIX.equals(prefix))
			nm.setModifiable(prefix, true);
		nm.setPrefix(SERVICE_URI + "#", SERVICE_PREFIX);

		prefix = nm.getPrefix(LIST_URI + "#");
		if (prefix != null && !LIST_PREFIX.equals(prefix))
			nm.setModifiable(prefix, true);
		nm.setPrefix(LIST_URI + "#", LIST_PREFIX);

		prefix = nm.getPrefix(SWRL_URI + "#");
		if (prefix != null && !SWRL_PREFIX.equals(prefix))
			nm.setModifiable(prefix, true);
		nm.setPrefix(SWRL_URI + "#", SWRL_PREFIX);

		prefix = nm.getPrefix(SWRLB_URI + "#");
		if (prefix != null && !SWRLB_PREFIX.equals(prefix))
			nm.setModifiable(prefix, true);
		nm.setPrefix(SWRLB_URI + "#", SWRLB_PREFIX);

		prefix = nm.getPrefix(EXPR_URI + "#");
		if (prefix != null && !EXPR_PREFIX.equals(prefix))
			nm.setModifiable(prefix, true);
		nm.setPrefix(EXPR_URI + "#", EXPR_PREFIX);

		prefix = nm.getPrefix(TIME_URI + "#");
		if (prefix != null && !TIME_PREFIX.equals(prefix))
			nm.setModifiable(prefix, true);
		nm.setPrefix(TIME_URI + "#", TIME_PREFIX);

		Iterator it = nm.getPrefixes().iterator();
		while (it.hasNext()) {
			String p = (String) it.next();
			System.out.println(p + " : " + nm.getNamespaceForPrefix(p));
		}
	}

	/** We need to kill all the event handlers here. */
	public void dispose() {
		System.out.println("OWL-S Editor closing. Performing cleanup...");

		// cleanup all registered listeners
		Cleaner.getInstance().fire();

		mainpane = null;
		splitpane = null;
		editor = null;
		selector = null;
		toolbar = null;

		// searchButton = null;
		ioprManagerButton = null;
		generateFromWSDLButton = null;
		optionsButton = null;
		graphViewButton = null;

		okb = null;

		System.out.println("...done");
	}

	private void setupToolbar() {
		// Set up all the buttons, with their action handlers, icons
		// and tool tips.

		/*
		 * searchButton = toolbar.add(new SearchAction(okb));
		 * searchButton.setToolTipText("Search for OWL-S instances on the web\n"
		 * + " (not implemented yet)");
		 */

		ioprManagerButton = toolbar.add(new IOPRManagerAction(ioprManager));
		ioprManagerButton
				.setToolTipText("Input/Output/Precondition/Result Manager");

		generateFromWSDLButton = toolbar.add(new GenerateFromWSDLAction(okb));
		generateFromWSDLButton
				.setToolTipText("Generate OWL-S instances from a WSDL document");

		/** The action handler for the Generate from BPEL toolbar button item */

		/*
		 * class GenerateFromBPELAction extends AbstractAction{ public
		 * GenerateFromBPELAction(OWLModel okb) { super("",
		 * OWLSIcons.getGenerateFromBPELIcon()); } public void
		 * actionPerformed(ActionEvent e){
		 * //System.out.println("Generate from BPEL"); } }
		 * generateFromBPELButton = toolbar.add(new
		 * GenerateFromBPELAction(okb)); generateFromBPELButton.setToolTipText(
		 * "Generate OWL-S instances from a BPEL4WS document\n" +
		 * " (not implemented yet)");
		 */

		// graph-view-mod
		graphViewButton = toolbar.add(new GraphViewAction(graphDisplay));
		graphViewButton
				.setToolTipText("Graph overview centered on the currently selected instance");

		/** The action handler for the Options toolbar button */
		optionsButton = toolbar.add(new OptionsAction(options));
		optionsButton.setToolTipText("Options");

		// consistency check
		consistencyCheckButton = toolbar.add(new ConsistencyCheckAction(okb));
		consistencyCheckButton.setToolTipText("Consistency Check");
	}

	// this method is useful for debugging
	public static void main(String[] args) {
		edu.stanford.smi.protege.Application.main(args);
	}
}
