package fr.sorbonne_u.components.hem2025e1.equipments.hem;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.BCMRuntimeException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.bases.AdjustableCI;
import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.Batteries;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.BatteriesCI;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.BatteriesUnitTester;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.connections.BatteriesConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.connections.BatteriesOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.Generator;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorCI;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorUnitTester;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.connections.GeneratorConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.connections.GeneratorOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.Heater;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterUnitTester;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterCI;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterUnitTester;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.connections.ElectricMeterConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.connections.ElectricMeterOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.SolarPanel;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.SolarPanelCI;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.SolarPanelUnitTester;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.connections.SolarPanelConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.connections.SolarPanelOutboundPort;
import fr.sorbonne_u.components.utils.tests.TestsStatistics;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.File;

// -----------------------------------------------------------------------------
/**
 * The class <code>HEM</code> implements the basis for a household energy
 * management component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * As is, this component is only a very limited starting point for the actual
 * component. The given code is there only to ease the understanding of the
 * objectives, but most of it must be replaced to get the correct code.
 * Especially, no registration of the components representing the appliances
 * is given.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code NUMBER_OF_STANDARD_THREADS >= 0}
 * invariant	{@code NUMBER_OF_SCHEDULABLE_THREADS >= 0}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code X_RELATIVE_POSITION >= 0}
 * invariant	{@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p>Created on : 2021-09-09</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@OfferedInterfaces(offered = {HEMRegistrationCI.class})
@RequiredInterfaces(required = {ClocksServerCI.class,
								AdjustableCI.class,
								ElectricMeterCI.class,
								BatteriesCI.class,
								SolarPanelCI.class,
								GeneratorCI.class})
public class			HEM
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI for the HEM registration inbound port.							*/
	public static final String				REGISTRATION_INBOUND_PORT_URI = "hem-registration";

	/** when true, methods trace their actions.								*/
	public static boolean					VERBOSE = false;
	/** when true, methods trace their actions.								*/
	public static boolean					DEBUG = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int						X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int						Y_RELATIVE_POSITION = 0;

	/** port to allow appliances to register with HEM.						*/
	protected HEMRegistrationInboundPort	registrationPort;

	/** port to connect to the electric meter.								*/
	protected ElectricMeterOutboundPort		meterop;

	/** port to connect to the batteries.									*/
	protected BatteriesOutboundPort			batteriesop;
	/** port to connect to the solar panel.									*/
	protected SolarPanelOutboundPort		solarPanelop;
	/** port to connect to the generator.									*/
	protected GeneratorOutboundPort			generatorop;

	/** when true, manage the heater in a customised way, otherwise let
	 *  it register itself as an adjustable appliance.						*/
	protected boolean						isPreFirstStep;
	/** port to connect to the heater when managed in a customised way.		*/
	protected AdjustableOutboundPort		heaterop;

	/** Map to store registered appliances: applianceId -> AdjustableOutboundPort */
	protected Map<String, AdjustableOutboundPort> registeredAppliances;
	/** Counter for generating unique connector class names */
	protected int							connectorCounter = 0;

	// Execution/Simulation

	/** one thread for the method execute.									*/
	protected static int					NUMBER_OF_STANDARD_THREADS = 1;
	/** one thread to schedule this component test actions.					*/
	protected static int					NUMBER_OF_SCHEDULABLE_THREADS = 1;

	/** when true, this implementation of the HEM performs the tests
	 *  that are planned in the method execute.								*/
	protected boolean						performTest;
	/** accelerated clock used for the tests.								*/
	protected AcceleratedClock				ac;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the static implementation invariants are observed, false
	 * otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the static invariants are observed, false otherwise.
	 */
	public static boolean	staticImplementationInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				NUMBER_OF_STANDARD_THREADS >= 0,
				HEM.class,
				"NUMBER_OF_STANDARD_THREADS >= 0");
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				NUMBER_OF_SCHEDULABLE_THREADS >= 0,
				HEM.class,
				"NUMBER_OF_SCHEDULABLE_THREADS");
		return ret;
	}

	/**
	 * return true if the implementation invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hem != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param hem	instance to be tested.
	 * @return		true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(HEM hem)
	{
		assert	hem != null : new PreconditionException("hem != null");

		boolean ret = true;
		ret &= staticImplementationInvariants();
		return ret;
	}

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the static invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				X_RELATIVE_POSITION >= 0,
				HEM.class,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkStaticInvariant(
				Y_RELATIVE_POSITION >= 0,
				HEM.class,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hem != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param hem	instance to be tested.
	 * @return		true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(HEM hem)
	{
		assert	hem != null : new PreconditionException("hem != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a household energy manager component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected 			HEM()
	{
		// by default, perform the tests planned in the method execute.
		this(true);
	}

	/**
	 * create a household energy manager component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param performTest	if {@code true}, the HEM performs the planned tests, otherwise not.
	 */
	protected 			HEM(boolean performTest)
	{
		// 1 standard thread to execute the method execute and 1 schedulable
		// thread that is used to perform the tests
		super(NUMBER_OF_STANDARD_THREADS, NUMBER_OF_SCHEDULABLE_THREADS);

		this.performTest = performTest;

		// by default, consider this execution as one in the pre-first step
		// and manage the heater in a customised way.
		this.isPreFirstStep = true;

		// Initialize the map for storing registered appliances
		this.registeredAppliances = new HashMap<>();

		// Create and publish registration inbound port immediately
		// so appliances can connect during their start() method
		try {
			this.registrationPort = new HEMRegistrationInboundPort(
					REGISTRATION_INBOUND_PORT_URI, this);
			this.registrationPort.publishPort();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (VERBOSE) {
			this.tracer.get().setTitle("Home Energy Manager component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		assert	HEM.implementationInvariants(this) :
				new ImplementationInvariantException(
						"HEM.implementationInvariants(this)");
		assert	HEM.invariants(this) :
				new InvariantException("HEM.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			// Registration port already published in constructor
			
			this.meterop = new ElectricMeterOutboundPort(this);
			this.meterop.publishPort();
			this.doPortConnection(
					this.meterop.getPortURI(),
					ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI,
					ElectricMeterConnector.class.getCanonicalName());
			this.batteriesop = new BatteriesOutboundPort(this);
			this.batteriesop.publishPort();
			this.doPortConnection(
					batteriesop.getPortURI(),
					Batteries.STANDARD_INBOUND_PORT_URI,
					BatteriesConnector.class.getCanonicalName());
			this.solarPanelop = new SolarPanelOutboundPort(this);
			this.solarPanelop.publishPort();
			this.doPortConnection(
					this.solarPanelop.getPortURI(),
					SolarPanel.STANDARD_INBOUND_PORT_URI,
					SolarPanelConnector.class.getCanonicalName());
			this.generatorop = new GeneratorOutboundPort(this);
			this.generatorop.publishPort();
			this.doPortConnection(
					this.generatorop.getPortURI(),
					Generator.STANDARD_INBOUND_PORT_URI,
					GeneratorConnector.class.getCanonicalName());

			if (this.isPreFirstStep) {
				// in this case, connect using the statically customised
				// heater connector and keep a specific outbound port to
				// call the heater.
				this.heaterop = new AdjustableOutboundPort(this);
				this.heaterop.publishPort();
				this.doPortConnection(
						this.heaterop.getPortURI(),
						Heater.EXTERNAL_CONTROL_INBOUND_PORT_URI,
						HeaterConnector.class.getCanonicalName());
			}
		} catch (Throwable e) {
			throw new ComponentStartException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void	execute() throws Exception
	{
		// First, get the clock and wait until the start time that it specifies.
		this.ac = null;
		ClocksServerOutboundPort clocksServerOutboundPort =
											new ClocksServerOutboundPort(this);
		clocksServerOutboundPort.publishPort();
		this.doPortConnection(
					clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
		this.traceMessage("HEM gets the clock.\n");
		this.ac = clocksServerOutboundPort.getClock(CVMIntegrationTest.CLOCK_URI);
		this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
		clocksServerOutboundPort.unpublishPort();
		this.traceMessage("HEM waits until start time.\n");
		this.ac.waitUntilStart();
		this.traceMessage("HEM starts.\n");

		if (this.performTest) {
			this.logMessage("Electric meter tests start.");
			this.testMeter();
			this.logMessage("Electric meter tests end.");
			this.logMessage("Batteries tests start.");
			this.testBatteries();
			this.logMessage("Batteries tests end.");
			this.logMessage("Solar Panel tests start.");
			this.testSolarPanel();
			this.logMessage("Solar Panel tests end.");
			this.logMessage("Generator tests start.");
			this.testGenerator();
			this.logMessage("Generator tests end.");
			
			// Wait a bit for appliances to register
			Thread.sleep(2000);
			
			// Test registered appliances
			this.logMessage("Registered appliances tests start.");
			this.testRegisteredAppliances();
			this.logMessage("Registered appliances tests end.");
			
			if (this.isPreFirstStep) {
				this.scheduleTestHeater();
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.meterop.getPortURI());
		this.doPortDisconnection(this.batteriesop.getPortURI());
		this.doPortDisconnection(this.solarPanelop.getPortURI());
		this.doPortDisconnection(this.generatorop.getPortURI());
		if (this.isPreFirstStep) {
			this.doPortDisconnection(this.heaterop.getPortURI());
		}
		
		// Disconnect registered appliances
		for (AdjustableOutboundPort port : registeredAppliances.values()) {
			this.doPortDisconnection(port.getPortURI());
		}
		
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.registrationPort.unpublishPort();
			this.meterop.unpublishPort();
			this.batteriesop.unpublishPort();
			this.solarPanelop.unpublishPort();
			this.generatorop.unpublishPort();
			if (this.isPreFirstStep) {
				this.heaterop.unpublishPort();
			}
			
			// Unpublish registered appliance ports
			for (AdjustableOutboundPort port : registeredAppliances.values()) {
				port.unpublishPort();
			}
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	/**
	 * Register an appliance with the HEM by dynamically generating a connector
	 * that adapts the appliance's specific interface to AdjustableCI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code id != null && !id.isEmpty()}
	 * pre	{@code controlPortURI != null && !controlPortURI.isEmpty()}
	 * pre	{@code xmlDescriptorPath != null && !xmlDescriptorPath.isEmpty()}
	 * post	{@code registeredAppliances.containsKey(id)}
	 * </pre>
	 *
	 * @param id					unique identifier for the appliance
	 * @param controlPortURI		URI of the appliance's control inbound port
	 * @param xmlDescriptorPath		path to the XML descriptor file
	 * @throws Exception			if registration fails
	 */
	public void register(String id, String controlPortURI, String xmlDescriptorPath) 
			throws Exception
	{
		assert id != null && !id.isEmpty() : 
			new PreconditionException("id != null && !id.isEmpty()");
		assert controlPortURI != null && !controlPortURI.isEmpty() : 
			new PreconditionException("controlPortURI != null && !controlPortURI.isEmpty()");
		assert xmlDescriptorPath != null && !xmlDescriptorPath.isEmpty() : 
			new PreconditionException("xmlDescriptorPath != null && !xmlDescriptorPath.isEmpty()");

		this.traceMessage("HEM: Registering appliance " + id + " with control port " + 
						  controlPortURI + "\n");

		// Parse XML descriptor
		File xmlFile = new File(xmlDescriptorPath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		doc.getDocumentElement().normalize();

		Element root = doc.getDocumentElement();
		String offeredInterface = root.getAttribute("offered");

		this.traceMessage("HEM: Appliance offers interface: " + offeredInterface + "\n");

		// Generate connector class using Javassist
		String connectorClassName = generateConnector(id, offeredInterface, doc);

		this.traceMessage("HEM: Generated connector class: " + connectorClassName + "\n");

		// Create outbound port
		AdjustableOutboundPort port = new AdjustableOutboundPort(this);
		port.publishPort();

		// Connect to appliance
		this.doPortConnection(
				port.getPortURI(),
				controlPortURI,
				connectorClassName);

		// Store the port for later use
		registeredAppliances.put(id, port);

		this.logMessage("HEM: Successfully registered appliance " + id + 
						" via connector " + connectorClassName);
		System.out.println(">>> HEM: Successfully registered appliance " + id + 
						   " via connector " + connectorClassName + " <<<");

		assert registeredAppliances.containsKey(id) :
			new PostconditionException("registeredAppliances.containsKey(id)");
	}

	/**
	 * Generate a connector class dynamically using Javassist that adapts
	 * an appliance-specific interface to AdjustableCI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code applianceId != null}
	 * pre	{@code offeredInterface != null}
	 * pre	{@code doc != null}
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @param applianceId			unique identifier for the appliance
	 * @param offeredInterface		canonical name of the offered interface
	 * @param doc					parsed XML descriptor document
	 * @return						canonical name of the generated connector class
	 * @throws Exception			if connector generation fails
	 */
	protected String generateConnector(String applianceId, String offeredInterface, 
										Document doc) 
			throws Exception
	{
		assert applianceId != null : new PreconditionException("applianceId != null");
		assert offeredInterface != null : new PreconditionException("offeredInterface != null");
		assert doc != null : new PreconditionException("doc != null");

		ClassPool pool = ClassPool.getDefault();
		
		// Create unique connector class name
		String connectorClassName = "fr.sorbonne_u.components.hem2025e1.equipments.hem.ConnectorFor_" + 
									applianceId + "_" + (connectorCounter++);
		
		// Create new class extending AbstractConnector and implementing AdjustableCI
		CtClass connectorClass = pool.makeClass(connectorClassName);
		CtClass abstractConnectorClass = pool.get("fr.sorbonne_u.components.connectors.AbstractConnector");
		CtClass adjustableInterface = pool.get("fr.sorbonne_u.components.hem2025.bases.AdjustableCI");
		
		connectorClass.setSuperclass(abstractConnectorClass);
		connectorClass.addInterface(adjustableInterface);

		// Add instance variables from XML
		Element root = doc.getDocumentElement();
		NodeList instanceVars = root.getElementsByTagName("instance-var");
		for (int i = 0; i < instanceVars.getLength(); i++) {
			Element varElement = (Element) instanceVars.item(i);
			String modifiers = varElement.getAttribute("modifiers");
			String type = varElement.getAttribute("type");
			String name = varElement.getAttribute("name");
			String staticInit = varElement.getAttribute("static-init");

			String fieldDeclaration = modifiers + " " + type + " " + name;
			if (staticInit != null && !staticInit.isEmpty()) {
				fieldDeclaration += " = " + staticInit;
			}
			fieldDeclaration += ";";

			CtField field = CtField.make(fieldDeclaration, connectorClass);
			connectorClass.addField(field);
		}

		// Generate internal helper methods
		NodeList internalMethods = root.getElementsByTagName("internal");
		for (int i = 0; i < internalMethods.getLength(); i++) {
			Element methodElement = (Element) internalMethods.item(i);
			String methodCode = generateInternalMethod(methodElement, offeredInterface);
			CtMethod method = CtMethod.make(methodCode, connectorClass);
			connectorClass.addMethod(method);
		}

		// Generate AdjustableCI interface methods
		generateAdjustableMethod(connectorClass, root, "maxMode", offeredInterface, "int");
		generateAdjustableMethod(connectorClass, root, "upMode", offeredInterface, "boolean");
		generateAdjustableMethod(connectorClass, root, "downMode", offeredInterface, "boolean");
		generateAdjustableMethodWithParam(connectorClass, root, "setMode", offeredInterface, "boolean", "int");
		generateAdjustableMethod(connectorClass, root, "currentMode", offeredInterface, "int");
		generateAdjustableMethodWithParam(connectorClass, root, "getModeConsumption", offeredInterface, "double", "int");
		generateAdjustableMethod(connectorClass, root, "suspended", offeredInterface, "boolean");
		generateAdjustableMethod(connectorClass, root, "suspend", offeredInterface, "boolean");
		generateAdjustableMethod(connectorClass, root, "resume", offeredInterface, "boolean");
		generateAdjustableMethod(connectorClass, root, "emergency", offeredInterface, "double");

		// Load the class
		connectorClass.toClass();
		
		String result = connectorClassName;
		assert result != null : new PostconditionException("return != null");
		return result;
	}

	/**
	 * Generate code for an internal helper method from XML.
	 * 
	 * @param methodElement		XML element describing the method
	 * @param offeredInterface	canonical name of the offered interface
	 * @return					Java code for the method
	 */
	protected String generateInternalMethod(Element methodElement, String offeredInterface)
	{
		String modifiers = methodElement.getAttribute("modifiers");
		String type = methodElement.getAttribute("type");
		String name = methodElement.getAttribute("name");

		StringBuilder methodCode = new StringBuilder();
		methodCode.append(modifiers).append(" ").append(type).append(" ").append(name).append("(");

		// Add parameters
		NodeList parameters = methodElement.getElementsByTagName("parameter");
		for (int i = 0; i < parameters.getLength(); i++) {
			Element param = (Element) parameters.item(i);
			String paramType = param.getAttribute("type");
			String paramName = param.getAttribute("name");
			
			if (i > 0) methodCode.append(", ");
			methodCode.append(paramType).append(" ").append(paramName);
		}
		methodCode.append(")");

		// Add throws clause
		NodeList thrown = methodElement.getElementsByTagName("thrown");
		if (thrown.getLength() > 0) {
			methodCode.append(" throws ");
			for (int i = 0; i < thrown.getLength(); i++) {
				if (i > 0) methodCode.append(", ");
				methodCode.append(thrown.item(i).getTextContent().trim());
			}
		}

		// Add method body
		Element bodyElement = (Element) methodElement.getElementsByTagName("body").item(0);
		String equipmentRef = bodyElement.getAttribute("equipmentRef");
		String bodyCode = bodyElement.getTextContent().trim();

		methodCode.append(" {\n");
		
		// If equipmentRef is specified, get reference to the offering component
		if (equipmentRef != null && !equipmentRef.isEmpty()) {
			methodCode.append("  ").append(offeredInterface).append(" ")
					  .append(equipmentRef).append(" = (").append(offeredInterface)
					  .append(")this.offering;\n");
		}
		
		methodCode.append("  ").append(bodyCode).append("\n");
		methodCode.append("}");

		return methodCode.toString();
	}

	/**
	 * Generate an AdjustableCI method without parameters.
	 * 
	 * @param connectorClass	class to add method to
	 * @param root				XML root element
	 * @param methodName		name of the method
	 * @param offeredInterface	canonical name of offered interface
	 * @param returnType		return type of method
	 * @throws Exception		if method generation fails
	 */
	protected void generateAdjustableMethod(CtClass connectorClass, Element root, 
											String methodName, String offeredInterface,
											String returnType) 
			throws Exception
	{
		NodeList methods = root.getElementsByTagName(methodName);
		if (methods.getLength() == 0) return;

		Element methodElement = (Element) methods.item(0);
		Element bodyElement = (Element) methodElement.getElementsByTagName("body").item(0);
		String equipmentRef = bodyElement.getAttribute("equipmentRef");
		String bodyCode = bodyElement.getTextContent().trim();

		StringBuilder methodCode = new StringBuilder();
		methodCode.append("public ").append(returnType).append(" ").append(methodName)
				  .append("() throws Exception {\n");

		// If equipmentRef is specified, get reference to the offering component
		if (equipmentRef != null && !equipmentRef.isEmpty()) {
			methodCode.append("  ").append(offeredInterface).append(" ")
					  .append(equipmentRef).append(" = (").append(offeredInterface)
					  .append(")this.offering;\n");
		}

		methodCode.append("  ").append(bodyCode).append("\n");
		methodCode.append("}");

		CtMethod method = CtMethod.make(methodCode.toString(), connectorClass);
		connectorClass.addMethod(method);
	}

	/**
	 * Generate an AdjustableCI method with one parameter.
	 * 
	 * @param connectorClass	class to add method to
	 * @param root				XML root element
	 * @param methodName		name of the method
	 * @param offeredInterface	canonical name of offered interface
	 * @param returnType		return type of method
	 * @param paramType			type of parameter
	 * @throws Exception		if method generation fails
	 */
	protected void generateAdjustableMethodWithParam(CtClass connectorClass, Element root,
													 String methodName, String offeredInterface,
													 String returnType, String paramType) 
			throws Exception
	{
		NodeList methods = root.getElementsByTagName(methodName);
		if (methods.getLength() == 0) return;

		Element methodElement = (Element) methods.item(0);
		
		// Get parameter name
		NodeList parameters = methodElement.getElementsByTagName("parameter");
		String paramName = "param";
		if (parameters.getLength() > 0) {
			Element param = (Element) parameters.item(0);
			paramName = param.getAttribute("name");
		}

		Element bodyElement = (Element) methodElement.getElementsByTagName("body").item(0);
		String equipmentRef = bodyElement.getAttribute("equipmentRef");
		String bodyCode = bodyElement.getTextContent().trim();

		StringBuilder methodCode = new StringBuilder();
		methodCode.append("public ").append(returnType).append(" ").append(methodName)
				  .append("(").append(paramType).append(" ").append(paramName)
				  .append(") throws Exception {\n");

		// If equipmentRef is specified, get reference to the offering component
		if (equipmentRef != null && !equipmentRef.isEmpty()) {
			methodCode.append("  ").append(offeredInterface).append(" ")
					  .append(equipmentRef).append(" = (").append(offeredInterface)
					  .append(")this.offering;\n");
		}

		methodCode.append("  ").append(bodyCode).append("\n");
		methodCode.append("}");

		CtMethod method = CtMethod.make(methodCode.toString(), connectorClass);
		connectorClass.addMethod(method);
	}

	/**
	 * Test all registered appliances by calling downMode() on each.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	if testing fails
	 */
	protected void testRegisteredAppliances() throws Exception
	{
		this.logMessage("HEM: Testing all registered appliances...");
		System.out.println(">>> HEM: Testing all registered appliances (" + 
						   registeredAppliances.size() + " total) <<<");
		
		for (Map.Entry<String, AdjustableOutboundPort> entry : registeredAppliances.entrySet()) {
			String applianceId = entry.getKey();
			AdjustableOutboundPort port = entry.getValue();
			
			try {
				this.logMessage("HEM: Testing appliance " + applianceId);
				System.out.println(">>> HEM: Testing appliance " + applianceId + " <<<");
				int currentMode = port.currentMode();
				this.logMessage("  Current mode: " + currentMode);
				System.out.println("  Current mode: " + currentMode);
				int maxMode = port.maxMode();
				this.logMessage("  Max mode: " + maxMode);
				System.out.println("  Max mode: " + maxMode);
				
				boolean result = port.downMode();
				this.logMessage("  downMode() result: " + result);
				System.out.println("  downMode() result: " + result);
				int newMode = port.currentMode();
				this.logMessage("  New mode: " + newMode);
				System.out.println("  New mode: " + newMode);
			} catch (Exception e) {
				this.logMessage("HEM: Error testing appliance " + applianceId + ": " + e.getMessage());
				System.err.println(">>> HEM: Error testing appliance " + applianceId + ": " + e.getMessage() + " <<<");
				e.printStackTrace();
			}
		}
		
		this.logMessage("HEM: Finished testing registered appliances.");
		System.out.println(">>> HEM: Finished testing registered appliances <<<");
	}

	/**
	 * test the {@code ElectricMeter} component.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * Calls the test methods defined in {@code ElectricMeterUnitTester}.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		testMeter() throws Exception
	{
		ElectricMeterUnitTester.runAllTests(this, this.meterop,
											new TestsStatistics());
	}

	/**
	 * test the {@code Batteries} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * @throws Exception	<i>to do</i>.
	 *
	 */
	protected void		testBatteries() throws Exception
	{
		BatteriesUnitTester.runAllTests(this, this.batteriesop,
										new TestsStatistics());
	}

	/**
	 * test the {@code SolarPanel} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * @throws Exception	<i>to do</i>.
	 *
	 */
	protected void		testSolarPanel() throws Exception
	{
		SolarPanelUnitTester.runAllTests(this, this.solarPanelop,
										 new TestsStatistics());
	}

	/**
	 * test the {@code Generator} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * @throws Exception	<i>to do</i>.
	 *
	 */
	protected void		testGenerator() throws Exception
	{
		GeneratorUnitTester.runAllTests(this, this.generatorop,
										 new TestsStatistics());
	}

	/**
	 * test the heater.
	 * 
	 * <p><strong>Gherkin specification</strong></p>
	 * 
	 * <pre>
	 * Feature: adjustable appliance mode management
	 *   Scenario: getting the max mode index
	 *     Given the heater has just been turned on
	 *     When I call maxMode()
	 *     Then the result is its max mode index
	 *   Scenario: getting the current mode index
	 *     Given the heater has just been turned on
	 *     When I call currentMode()
	 *     Then the current mode is its max mode
	 *   Scenario: going down one mode index
	 *     Given the heater is turned on
	 *     And the current mode index is the max mode index
	 *     When I call downMode()
	 *     Then the method returns true
	 *     And the current mode is its max mode minus one
	 *   Scenario: going up one mode index
	 *     Given the heater is turned on
	 *     And the current mode index is the max mode index minus one
	 *     When I call upMode()
	 *     Then the method returns true
	 *     And the current mode is its max mode
	 *   Scenario: setting the mode index
	 *     Given the heater is turned on
	 *     And the mode index 1 is legitimate
	 *     When I call setMode(1)
	 *     Then the method returns true
	 *     And the current mode is 1
	 * Feature: Getting the power consumption given a mode
	 *   Scenario: getting the power consumption of the maximum mode
	 *     Given the heater is turned on
	 *     When I get the power consumption of the maximum mode
	 *     Then the result is the maximum power consumption of the heater
	 * Feature: suspending and resuming
	 *   Scenario: checking if suspended when not
	 *     Given the heater is turned on
	 *     And it has not been suspended yet
	 *     When I check if suspended
	 *     Then it is not
	 *   Scenario: suspending
	 *     Given the heater is turned on
	 *     And it is not suspended
	 *     When I call suspend()
	 *     Then the method returns true
	 *     And the heater is suspended
	 *   Scenario: going down one mode index when suspended
	 *     Given the heater is turned on
	 *     And the heater is suspended
	 *     When I call downMode()
	 *     Then a precondition exception is thrown
	 *   Scenario: going up one mode index when suspended
	 *     Given the heater is turned on
	 *     And the heater is suspended
	 *     When I call upMode()
	 *     Then a precondition exception is thrown
	 *   Scenario: going up one mode index when suspended
	 *     Given the heater is turned on
	 *     And the heater is suspended
	 *     When I call upMode()
	 *     Then a precondition exception is thrown
	 *   Scenario: getting the current mode when suspended
	 *     Given the heater is turned on
	 *     And the heater is suspended
	 *     When I get the current mode
	 *     Then a precondition exception is thrown
	 *   Scenario: checking the emergency
	 *     Given the heater is turned on
	 *     And it has just been suspended
	 *     When I call emergency()
	 *     Then the emergency is between 0.0 and 1.0
	 *   Scenario: resuming
	 *     Given the heater is turned on
	 *     And it is suspended
	 *     When I call resume()
	 *     Then the method returns true
	 *     And the heater is not suspended
	 * </pre>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		testHeater() throws Exception
	{
		this.logMessage("Heater tests start.");
		TestsStatistics statistics = new TestsStatistics();
		try {
			this.logMessage("Feature: adjustable appliance mode management");
			this.logMessage("  Scenario: getting the max mode index");
			this.logMessage("    Given the heater has just been turned on");
			this.logMessage("    When I call maxMode()");
			this.logMessage("    Then the result is its max mode index");
			final int maxMode = heaterop.maxMode();

			statistics.updateStatistics();

			this.logMessage("  Scenario: getting the current mode index");
			this.logMessage("    Given the heater has just been turned on");
			this.logMessage("    When I call currentMode()");
			this.logMessage("    Then the current mode is its max mode");
			int result = heaterop.currentMode();
			if (result != maxMode) {
				this.logMessage("      but was: " + result);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: going down one mode index");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the current mode index is the max mode index");
			result = heaterop.currentMode();
			if (result != maxMode) {
				this.logMessage("      but was: " + result);
				statistics.failedCondition();
			}
			this.logMessage("    When I call downMode()");
			this.logMessage("    Then the method returns true");
			boolean bResult = heaterop.downMode();
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				statistics.incorrectResult();
			}
			this.logMessage("    And the current mode is its max mode minus one");
			result = heaterop.currentMode();
			if (result != maxMode - 1) {
				this.logMessage("      but was: " + result);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: going up one mode index");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the current mode index is the max mode index minus one");
			result = heaterop.currentMode();
			if (result != maxMode - 1) {
				this.logMessage("      but was: " + result);
				statistics.failedCondition();
			}
			this.logMessage("    When I call upMode()");
			this.logMessage("    Then the method returns true");
			bResult = heaterop.upMode();
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				statistics.incorrectResult();
			}
			this.logMessage("    And the current mode is its max mode");
			result = heaterop.currentMode();
			if (result != maxMode) {
				this.logMessage("      but was: " + result);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: setting the mode index");
			this.logMessage("    Given the heater is turned on");
			int index = 1;
			this.logMessage("    And the mode index 1 is legitimate");
			if (index > maxMode) {
				this.logMessage("      but was not!");
				statistics.failedCondition();
			}
			this.logMessage("    When I call setMode(1)");
			this.logMessage("    Then the method returns true");
			bResult = heaterop.setMode(1);
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				statistics.incorrectResult();
			}
			this.logMessage("    And the current mode is 1");
			result = heaterop.currentMode();
			if (result != 1) {
				this.logMessage("      but was: " + result);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("Feature: Getting the power consumption given a mode");
			this.logMessage("  Scenario: getting the power consumption of the maximum mode");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    When I get the power consumption of the maximum mode");
			double dResult = heaterop.getModeConsumption(maxMode);
			this.logMessage("    Then the result is the maximum power consumption of the heater");

			statistics.updateStatistics();

			this.logMessage("Feature: suspending and resuming");
			this.logMessage("  Scenario: checking if suspended when not");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And it has not been suspended yet");
			this.logMessage("    When I check if suspended");
			bResult = heaterop.suspended();
			this.logMessage("    Then it is not");
			if (bResult) {
				this.logMessage("      but it was!");
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: suspending");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And it is not suspended");
			bResult = heaterop.suspended();
			if (bResult) {
				this.logMessage("      but it was!");
				statistics.failedCondition();;
			}
			this.logMessage("    When I call suspend()");
			bResult = heaterop.suspend();
			this.logMessage("    Then the method returns true");
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				statistics.incorrectResult();
			}
			this.logMessage("    And the heater is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: going down one mode index when suspended");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the heater is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.failedCondition();;
			}
			this.logMessage("    When I call downMode()");
			this.logMessage("    Then a precondition exception is thrown");
			boolean old = BCMException.VERBOSE;
			try {
				BCMException.VERBOSE = false;
				heaterop.downMode();
				this.logMessage("      but it was not!");
				statistics.incorrectResult();
			} catch (Throwable e) {
			} finally {
				BCMException.VERBOSE = old;
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: going up one mode index when suspended");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the heater is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.failedCondition();;
			}
			this.logMessage("    When I call upMode()");
			this.logMessage("    Then a precondition exception is thrown");
			old = BCMException.VERBOSE;
			try {
				BCMException.VERBOSE = false;
				heaterop.upMode();
				this.logMessage("      but it was not!");
				statistics.incorrectResult();
			} catch (Throwable e) {
			} finally {
				BCMException.VERBOSE = old;
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: setting the mode when suspended");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the heater is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.failedCondition();;
			}
			this.logMessage("    And the mode index 1 is legitimate");
			if (index > maxMode) {
				this.logMessage("      but was not!");
				statistics.failedCondition();
			}
			this.logMessage("    When I call setMode(1)");
			this.logMessage("    Then a precondition exception is thrown");
			old = BCMException.VERBOSE;
			try {
				BCMException.VERBOSE = false;
				heaterop.upMode();
				this.logMessage("      but it was not!");
				statistics.incorrectResult();
			} catch (Throwable e) {
			} finally {
				BCMException.VERBOSE = old;
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: getting the current mode when suspended");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And the heater is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.failedCondition();;
			}
			this.logMessage("    When I get the current mode");
			this.logMessage("    Then a precondition exception is thrown");
			old = BCMException.VERBOSE;
			try {
				BCMException.VERBOSE = false;
				heaterop.currentMode();
				this.logMessage("      but it was not!");
				statistics.incorrectResult();
			} catch (Throwable e) {
			} finally {
				BCMException.VERBOSE = old;
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: checking the emergency");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And it has just been suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.failedCondition();;
			}
			this.logMessage("    When I call emergency()");
			dResult = heaterop.emergency();
			this.logMessage("    Then the emergency is between 0.0 and 1.0");
			if (dResult < 0.0 || dResult > 1.0) {
				this.logMessage("      but was: " + dResult);
				statistics.incorrectResult();
			}

			statistics.updateStatistics();

			this.logMessage("  Scenario: resuming");
			this.logMessage("    Given the heater is turned on");
			this.logMessage("    And it is suspended");
			bResult = heaterop.suspended();
			if (!bResult) {
				this.logMessage("      but it was not!");
				statistics.failedCondition();;
			}
			this.logMessage("    When I call resume()");
			bResult = heaterop.resume();
			this.logMessage("    Then the method returns true");
			if (!bResult) {
				this.logMessage("      but was: " + bResult);
				statistics.incorrectResult();
			}
			this.logMessage("    And the heater is not suspended");
			bResult = heaterop.suspended();
			if (bResult) {
				this.logMessage("      but it was!");
				statistics.incorrectResult();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		statistics.updateStatistics();
		statistics.statisticsReport(this);

		this.logMessage("Heater tests end.");
	}

	/**
	 * test the {@code Heater} component, in cooperation with the
	 * {@code HeaterTester} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		scheduleTestHeater()
	{
		// Test for the heater
		Instant heaterTestStart =
				this.ac.getStartInstant().plusSeconds(
							(HeaterUnitTester.SWITCH_ON_DELAY +
											HeaterUnitTester.SWITCH_OFF_DELAY)/2);
		this.traceMessage("HEM schedules the heater test.\n");
		long delay = this.ac.nanoDelayUntilInstant(heaterTestStart);

		// schedule the switch on heater in one second
		this.scheduleTaskOnComponent(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							testHeater();
						} catch (Throwable e) {
							throw new BCMRuntimeException(e) ;
						}
					}
				}, delay, TimeUnit.NANOSECONDS);
	}
}
// -----------------------------------------------------------------------------
