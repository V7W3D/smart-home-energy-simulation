package fr.sorbonne_u.components.hem2025e3.equipments.fan;

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

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.annotations.LocalArchitecture;
import fr.sorbonne_u.components.cyphy.annotations.SIL_Simulation_Architectures;
import fr.sorbonne_u.components.cyphy.interfaces.CyPhyReflectionCI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.Fan;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanInboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationOutboundPort;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.FanEventInfo;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetMediumSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sil.FanElectricitySILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sil.FanStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sil.Local_SIL_SimulationArchitectures;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.components.cyphy.interfaces.ModelStateAccessI.VariableValue;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanCyPhy</code> implements the cyber-physical
 * component version of the fan.
 *
 * <p>Created on : 2026-02-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@SIL_Simulation_Architectures({
	@LocalArchitecture(
		uri = "silUnitTests",
		rootModelURI = "FanCoupledModel",
		simulatedTimeUnit = TimeUnit.HOURS,
		externalEvents = @ModelExternalEvents()
		),
	@LocalArchitecture(
		uri = "silIntegrationTests",
		rootModelURI = "FanStateSILModel",
		simulatedTimeUnit = TimeUnit.HOURS,
		externalEvents =
			@ModelExternalEvents(
				imported = {},
				exported = {SwitchOnFan.class,
							SwitchOffFan.class,
							SetLowSpeed.class,
							SetMediumSpeed.class,
							SetHighSpeed.class}
				)
		)
	})
//-----------------------------------------------------------------------------
@OfferedInterfaces(offered = {FanUserCI.class})
//-----------------------------------------------------------------------------
public class			FanCyPhy
extends		AbstractCyPhyComponent
implements	FanImplementationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** standard URI of the fan reflection inbound port. */
	public static final String		REFLECTION_INBOUND_PORT_URI =
										"FAN-RIP-URI";
	/** URI of the fan inbound port used in tests. */
	public static final String		INBOUND_PORT_URI =
										Fan.INBOUND_PORT_URI;
	/** URI of the local simulation architecture for SIL unit tests. */
	public static final String		UNIT_TEST_ARCHITECTURE_URI =
										"silUnitTests";
	/** URI of the local simulation architecture for SIL integration tests. */
	public static final String		INTEGRATION_TEST_ARCHITECTURE_URI =
										"silIntegrationTests";

	/** default appliance identifier for HEM registration. */
	public static final String		DEFAULT_APPLIANCE_ID = "Fan";
	/** default XML descriptor path for HEM registration. */
	public static final String		DEFAULT_XML_DESCRIPTOR_PATH =
									"hem-adapter/fan-config.xml";
	/** HEM registration inbound port URI. */
	public static final String		HEM_REGISTRATION_INBOUND_PORT_URI =
									"hem-registration";
	/** delay between HEM registration retries (ms). */
	public static final long		HEM_REGISTRATION_RETRY_MS = 1000L;

	// Configuration

	public static final MeasurementUnit	POWER_UNIT = MeasurementUnit.WATTS;
	public static final MeasurementUnit	TENSION_UNIT = MeasurementUnit.VOLTS;

	/** power consumption when in LOW mode. */
	public static final Measure<Double>	LOW_POWER =
										new Measure<>(
												FanImplementationI.LOW_POWER,
												POWER_UNIT);
	/** power consumption when in MEDIUM mode. */
	public static final Measure<Double>	MEDIUM_POWER =
										new Measure<>(
												FanImplementationI.MEDIUM_POWER,
												POWER_UNIT);
	/** power consumption when in HIGH mode. */
	public static final Measure<Double>	HIGH_POWER =
										new Measure<>(
												FanImplementationI.HIGH_POWER,
												POWER_UNIT);
	/** tension required by the fan. */
	public static final Measure<Double>	TENSION =
										new Measure<>(220.0, TENSION_UNIT);

	// Internal component state variables

	/** initial state of the fan. */
	public static final FanImplementationI.FanState		INITIAL_STATE =
											FanImplementationI.FanState.OFF;

	/** current state (off, low, medium, high) of the fan. */
	protected FanImplementationI.FanState				currentState;

	/** inbound port offering the <code>FanUserCI</code> interface. */
	protected FanInboundPort			fip;
	/** stored fan inbound port URI for registration. */
	protected String					fanInboundPortURI;
	/** appliance id used for HEM registration. */
	protected String					applianceId;
	/** XML descriptor path for dynamic connector generation. */
	protected String					xmlDescriptorPath;
	/** outbound port to HEM registration service. */
	protected HEMRegistrationOutboundPort	hemRegistrationPort;
	/** true once HEM registration succeeds. */
	protected boolean				hemRegistered;
	/** true once a retry task has been scheduled. */
	protected boolean				hemRegistrationScheduled;

	// Execution/Simulation

	/** when true, methods trace their actions. */
	public static boolean				VERBOSE = false;
	/** when true, methods provides debugging traces of their actions. */
	public static boolean				DEBUG = false;
	/** when tracing, x coordinate of the window relative position. */
	public static int					X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position. */
	public static int					Y_RELATIVE_POSITION = 0;

	/** one thread for the method execute and one to answer component calls. */
	protected static int					NUMBER_OF_STANDARD_THREADS = 2;
	/** schedulable thread used for registration retries. */
	protected static int					NUMBER_OF_SCHEDULABLE_THREADS = 1;

	/** plug-in holding the local simulation architecture and simulators. */
	protected AtomicSimulatorPlugin		asp;
	/** URI of the local simulation architecture used to compose the global
	 *  simulation architecture or the empty string if no simulation. */
	protected final String				localArchitectureURI;
	/** root model URI to use when creating a custom simulation architecture. */
	protected final String				customSimulationRootModelURI;
	/** acceleration factor to be used when running the real time simulation. */
	protected final double				accelerationFactor;
	/** cached state model URI for SIL simulations. */
	protected final String				stateModelURI;
	/** optional tracer title for multi-instance deployments. */
	protected String				tracerTitle;
	/** optional tracer x position for multi-instance deployments. */
	protected int				tracerX;
	/** optional tracer y position for multi-instance deployments. */
	protected int				tracerY;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				REFLECTION_INBOUND_PORT_URI != null &&
												!REFLECTION_INBOUND_PORT_URI.isEmpty(),
				FanCyPhy.class,
				"REFLECTION_INBOUND_PORT_URI != null && "
				+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty(),
				FanCyPhy.class,
				"INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				UNIT_TEST_ARCHITECTURE_URI != null &&
												!UNIT_TEST_ARCHITECTURE_URI.isEmpty(),
				FanCyPhy.class,
				"UNIT_TEST_ARCHITECTURE_URI != null && "
				+ "!UNIT_TEST_ARCHITECTURE_URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				INTEGRATION_TEST_ARCHITECTURE_URI != null &&
												!INTEGRATION_TEST_ARCHITECTURE_URI.isEmpty(),
				FanCyPhy.class,
				"INTEGRATION_TEST_ARCHITECTURE_URI != null && "
				+ "!INTEGRATION_TEST_ARCHITECTURE_URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				LOW_POWER != null && LOW_POWER.getData() > 0.0 &&
												LOW_POWER.getMeasurementUnit().equals(POWER_UNIT),
				FanCyPhy.class,
				"LOW_POWER != null && LOW_POWER.getData() > 0.0 && "
				+ "LOW_POWER.getMeasurementUnit().equals(POWER_UNIT)");
		ret &= AssertionChecking.checkStaticInvariant(
				MEDIUM_POWER != null && MEDIUM_POWER.getData() > 0.0 &&
												MEDIUM_POWER.getMeasurementUnit().equals(POWER_UNIT),
				FanCyPhy.class,
				"MEDIUM_POWER != null && MEDIUM_POWER.getData() > 0.0 && "
				+ "MEDIUM_POWER.getMeasurementUnit().equals(POWER_UNIT)");
		ret &= AssertionChecking.checkStaticInvariant(
				HIGH_POWER != null && HIGH_POWER.getData() > 0.0 &&
												HIGH_POWER.getMeasurementUnit().equals(POWER_UNIT),
				FanCyPhy.class,
				"HIGH_POWER != null && HIGH_POWER.getData() > 0.0 && "
				+ "HIGH_POWER.getMeasurementUnit().equals(POWER_UNIT)");
		ret &= AssertionChecking.checkStaticInvariant(
				TENSION != null && TENSION.getData() == 220.0 &&
												TENSION.getMeasurementUnit().equals(TENSION_UNIT),
				FanCyPhy.class,
				"TENSION != null && TENSION.getData() == 220.0 && "
				+ "TENSION.getMeasurementUnit().equals(TENSION_UNIT)");
		ret &= AssertionChecking.checkStaticInvariant(
				INITIAL_STATE != null,
				FanCyPhy.class,
				"INITIAL_STATE != null");
		ret &= AssertionChecking.checkStaticInvariant(
				X_RELATIVE_POSITION >= 0,
				FanCyPhy.class,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkStaticInvariant(
				Y_RELATIVE_POSITION >= 0,
				FanCyPhy.class,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	protected static boolean	implementationInvariants(FanCyPhy f)
	{
		assert	f != null : new PreconditionException("f != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				f.currentState != null,
				FanCyPhy.class,
				f,
				"currentState != null");
		ret &= staticInvariants();
		return ret;
	}

	protected static boolean	invariants(FanCyPhy f)
	{
		assert	f != null : new PreconditionException("f != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a fan component for standard execution.
	 * 
	 * @throws Exception <i>to do</i>.
	 */
	protected			FanCyPhy() throws Exception
	{
		this(INBOUND_PORT_URI);
	}

	/**
	 * create a fan component for standard execution with the given inbound
	 * port URI.
	 *
	 * @param fanInboundPortURI	URI of the fan inbound port.
	 * @throws Exception			<i>to do</i>.
	 */
	protected			FanCyPhy(String fanInboundPortURI) throws Exception
	{
		this(AbstractPort.generatePortURI(CyPhyReflectionCI.class),
			 fanInboundPortURI);
	}

	/**
	 * create a fan component for standard execution with given reflection and
	 * inbound port URIs.
	 *
	 * @param reflectionInboundPortURI	URI of reflection inbound port.
	 * @param fanInboundPortURI		URI of fan inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			FanCyPhy(
		String reflectionInboundPortURI,
		String fanInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI,
			  NUMBER_OF_STANDARD_THREADS,
			  NUMBER_OF_SCHEDULABLE_THREADS);

		this.localArchitectureURI = null;
		this.customSimulationRootModelURI = null;
		this.accelerationFactor = 0.0;
		this.stateModelURI = FanStateSILModel.URI;
		this.tracerTitle = null;
		this.tracerX = X_RELATIVE_POSITION;
		this.tracerY = Y_RELATIVE_POSITION;
		this.applianceId = DEFAULT_APPLIANCE_ID;
		this.xmlDescriptorPath = DEFAULT_XML_DESCRIPTOR_PATH;
		this.hemRegistered = false;
		this.hemRegistrationScheduled = false;

		this.initialise(fanInboundPortURI);

		assert	FanCyPhy.implementationInvariants(this) :
				new ImplementationInvariantException(
						"FanCyPhy.implementationInvariants(this)");
		assert	FanCyPhy.invariants(this) :
				new InvariantException("FanCyPhy.invariants(this)");
	}

	/**
	 * create a fan component for test executions without simulation.
	 * 
	 * @param executionMode	execution mode for the next run.
	 * @throws Exception		<i>to do</i>.
	 */
	protected			FanCyPhy(ExecutionMode executionMode) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, INBOUND_PORT_URI,
			 AssertionChecking.assertTrueAndReturnOrThrow(
					executionMode != null
											&& executionMode.isTestWithoutSimulation(),
					executionMode,
					() -> new PreconditionException(
								"executionMode != null && "
								+ "executionMode."
								+ "isTestWithoutSimulation()")));
	}

	/**
	 * create a fan component for test executions without simulation with the
	 * given inbound port URI.
	 *
	 * @param fanInboundPortURI	URI of the fan inbound port.
	 * @param executionMode		execution mode for the next run.
	 * @throws Exception			<i>to do</i>.
	 */
	protected			FanCyPhy(
		String fanInboundPortURI,
		ExecutionMode executionMode
		) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, fanInboundPortURI,
			 AssertionChecking.assertTrueAndReturnOrThrow(
					executionMode != null
											&& executionMode.isTestWithoutSimulation(),
					executionMode,
					() -> new PreconditionException(
								"executionMode != null && "
								+ "executionMode."
								+ "isTestWithoutSimulation()")));
	}

	// Standard constructor for tests without simulation
	protected			FanCyPhy(
		String reflectionInboundPortURI,
		String fanInboundPortURI,
		ExecutionMode executionMode
		) throws Exception
	{
		super(reflectionInboundPortURI,
			  NUMBER_OF_STANDARD_THREADS,
			  NUMBER_OF_SCHEDULABLE_THREADS,
			  executionMode,
			  "fake-clock",
			  null);

		assert	executionMode != null &&
								executionMode.isTestWithoutSimulation() :
				new PreconditionException(
						"executionMode != null && executionMode."
						+ "isTestWithoutSimulation()");

		this.localArchitectureURI = null;
		this.customSimulationRootModelURI = null;
		this.accelerationFactor = 0.0;
		this.stateModelURI = FanStateSILModel.URI;
		this.tracerTitle = null;
		this.tracerX = X_RELATIVE_POSITION;
		this.tracerY = Y_RELATIVE_POSITION;
		this.applianceId = DEFAULT_APPLIANCE_ID;
		this.xmlDescriptorPath = DEFAULT_XML_DESCRIPTOR_PATH;
		this.hemRegistered = false;
		this.hemRegistrationScheduled = false;

		this.initialise(fanInboundPortURI);

		assert	FanCyPhy.implementationInvariants(this) :
				new ImplementationInvariantException(
						"FanCyPhy.implementationInvariants(this)");
		assert	FanCyPhy.invariants(this) :
				new InvariantException("FanCyPhy.invariants(this)");
	}

	// Tests with simulation
	protected			FanCyPhy(
		String reflectionInboundPortURI,
		String fanInboundPortURI,
		ExecutionMode executionMode,
		TestScenario testScenario,
		String localArchitectureURI,
		double accelerationFactor
		) throws Exception
	{
		super(reflectionInboundPortURI,
			  NUMBER_OF_STANDARD_THREADS,
			  NUMBER_OF_SCHEDULABLE_THREADS,
			  executionMode,
			  AssertionChecking.assertTrueAndReturnOrThrow(
					  testScenario != null,
					  testScenario.getClockURI(),
					  () -> new PreconditionException("testScenario != null")),
			  testScenario,
			  ((Supplier<Set<String>>)() ->
					  { HashSet<String> hs = new HashSet<>();
						hs.add(UNIT_TEST_ARCHITECTURE_URI);
						hs.add(INTEGRATION_TEST_ARCHITECTURE_URI);
						return hs;
					}).get(),
			  accelerationFactor);

		assert	fanInboundPortURI != null && !fanInboundPortURI.isEmpty() :
				new PreconditionException(
						"fanInboundPortURI != null && "
						+ "!fanInboundPortURI.isEmpty()");

		this.localArchitectureURI = localArchitectureURI;
		this.customSimulationRootModelURI = null;
		this.accelerationFactor = accelerationFactor;
		this.stateModelURI = FanStateSILModel.URI;
		this.tracerTitle = null;
		this.tracerX = X_RELATIVE_POSITION;
		this.tracerY = Y_RELATIVE_POSITION;
		this.applianceId = DEFAULT_APPLIANCE_ID;
		this.xmlDescriptorPath = DEFAULT_XML_DESCRIPTOR_PATH;
		this.hemRegistered = false;
		this.hemRegistrationScheduled = false;

		this.initialise(fanInboundPortURI);

		if (DEBUG) {
			this.logMessage("FanCyPhy local simulation architectures: "
							+ this.localSimulationArchitectures);
		}

		assert	FanCyPhy.implementationInvariants(this) :
				new ImplementationInvariantException(
						"FanCyPhy.implementationInvariants(this)");
		assert	FanCyPhy.invariants(this) :
				new InvariantException("FanCyPhy.invariants(this)");
	}

	// Tests with simulation and custom registration/simulation identifiers
	protected			FanCyPhy(
		String reflectionInboundPortURI,
		String fanInboundPortURI,
		ExecutionMode executionMode,
		TestScenario testScenario,
		String localArchitectureURI,
		double accelerationFactor,
		String applianceId,
		String xmlDescriptorPath,
		String customSimulationRootModelURI
		) throws Exception
	{
		super(reflectionInboundPortURI,
			  NUMBER_OF_STANDARD_THREADS,
			  NUMBER_OF_SCHEDULABLE_THREADS,
			  executionMode,
			  AssertionChecking.assertTrueAndReturnOrThrow(
					  testScenario != null,
					  testScenario.getClockURI(),
					  () -> new PreconditionException("testScenario != null")),
			  testScenario,
			  ((Supplier<Set<String>>)() ->
					  { HashSet<String> hs = new HashSet<>();
						hs.add(UNIT_TEST_ARCHITECTURE_URI);
						hs.add(INTEGRATION_TEST_ARCHITECTURE_URI);
						return hs;
					}).get(),
			  accelerationFactor);

		assert	fanInboundPortURI != null && !fanInboundPortURI.isEmpty() :
				new PreconditionException(
						"fanInboundPortURI != null && "
						+ "!fanInboundPortURI.isEmpty()");
		assert	applianceId != null && !applianceId.isEmpty() :
				new PreconditionException(
						"applianceId != null && !applianceId.isEmpty()");
		assert	xmlDescriptorPath != null && !xmlDescriptorPath.isEmpty() :
				new PreconditionException(
						"xmlDescriptorPath != null && !xmlDescriptorPath.isEmpty()");
		assert	customSimulationRootModelURI != null
					&& !customSimulationRootModelURI.isEmpty() :
				new PreconditionException(
						"customSimulationRootModelURI != null && "
						+ "!customSimulationRootModelURI.isEmpty()");

		this.localArchitectureURI = localArchitectureURI;
		this.customSimulationRootModelURI = customSimulationRootModelURI;
		this.accelerationFactor = accelerationFactor;
		this.stateModelURI = customSimulationRootModelURI;
		this.tracerTitle = null;
		this.tracerX = X_RELATIVE_POSITION;
		this.tracerY = Y_RELATIVE_POSITION;
		this.applianceId = applianceId;
		this.xmlDescriptorPath = xmlDescriptorPath;
		this.hemRegistered = false;
		this.hemRegistrationScheduled = false;

		this.initialise(fanInboundPortURI);

		if (DEBUG) {
			this.logMessage("FanCyPhy local simulation architectures: "
							+ this.localSimulationArchitectures);
		}

		assert	FanCyPhy.implementationInvariants(this) :
				new ImplementationInvariantException(
						"FanCyPhy.implementationInvariants(this)");
		assert	FanCyPhy.invariants(this) :
				new InvariantException("FanCyPhy.invariants(this)");
	}

	// Tests with simulation and custom registration/simulation identifiers
	// plus per-instance tracing parameters
	protected			FanCyPhy(
		String reflectionInboundPortURI,
		String fanInboundPortURI,
		ExecutionMode executionMode,
		TestScenario testScenario,
		String localArchitectureURI,
		double accelerationFactor,
		String applianceId,
		String xmlDescriptorPath,
		String customSimulationRootModelURI,
		String tracerTitle,
		int tracerX,
		int tracerY
		) throws Exception
	{
		this(reflectionInboundPortURI,
				fanInboundPortURI,
				executionMode,
				testScenario,
				localArchitectureURI,
				accelerationFactor,
				applianceId,
				xmlDescriptorPath,
				customSimulationRootModelURI);

		this.tracerTitle = tracerTitle;
		this.tracerX = tracerX;
		this.tracerY = tracerY;
	}

	// -------------------------------------------------------------------------
	// Initialisation methods
	// -------------------------------------------------------------------------

	protected void		initialise(String fanInboundPortURI)
	throws Exception
	{
		assert	fanInboundPortURI != null :
				new PreconditionException("fanInboundPortURI != null");
		assert	!fanInboundPortURI.isEmpty() :
				new PreconditionException("!fanInboundPortURI.isEmpty()");
		assert	this.applianceId != null && !this.applianceId.isEmpty() :
				new PreconditionException("applianceId != null && !applianceId.isEmpty()");
		assert	this.xmlDescriptorPath != null && !this.xmlDescriptorPath.isEmpty() :
				new PreconditionException("xmlDescriptorPath != null && !xmlDescriptorPath.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.fanInboundPortURI = fanInboundPortURI;
		this.fip = new FanInboundPort(fanInboundPortURI, this);
		this.fip.publishPort();

		if (FanCyPhy.VERBOSE || FanCyPhy.DEBUG) {
			String title = this.tracerTitle != null ?
					this.tracerTitle : "Fan component";
			int x = this.tracerTitle != null ? this.tracerX : X_RELATIVE_POSITION;
			int y = this.tracerTitle != null ? this.tracerY : Y_RELATIVE_POSITION;
			this.tracer.get().setTitle(title);
			this.tracer.get().setRelativePosition(x, y);
			this.toggleTracing();
		}

		assert	FanCyPhy.implementationInvariants(this) :
				new ImplementationInvariantException(
						"Fan.implementationInvariants(this)");
		assert	FanCyPhy.invariants(this) :
				new InvariantException("Fan.invariants(this)");
	}

	@Override
	protected RTArchitecture	createLocalSimulationArchitecture(
		String architectureURI,
		String rootModelURI,
		TimeUnit simulatedTimeUnit,
		double accelerationFactor
		) throws Exception
	{
		assert	architectureURI != null && !architectureURI.isEmpty() :
				new PreconditionException(
						"architectureURI != null && !architectureURI.isEmpty()");
		assert	rootModelURI != null && !rootModelURI.isEmpty() :
				new PreconditionException(
						"rootModelURI != null && !rootModelURI.isEmpty()");
		assert	simulatedTimeUnit != null :
				new PreconditionException("simulatedTimeUnit != null");
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");

		RTArchitecture ret = null;
		if (architectureURI.equals(UNIT_TEST_ARCHITECTURE_URI)) {
			ret = Local_SIL_SimulationArchitectures.
						createFanSIL_Architecture4UnitTest(
								architectureURI,
								rootModelURI,
								simulatedTimeUnit,
								accelerationFactor);
		} else if (architectureURI.equals(INTEGRATION_TEST_ARCHITECTURE_URI)) {
			ret = Local_SIL_SimulationArchitectures.
						createFanSIL_Architecture4IntegrationTest(
								architectureURI,
								rootModelURI,
								simulatedTimeUnit,
								accelerationFactor);
		} else {
			throw new BCMException("Unknown local simulation architecture "
								   + "URI: " + architectureURI);
		}

		return ret;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		assert	FanCyPhy.implementationInvariants(this) :
				new ImplementationInvariantException(
						"FanCyPhy.implementationInvariants(this)");
		assert	FanCyPhy.invariants(this) :
				new InvariantException("FanCyPhy.invariants(this)");

		try {
			ExecutionMode mode = this.getExecutionMode();
			if (mode == ExecutionMode.INTEGRATION_TEST ||
					mode == ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION ||
					mode == ExecutionMode.UNIT_TEST ||
					mode == ExecutionMode.UNIT_TEST_WITH_SIL_SIMULATION) {
				this.hemRegistrationPort =
						new HEMRegistrationOutboundPort(this);
				this.hemRegistrationPort.publishPort();
				this.scheduleHemRegistrationRetry(0L);
			}

			switch (this.getExecutionMode()) {
			case STANDARD:
			case UNIT_TEST:
			case INTEGRATION_TEST:
				break;
			case UNIT_TEST_WITH_SIL_SIMULATION:
			case INTEGRATION_TEST_WITH_SIL_SIMULATION:
				RTArchitecture architecture = null;
				if (this.customSimulationRootModelURI != null) {
					architecture = this.createLocalSimulationArchitecture(
							this.localArchitectureURI,
							this.customSimulationRootModelURI,
							TimeUnit.HOURS,
							this.accelerationFactor);
				} else {
					architecture = (RTArchitecture)
							this.localSimulationArchitectures.
										get(this.localArchitectureURI);
				}
				this.asp = new RTAtomicSimulatorPlugin() {
					private static final long serialVersionUID = 1L;
					@SuppressWarnings("unchecked")
					@Override
					public VariableValue<Double>	getModelVariableValue(
						String modelURI,
						String name
						) throws Exception
					{
						assert	modelURI.equals(FanElectricitySILModel.URI);
						assert	name.equals("currentIntensity");

						return ((FanElectricitySILModel)
									this.atomicSimulators.get(modelURI).
											getSimulatedModel()).
															getCurrentIntensity();
					}
				};
				((RTAtomicSimulatorPlugin)this.asp).
								setPluginURI(architecture.getRootModelURI());
				((RTAtomicSimulatorPlugin)this.asp).
								setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				this.asp.createSimulator();
				this.asp.setSimulationRunParameters(
						(TestScenarioWithSimulation) this.testScenario,
						new HashMap<>());
				break;
			case UNIT_TEST_WITH_HIL_SIMULATION:
			case INTEGRATION_TEST_WITH_HIL_SIMULATION:
				throw new BCMException("HIL simulation not implemented yet!");
			default:
			}
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}

		assert	FanCyPhy.implementationInvariants(this) :
				new ImplementationInvariantException(
						"FanCyPhy.implementationInvariants(this)");
		assert	FanCyPhy.invariants(this) :
				new InvariantException("FanCyPhy.invariants(this)");
	}

	protected boolean		connectToHemRegistration() throws Exception
	{
		if (this.hemRegistrationPort.connected()
					&& this.hemRegistrationPort.getConnector() != null) {
			return true;
		}
		if (this.hemRegistrationPort.connected()
					&& this.hemRegistrationPort.getConnector() == null) {
			try {
				this.doPortDisconnection(this.hemRegistrationPort.getPortURI());
			} catch (Exception e) {
				// ignore and retry connection
			}
		}
		final int maxRetries = 50;
		Exception lastException = null;
		for (int i = 0; i < maxRetries; i++) {
			try {
				this.doPortConnection(
						this.hemRegistrationPort.getPortURI(),
						HEM_REGISTRATION_INBOUND_PORT_URI,
						HEMRegistrationConnector.class.getCanonicalName());
				if (this.hemRegistrationPort.getConnector() != null) {
					return true;
				}
			} catch (Exception e) {
				lastException = e;
				TimeUnit.MILLISECONDS.sleep(100L);
			}
		}
		if (lastException != null) {
			this.traceMessage(
					"Fan HEM registration connection failed: "
							+ lastException.getMessage() + "\n");
		}
		return false;
	}

	protected void			scheduleHemRegistrationRetry(long delayMs)
	{
		if (this.hemRegistrationScheduled || this.hemRegistered) {
			return;
		}
		this.hemRegistrationScheduled = true;
		this.scheduleTaskOnComponent(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						if (hemRegistered) {
							return;
						}
						boolean connected = connectToHemRegistration();
						if (connected && hemRegistrationPort.getConnector() != null) {
							hemRegistrationPort.register(
									applianceId,
									fanInboundPortURI,
									xmlDescriptorPath);
							hemRegistered = true;
							hemRegistrationScheduled = false;
							traceMessage("Fan registered with HEM.\n");
							return;
						}
					} catch (Throwable e) {
						// keep retrying
					}
					hemRegistrationScheduled = false;
					scheduleHemRegistrationRetry(HEM_REGISTRATION_RETRY_MS);
				}
			},
			delayMs,
			TimeUnit.MILLISECONDS);
	}

	@Override
	public void			execute() throws Exception
	{
		this.traceMessage("Fan CyPhy executes.\n");
		if (this.hemRegistrationPort != null && !this.hemRegistered) {
			this.scheduleHemRegistrationRetry(0L);
		}

		assert	FanCyPhy.implementationInvariants(this) :
				new ImplementationInvariantException(
						"FanCyPhy.implementationInvariants(this)");
		assert	FanCyPhy.invariants(this) :
				new InvariantException("FanCyPhy.invariants(this)");

		switch (this.getExecutionMode()) {
		case UNIT_TEST:
		case INTEGRATION_TEST:
			break;
		case UNIT_TEST_WITH_SIL_SIMULATION:
			this.initialiseClock4Simulation(
					ClocksServerWithSimulation.STANDARD_INBOUNDPORT_URI,
					this.clockURI);
			this.asp.initialiseSimulation(
					this.getClock4Simulation().getSimulatedStartTime(),
					this.getClock4Simulation().getSimulatedDuration());
			this.asp.startRTSimulation(
					TimeUnit.NANOSECONDS.toMillis(
							this.getClock4Simulation().getStartEpochNanos()),
					this.getClock4Simulation().getSimulatedStartTime().
										getSimulatedTime(),
					this.getClock4Simulation().getSimulatedDuration().
										getSimulatedDuration());
			this.getClock4Simulation().waitUntilEnd();
			Thread.sleep(200L);
			this.logMessage(this.asp.getFinalReport().toString());
			break;
		case INTEGRATION_TEST_WITH_SIL_SIMULATION:
				this.initialiseClock4Simulation(
						ClocksServerWithSimulation.STANDARD_INBOUNDPORT_URI,
						this.clockURI);
				break;
		case UNIT_TEST_WITH_HIL_SIMULATION:
		case INTEGRATION_TEST_WITH_HIL_SIMULATION:
			throw new BCMException("HIL simulation not implemented yet!");
		case STANDARD:
		default:
		}
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			if (ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION.equals(
											this.getExecutionMode()) && this.asp != null) {
				SimulationReportI report = this.asp.getFinalReport();
				if (report instanceof GlobalReportI) {
					String reportText = ((GlobalReportI) report).printout("");
					this.logMessage(reportText);
				} else if (report != null) {
					String reportText = report.toString();
					this.logMessage(reportText);
				}
			}
			if (this.hemRegistrationPort != null) {
				this.doPortDisconnection(this.hemRegistrationPort.getPortURI());
				this.hemRegistrationPort.unpublishPort();
			}
			this.fip.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	@Override
	public FanImplementationI.FanState		getState() throws Exception
	{
		if (FanCyPhy.VERBOSE) {
			this.traceMessage("Fan returns its state : " +
													this.currentState + ".\n");
		}

		return this.currentState;
	}

	@Override
	public void			turnOn() throws Exception
	{
		if (FanCyPhy.VERBOSE) {
			this.traceMessage("Fan is turned on.\n");
		}

		assert	this.getState() == FanImplementationI.FanState.OFF :
				new PreconditionException("getState() == FanState.OFF");

		this.currentState = FanImplementationI.FanState.LOW;

		assert	this.getState() == FanImplementationI.FanState.LOW :
				new PostconditionException("getState() == FanState.LOW");

		if (this.getExecutionMode().isSILTest()) {
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					this.stateModelURI,
					t -> new SwitchOnFan(t, new FanEventInfo(this.applianceId)));
		}
	}

	@Override
	public void			turnOff() throws Exception
	{
		if (FanCyPhy.VERBOSE) {
			this.traceMessage("Fan is turned off.\n");
		}

		assert	this.getState() != FanImplementationI.FanState.OFF :
				new PreconditionException("getState() != FanState.OFF");

		this.currentState = FanImplementationI.FanState.OFF;

		assert	this.getState() == FanImplementationI.FanState.OFF :
				new PostconditionException("getState() == FanState.OFF");

		if (this.getExecutionMode().isSILTest()) {
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					this.stateModelURI,
					t -> new SwitchOffFan(t, new FanEventInfo(this.applianceId)));
		}
	}

	@Override
	public void			setLow() throws Exception
	{
		if (FanCyPhy.VERBOSE) {
			this.traceMessage("Fan is set low.\n");
		}

		assert	this.getState() != FanImplementationI.FanState.OFF :
				new PreconditionException("getState() != FanState.OFF");

		this.currentState = FanImplementationI.FanState.LOW;

		assert	this.getState() == FanImplementationI.FanState.LOW :
				new PostconditionException("getState() == FanState.LOW");

		if (this.getExecutionMode().isSILTest()) {
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					this.stateModelURI,
					t -> new SetLowSpeed(t, new FanEventInfo(this.applianceId)));
		}
	}

	@Override
	public void			setMedium() throws Exception
	{
		if (FanCyPhy.VERBOSE) {
			this.traceMessage("Fan is set medium.\n");
		}

		assert	this.getState() != FanImplementationI.FanState.OFF :
				new PreconditionException("getState() != FanState.OFF");

		this.currentState = FanImplementationI.FanState.MEDIUM;

		assert	this.getState() == FanImplementationI.FanState.MEDIUM :
				new PostconditionException("getState() == FanState.MEDIUM");

		if (this.getExecutionMode().isSILTest()) {
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					this.stateModelURI,
					t -> new SetMediumSpeed(t, new FanEventInfo(this.applianceId)));
		}
	}

	@Override
	public void			setHigh() throws Exception
	{
		if (FanCyPhy.VERBOSE) {
			this.traceMessage("Fan is set high.\n");
		}

		assert	this.getState() != FanImplementationI.FanState.OFF :
				new PreconditionException("getState() != FanState.OFF");

		this.currentState = FanImplementationI.FanState.HIGH;

		assert	this.getState() == FanImplementationI.FanState.HIGH :
				new PostconditionException("getState() == FanState.HIGH");

		if (this.getExecutionMode().isSILTest()) {
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					this.stateModelURI,
					t -> new SetHighSpeed(t, new FanEventInfo(this.applianceId)));
		}
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		double power;
		if (this.getExecutionMode().isSILTest() &&
					this.localArchitectureURI != null &&
					this.localArchitectureURI.equals(UNIT_TEST_ARCHITECTURE_URI)) {
			VariableValue<Double> v = this.computeCurrentIntensity();
			power = FanCyPhy.TENSION.getData() * v.getValue();
			if (FanCyPhy.VERBOSE) {
				this.traceMessage("Fan returns its current power "
										+ power + " W.\n");
			}
		} else {
			switch (this.currentState) {
				case LOW:
					power = FanImplementationI.LOW_POWER; break;
				case MEDIUM:
					power = FanImplementationI.MEDIUM_POWER; break;
				case HIGH:
					power = FanImplementationI.HIGH_POWER; break;
				case OFF:
				default:
					power = FanImplementationI.OFF_POWER;
			}
		}
		return power;
	}

	protected VariableValue<Double>	computeCurrentIntensity() throws Exception
	{
		return this.asp.getModelVariableValue(
							FanElectricitySILModel.URI,
							"currentIntensity");
	}
}
// -----------------------------------------------------------------------------
