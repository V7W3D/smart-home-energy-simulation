package fr.sorbonne_u.components.hem2025e3.equipments.refrigerator;

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
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.annotations.LocalArchitecture;
import fr.sorbonne_u.components.cyphy.annotations.SIL_Simulation_Architectures;
import fr.sorbonne_u.components.cyphy.interfaces.ModelStateAccessI.VariableValue;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.Refrigerator;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.connections.RefrigeratorInboundPort;
import fr.sorbonne_u.components.hem2025e2.equipments.refrigerator.mil.RefrigeratorElectricityModel;
import fr.sorbonne_u.components.hem2025e2.equipments.refrigerator.mil.events.CompressorActive;
import fr.sorbonne_u.components.hem2025e2.equipments.refrigerator.mil.events.CompressorInactive;
import fr.sorbonne_u.components.hem2025e3.equipments.refrigerator.sil.Local_SIL_SimulationArchitectures;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

// -----------------------------------------------------------------------------
/**
 * The class <code>RefrigeratorCyPhy</code>.
 *
 * <p>Created on : 2026-01-15</p>
 *
 * @author	Softweavers
 */
@SIL_Simulation_Architectures({
	@LocalArchitecture(
		uri = "silUnitTests",
		rootModelURI = "RefrigeratorElectricityModel",
		simulatedTimeUnit = TimeUnit.HOURS,
		externalEvents = @ModelExternalEvents()
		),
	@LocalArchitecture(
		uri = "silIntegrationTests",
		rootModelURI = "RefrigeratorElectricityModel",
		simulatedTimeUnit = TimeUnit.HOURS,
		externalEvents =
			@ModelExternalEvents(
				exported = {CompressorActive.class,
							CompressorInactive.class}
				)
		)
	})
@OfferedInterfaces(offered={RefrigeratorUserCI.class})
public class			RefrigeratorCyPhy
extends		AbstractCyPhyComponent
implements	RefrigeratorImplementationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static final String		REFLECTION_INBOUND_PORT_URI =
			"REFRIGERATOR-RIP-URI";
	public static final String		INBOUND_PORT_URI =
				Refrigerator.INBOUND_PORT_URI;

	public static final String		UNIT_TEST_ARCHITECTURE_URI =
			"silUnitTests";
	public static final String		INTEGRATION_TEST_ARCHITECTURE_URI =
			"silIntegrationTests";

	public static final String		DEFAULT_APPLIANCE_ID =
				Refrigerator.APPLIANCE_ID;
	public static final String		DEFAULT_XML_DESCRIPTOR_PATH =
				Refrigerator.XML_DESCRIPTOR_PATH;
	public static final String		HEM_REGISTRATION_INBOUND_PORT_URI =
			"hem-registration";
	public static final long		HEM_REGISTRATION_RETRY_MS = 1000L;

	public static boolean			VERBOSE = false;
	public static boolean			DEBUG = false;
	public static int				X_RELATIVE_POSITION = 0;
	public static int				Y_RELATIVE_POSITION = 0;

	protected static int				NUMBER_OF_STANDARD_THREADS = 2;
	protected static int				NUMBER_OF_SCHEDULABLE_THREADS = 1;

	protected static final double	CONTROL_PERIOD_SECONDS = 10.0;
	protected static final double	THERMOSTAT_HYSTERESIS = 1.0;

	protected RefrigeratorState		currentState;
	protected double				currentTemperature;
	protected double				targetTemperature;
	protected boolean				compressorActive;
	protected boolean				hemSuspended;
	protected boolean				controlLoopScheduled;
	protected long					actualControlPeriodMs;

	protected RefrigeratorInboundPort		rip;
	protected String					applianceId;
	protected String					xmlDescriptorPath;
	protected String					refrigeratorInboundPortURI;
	protected HEMRegistrationOutboundPort	hemRegistrationPort;
	protected boolean					hemRegistered;
	protected boolean					hemRegistrationScheduled;
	protected boolean					disableHemRegistration;

	protected AtomicSimulatorPlugin		asp;
	protected String					localArchitectureURI;
	protected String					customSimulationRootModelURI;
	protected double					accelerationFactor;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			RefrigeratorCyPhy() throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, INBOUND_PORT_URI);
	}

	protected			RefrigeratorCyPhy(
		String reflectionInboundPortURI,
		String refrigeratorInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI,
				NUMBER_OF_STANDARD_THREADS,
				NUMBER_OF_SCHEDULABLE_THREADS,
				ExecutionMode.STANDARD,
				"fake-clock",
				null);

		this.localArchitectureURI = null;
		this.customSimulationRootModelURI = null;
		this.accelerationFactor = 0.0;
		this.applianceId = DEFAULT_APPLIANCE_ID;
		this.xmlDescriptorPath = DEFAULT_XML_DESCRIPTOR_PATH;
		this.hemRegistered = false;
		this.hemRegistrationScheduled = false;
		this.disableHemRegistration = false;
		this.initialise(refrigeratorInboundPortURI);
	}

	protected			RefrigeratorCyPhy(
		String reflectionInboundPortURI,
		String refrigeratorInboundPortURI,
		ExecutionMode executionMode
		) throws Exception
	{
		super(reflectionInboundPortURI,
				NUMBER_OF_STANDARD_THREADS,
				NUMBER_OF_SCHEDULABLE_THREADS,
				executionMode,
				"fake-clock",
				null);

		assert	executionMode != null && executionMode.isTestWithoutSimulation() :
				new PreconditionException(
						"executionMode != null && "
						+ "executionMode.isTestWithoutSimulation()");

		this.localArchitectureURI = null;
		this.customSimulationRootModelURI = null;
		this.accelerationFactor = 0.0;
		this.applianceId = DEFAULT_APPLIANCE_ID;
		this.xmlDescriptorPath = DEFAULT_XML_DESCRIPTOR_PATH;
		this.hemRegistered = false;
		this.hemRegistrationScheduled = false;
		this.disableHemRegistration = false;
		this.initialise(refrigeratorInboundPortURI);
	}

	protected			RefrigeratorCyPhy(
		String reflectionInboundPortURI,
		String refrigeratorInboundPortURI,
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
				((Supplier<Set<String>>)() -> {
					HashSet<String> hs = new HashSet<>();
					hs.add(UNIT_TEST_ARCHITECTURE_URI);
					hs.add(INTEGRATION_TEST_ARCHITECTURE_URI);
					return hs;
				}).get(),
				accelerationFactor);

		this.localArchitectureURI = localArchitectureURI;
		this.customSimulationRootModelURI = null;
		this.accelerationFactor = accelerationFactor;
		this.applianceId = DEFAULT_APPLIANCE_ID;
		this.xmlDescriptorPath = DEFAULT_XML_DESCRIPTOR_PATH;
		this.hemRegistered = false;
		this.hemRegistrationScheduled = false;
		this.disableHemRegistration = false;
		this.initialise(refrigeratorInboundPortURI);
	}

	protected			RefrigeratorCyPhy(
		String reflectionInboundPortURI,
		String refrigeratorInboundPortURI,
		ExecutionMode executionMode,
		TestScenario testScenario,
		String localArchitectureURI,
		double accelerationFactor,
		boolean disableHemRegistration
		) throws Exception
	{
		this(reflectionInboundPortURI,
			refrigeratorInboundPortURI,
			executionMode,
			testScenario,
			localArchitectureURI,
			accelerationFactor);
		this.disableHemRegistration = disableHemRegistration;
	}

	// -------------------------------------------------------------------------
	// Initialisation methods
	// -------------------------------------------------------------------------

	protected void		initialise(String refrigeratorInboundPortURI)
	throws Exception
	{
		assert	refrigeratorInboundPortURI != null :
				new PreconditionException("refrigeratorInboundPortURI != null");
		assert	!refrigeratorInboundPortURI.isEmpty() :
				new PreconditionException(
						"!refrigeratorInboundPortURI.isEmpty()");
		assert	this.applianceId != null && !this.applianceId.isEmpty() :
				new PreconditionException("applianceId != null && !applianceId.isEmpty()");
		assert	this.xmlDescriptorPath != null && !this.xmlDescriptorPath.isEmpty() :
				new PreconditionException("xmlDescriptorPath != null && !xmlDescriptorPath.isEmpty()");

		this.currentState = RefrigeratorState.OFF;
		this.currentTemperature = 5.0;
		this.targetTemperature = DEFAULT_TARGET_TEMPERATURE;
		this.compressorActive = false;
		this.hemSuspended = false;
		this.controlLoopScheduled = false;
		this.actualControlPeriodMs = Math.max(1L,
				(long)(CONTROL_PERIOD_SECONDS * 1000.0));

		this.refrigeratorInboundPortURI = refrigeratorInboundPortURI;
		this.rip = new RefrigeratorInboundPort(refrigeratorInboundPortURI, this);
		this.rip.publishPort();

		if (VERBOSE || DEBUG) {
			this.tracer.get().setTitle("Refrigerator component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		assert	RefrigeratorImplementationI.staticInvariants();
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

		if (architectureURI.equals(UNIT_TEST_ARCHITECTURE_URI)) {
			return Local_SIL_SimulationArchitectures.
					createRefrigeratorSIL_Architecture4UnitTest(
							architectureURI,
							rootModelURI,
							simulatedTimeUnit,
							accelerationFactor);
		} else if (architectureURI.equals(INTEGRATION_TEST_ARCHITECTURE_URI)) {
			return Local_SIL_SimulationArchitectures.
					createRefrigeratorSIL_Architecture4IntegrationTest(
							architectureURI,
							rootModelURI,
							simulatedTimeUnit,
							accelerationFactor);
		}

		throw new BCMException("Unknown local simulation architecture URI: "
									+ architectureURI);
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			ExecutionMode mode = this.getExecutionMode();
			if (!this.disableHemRegistration
					&& (mode == ExecutionMode.INTEGRATION_TEST
					|| mode == ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION
					|| mode == ExecutionMode.UNIT_TEST
					|| mode == ExecutionMode.UNIT_TEST_WITH_SIL_SIMULATION)) {
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
						assert	modelURI.equals(RefrigeratorElectricityModel.URI);
						assert	name.equals("currentTemperature");

						return ((RefrigeratorElectricityModel)
								this.atomicSimulators.get(modelURI).
										getSimulatedModel()).
										getCurrentTemperature();
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

		assert	RefrigeratorImplementationI.staticInvariants();
	}

	protected boolean	connectToHemRegistration() throws Exception
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
				// ignore and retry
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
					"Refrigerator HEM registration connection failed: "
							+ lastException.getMessage() + "\n");
		}
		return false;
	}

	protected void		scheduleHemRegistrationRetry(long delayMs)
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
						hemRegistrationScheduled = false;
						if (connectToHemRegistration()) {
							hemRegistrationPort.register(
									applianceId,
									refrigeratorInboundPortURI,
									xmlDescriptorPath);
							hemRegistered = true;
							if (VERBOSE) {
								traceMessage("Refrigerator registered with HEM.\n");
							}
						} else {
							scheduleHemRegistrationRetry(HEM_REGISTRATION_RETRY_MS);
						}
					} catch (Exception e) {
						scheduleHemRegistrationRetry(HEM_REGISTRATION_RETRY_MS);
					}
				}
			},
			delayMs,
			TimeUnit.MILLISECONDS);
	}

	@Override
	public synchronized void	execute() throws Exception
	{
		if (VERBOSE) {
			this.traceMessage("Refrigerator CyPhy executes.\n");
		}

		switch (this.getExecutionMode()) {
		case STANDARD:
			break;
		case UNIT_TEST:
		case INTEGRATION_TEST:
			this.initialiseClock(
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					this.clockURI);
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
			break;
		case INTEGRATION_TEST_WITH_SIL_SIMULATION:
			this.initialiseClock4Simulation(
					ClocksServerWithSimulation.STANDARD_INBOUNDPORT_URI,
					this.clockURI);
			break;
		default:
		}

		if (this.getExecutionMode().isSimulationTest()) {
			long periodMs = (long)((CONTROL_PERIOD_SECONDS * 1000.0)
							/ this.accelerationFactor);
			this.actualControlPeriodMs = Math.max(1L, periodMs);
		}
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		if (this.hemRegistrationPort != null
					&& this.hemRegistrationPort.connected()) {
			this.doPortDisconnection(this.hemRegistrationPort.getPortURI());
		}
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			if (this.hemRegistrationPort != null) {
				this.hemRegistrationPort.unpublishPort();
			}
			this.rip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Internal control
	// -------------------------------------------------------------------------

	protected void		scheduleThermostatControlLoop(long delayMs)
	{
		if (this.controlLoopScheduled) {
			return;
		}
		this.controlLoopScheduled = true;
		this.scheduleTaskOnComponent(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						controlLoopScheduled = false;
						performThermostatControl();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			},
			delayMs,
			TimeUnit.MILLISECONDS);
	}

	protected void		performThermostatControl() throws Exception
	{
		if (this.currentState == RefrigeratorState.OFF) {
			return;
		}

		double temp = this.getCurrentTemperature();
		if (!this.hemSuspended) {
			boolean shouldRun = this.compressorActive;
			if (temp > this.targetTemperature + THERMOSTAT_HYSTERESIS) {
				shouldRun = true;
			} else if (temp < this.targetTemperature - THERMOSTAT_HYSTERESIS) {
				shouldRun = false;
			}
			if (shouldRun != this.compressorActive) {
				setCompressorActive(shouldRun);
			}
		}

		this.scheduleThermostatControlLoop(this.actualControlPeriodMs);
	}

	protected void		setCompressorActive(boolean active) throws Exception
	{
		this.compressorActive = active;
		if (this.getExecutionMode().isSILTest()) {
			if (active) {
				((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
						RefrigeratorElectricityModel.URI,
						t -> new CompressorActive(t));
			} else {
				((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
						RefrigeratorElectricityModel.URI,
						t -> new CompressorInactive(t));
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected VariableValue<Double>	computeCurrentTemperature() throws Exception
	{
		return (VariableValue<Double>)(VariableValue<?>)
				this.asp.getModelVariableValue(
						RefrigeratorElectricityModel.URI,
						"currentTemperature");
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	@Override
	public RefrigeratorState	getState() throws Exception
	{
		if (VERBOSE) {
			this.traceMessage("Refrigerator returns its state: "
									+ this.currentState + ".\n");
		}
		return this.currentState;
	}

	@Override
	public void			turnOn() throws Exception
	{
		if (VERBOSE) {
			this.traceMessage("Refrigerator is turned on.\n");
		}
		assert	this.currentState == RefrigeratorState.OFF :
				new PreconditionException("getState() == RefrigeratorState.OFF");

		this.currentState = RefrigeratorState.ON;
		this.hemSuspended = false;
		this.setCompressorActive(true);
		this.scheduleThermostatControlLoop(0L);

		assert	this.currentState == RefrigeratorState.ON :
				new PostconditionException("getState() == RefrigeratorState.ON");
	}

	@Override
	public void			turnOff() throws Exception
	{
		if (VERBOSE) {
			this.traceMessage("Refrigerator is turned off.\n");
		}
		assert	this.currentState != RefrigeratorState.OFF :
				new PreconditionException("getState() != RefrigeratorState.OFF");

		this.currentState = RefrigeratorState.OFF;
		this.hemSuspended = false;
		this.setCompressorActive(false);

		assert	this.currentState == RefrigeratorState.OFF :
				new PostconditionException("getState() == RefrigeratorState.OFF");
	}

	@Override
	public double		getCurrentTemperature() throws Exception
	{
		if (this.getExecutionMode().isSILTest()) {
			VariableValue<Double> vv = this.computeCurrentTemperature();
			this.currentTemperature = vv.getValue();
		}
		if (VERBOSE) {
			this.traceMessage("Refrigerator returns current temperature: "
									+ this.currentTemperature + " C.\n");
		}
		return this.currentTemperature;
	}

	@Override
	public double		getTargetTemperature() throws Exception
	{
		if (VERBOSE) {
			this.traceMessage("Refrigerator returns target temperature: "
									+ this.targetTemperature + " C.\n");
		}
		return this.targetTemperature;
	}

	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		assert	target >= MIN_TEMPERATURE && target <= MAX_TEMPERATURE :
				new PreconditionException(
						"target >= MIN_TEMPERATURE && target <= MAX_TEMPERATURE");
		if (VERBOSE) {
			this.traceMessage("Refrigerator sets target temperature to: "
									+ target + " C.\n");
		}
		this.targetTemperature = target;

		assert	this.targetTemperature == target :
				new PostconditionException("getTargetTemperature() == target");
	}

	@Override
	public void			suspendCompressor() throws Exception
	{
		if (VERBOSE) {
			this.traceMessage("Refrigerator compressor is suspended.\n");
		}
		assert	this.currentState == RefrigeratorState.ON :
				new PreconditionException("getState() == RefrigeratorState.ON");

		this.currentState = RefrigeratorState.SUSPENDED;
		this.hemSuspended = true;
		this.setCompressorActive(false);

		assert	this.currentState == RefrigeratorState.SUSPENDED :
				new PostconditionException("getState() == RefrigeratorState.SUSPENDED");
	}

	@Override
	public void			resumeCompressor() throws Exception
	{
		if (VERBOSE) {
			this.traceMessage("Refrigerator compressor is resumed.\n");
		}
		assert	this.currentState == RefrigeratorState.SUSPENDED :
				new PreconditionException(
						"getState() == RefrigeratorState.SUSPENDED");

		this.currentState = RefrigeratorState.ON;
		this.hemSuspended = false;
		this.setCompressorActive(true);
		this.scheduleThermostatControlLoop(0L);

		assert	this.currentState == RefrigeratorState.ON :
				new PostconditionException("getState() == RefrigeratorState.ON");
	}

	@Override
	public boolean		isCompressorSuspended() throws Exception
	{
		return this.currentState == RefrigeratorState.SUSPENDED;
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		double power;
		switch (this.currentState) {
		case OFF:
			power = OFF_POWER;
			break;
		case SUSPENDED:
			power = STANDBY_POWER;
			break;
		case ON:
			power = this.compressorActive ? COMPRESSOR_POWER : STANDBY_POWER;
			break;
		default:
			power = 0.0;
		}
		if (VERBOSE) {
			this.traceMessage("Refrigerator returns its current power level: "
									+ power + " W.\n");
		}
		return power;
	}
}
