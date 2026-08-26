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

import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI.FanState;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanOutboundPort;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.components.utils.tests.TestsStatistics;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanTesterCyPhy</code>.
 *
 * <p>Created on : 2026-01-10</p>
 *
 * @author	Softweavers
 */
@RequiredInterfaces(required = {FanUserCI.class})
public class			FanTesterCyPhy
extends		AbstractCyPhyComponent
{
	public static boolean		VERBOSE = false;
	public static int			X_RELATIVE_POSITION = 0;
	public static int			Y_RELATIVE_POSITION = 0;

	public static final String	REFLECTION_INBOUND_PORT_URI =
								"fan-unit-tester-RIP-URI";

	protected FanOutboundPort	fop;
	protected String			fanInboundPortURI;

	// Execution/Simulation

	protected static int		NUMBER_OF_STANDARD_THREADS = 1;
	protected static int		NUMBER_OF_SCHEDULABLE_THREADS = 1;

	protected TestsStatistics	statistics;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean	implementationInvariants(FanTesterCyPhy ft)
	{
		assert	ft != null : new PreconditionException("ft != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				ft.fanInboundPortURI != null &&
											!ft.fanInboundPortURI.isEmpty(),
				FanTesterCyPhy.class, ft,
				"ft.fanInboundPortURI != null && "
				+ "!ft.fanInboundPortURI.isEmpty()");
		return ret;
	}

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				REFLECTION_INBOUND_PORT_URI != null &&
											!REFLECTION_INBOUND_PORT_URI.isEmpty(),
				FanTesterCyPhy.class,
				"REFLECTION_INBOUND_PORT_URI != null && "
				+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				X_RELATIVE_POSITION >= 0,
				FanTesterCyPhy.class,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkStaticInvariant(
				Y_RELATIVE_POSITION >= 0,
				FanTesterCyPhy.class,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	protected static boolean	invariants(FanTesterCyPhy ft)
	{
		assert	ft != null : new PreconditionException("ft != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	// Standard execution for manual tests (no test scenario and no simulation)

	protected			FanTesterCyPhy(
		String fanInboundPortURI
		) throws Exception
	{
		super(REFLECTION_INBOUND_PORT_URI,
			  NUMBER_OF_STANDARD_THREADS,
			  NUMBER_OF_SCHEDULABLE_THREADS);

		this.initialise(fanInboundPortURI);
	}

	// Test execution with test scenario

	protected			FanTesterCyPhy(
		String fanInboundPortURI,
		ExecutionMode executionMode,
		TestScenario testScenario
		) throws Exception
	{
		super(REFLECTION_INBOUND_PORT_URI,
			  NUMBER_OF_STANDARD_THREADS,
			  NUMBER_OF_SCHEDULABLE_THREADS,
			  executionMode,
			  AssertionChecking.assertTrueAndReturnOrThrow(
					testScenario != null,
					testScenario.getClockURI(),
					() -> new PreconditionException("testScenario != null")),
			  testScenario);

		this.initialise(fanInboundPortURI);
	}

	protected void		initialise(String fanInboundPortURI)
	throws Exception
	{
		assert	fanInboundPortURI != null :
				new PreconditionException("fanInboundPortURI != null");
		assert	!fanInboundPortURI.isEmpty() :
				new PreconditionException("!fanInboundPortURI.isEmpty()");

		this.fanInboundPortURI = fanInboundPortURI;
		this.fop = new FanOutboundPort(this);
		this.fop.publishPort();

		if (FanTesterCyPhy.VERBOSE) {
			this.tracer.get().setTitle("Fan tester component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
										  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		assert	FanTesterCyPhy.implementationInvariants(this) :
				new ImplementationInvariantException(
						"FanTester.implementationInvariants(this)");
		assert	FanTesterCyPhy.invariants(this) :
				new InvariantException("FanTester.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Test action methods
	// -------------------------------------------------------------------------

	public void		turnOnFan() throws Exception
	{
		this.fop.turnOn();
	}

	public void		turnOffFan() throws Exception
	{
		this.fop.turnOff();
	}

	public void		setLowFan() throws Exception
	{
		this.fop.setLow();
	}

	public void		setMediumFan() throws Exception
	{
		this.fop.setMedium();
	}

	public void		setHighFan() throws Exception
	{
		this.fop.setHigh();
	}

	// -------------------------------------------------------------------------
	// Tests implementations
	// -------------------------------------------------------------------------

	protected void		runAllUnitTests() throws Exception
	{
		FanState state = this.fop.getState();
		assert	state == FanState.OFF : "initial state should be OFF";

		this.fop.turnOn();
		state = this.fop.getState();
		assert	state == FanState.LOW : "state after turnOn should be LOW";

		this.fop.setMedium();
		state = this.fop.getState();
		assert	state == FanState.MEDIUM : "state after setMedium should be MEDIUM";

		this.fop.setHigh();
		state = this.fop.getState();
		assert	state == FanState.HIGH : "state after setHigh should be HIGH";

		this.fop.setLow();
		state = this.fop.getState();
		assert	state == FanState.LOW : "state after setLow should be LOW";

		this.fop.turnOff();
		state = this.fop.getState();
		assert	state == FanState.OFF : "state after turnOff should be OFF";

		this.statistics.statisticsReport(this);
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void	start()
	throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
							this.fop.getPortURI(),
							fanInboundPortURI,
							FanConnector.class.getCanonicalName());
		} catch (Throwable e) {
			throw new ComponentStartException(e) ;
		}
	}

	@Override
	public synchronized void execute() throws Exception
	{
		this.traceMessage("Fan Tester begins execution.\n");

		switch (this.getExecutionMode()) {
		case UNIT_TEST:
		case INTEGRATION_TEST:
			this.initialiseClock(
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					this.clockURI);
			this.executeTestScenario(testScenario);
			break;
		case UNIT_TEST_WITH_SIL_SIMULATION:
		case INTEGRATION_TEST_WITH_SIL_SIMULATION:
			this.initialiseClock4Simulation(
					ClocksServerWithSimulation.STANDARD_INBOUNDPORT_URI,
					this.clockURI);
			this.executeTestScenario(testScenario);
			break;
		case INTEGRATION_TEST_WITH_HIL_SIMULATION:
		case UNIT_TEST_WITH_HIL_SIMULATION:
			throw new BCMException("HIL simulation not implemented yet!");
		case STANDARD:
			this.statistics = new TestsStatistics();
			this.traceMessage("Fan Tester starts the tests.\n");
			this.runAllUnitTests();
			this.traceMessage("Fan Tester ends.\n");
			break;
		default:
		}
		this.traceMessage("Fan Tester ends execution.\n");
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.fop.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.fop.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
// -----------------------------------------------------------------------------
