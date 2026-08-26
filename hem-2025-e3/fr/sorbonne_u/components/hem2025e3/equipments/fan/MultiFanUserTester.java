package fr.sorbonne_u.components.hem2025e3.equipments.fan;

import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanOutboundPort;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The class <code>MultiFanUserTester</code>.
 *
 * <p>Created on : 2026-01-10</p>
 *
 * @author	Softweavers
 */
@RequiredInterfaces(required = {FanUserCI.class})
public class			MultiFanUserTester
extends		AbstractCyPhyComponent
{
	public static boolean		VERBOSE = false;
	public static int			X_RELATIVE_POSITION = 0;
	public static int			Y_RELATIVE_POSITION = 0;

	public static final String	REFLECTION_INBOUND_PORT_URI =
								"multi-fan-tester-rip";

	protected static int		NUMBER_OF_STANDARD_THREADS = 1;
	protected static int		NUMBER_OF_SCHEDULABLE_THREADS = 1;

	protected final String[]	fanInboundPortURIs;
	protected final Map<String, FanOutboundPort>	fanPorts;

	protected			MultiFanUserTester(
		String[] fanInboundPortURIs,
		ExecutionMode executionMode,
		TestScenario testScenario
		) throws Exception
	{
		super(REFLECTION_INBOUND_PORT_URI,
			  NUMBER_OF_STANDARD_THREADS,
			  NUMBER_OF_SCHEDULABLE_THREADS,
			  AssertionChecking.assertTrueAndReturnOrThrow(
					executionMode != null && !executionMode.isStandard(),
					executionMode,
					() -> new PreconditionException(
							"executionMode != null && !executionMode.isStandard()")),
			  AssertionChecking.assertTrueAndReturnOrThrow(
					testScenario != null,
					testScenario.getClockURI(),
					() -> new PreconditionException("testScenario != null")),
			  testScenario);

		assert	fanInboundPortURIs != null :
				new PreconditionException("fanInboundPortURIs != null");
		this.fanInboundPortURIs = fanInboundPortURIs.clone();
		this.fanPorts = new LinkedHashMap<>();

		this.initialise();

		assert	MultiFanUserTester.invariants(this) :
				new InvariantException("MultiFanUserTester.invariants(this)");
	}

	protected void			initialise() throws Exception
	{
		for (int i = 0; i < this.fanInboundPortURIs.length; i++) {
			String uri = this.fanInboundPortURIs[i];
			assert	uri != null && !uri.isEmpty() :
					new PreconditionException("fanInboundPortURI != null && !fanInboundPortURI.isEmpty()");
			FanOutboundPort fop = new FanOutboundPort(this);
			fop.publishPort();
			this.fanPorts.put(uri, fop);
		}

		if (VERBOSE) {
			this.tracer.get().setTitle("Multi-fan user tester");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
									  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
	}

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();
		try {
			for (Map.Entry<String, FanOutboundPort> entry : this.fanPorts.entrySet()) {
				this.doPortConnection(
						entry.getValue().getPortURI(),
						entry.getKey(),
						FanConnector.class.getCanonicalName());
			}
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public synchronized void	execute() throws Exception
	{
		this.traceMessage("Multi-fan tester begins execution.\n");
		switch (this.getExecutionMode()) {
		case UNIT_TEST:
		case INTEGRATION_TEST:
			this.initialiseClock(
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					this.testScenario.getClockURI());
			this.executeTestScenario(this.testScenario);
			break;
		case UNIT_TEST_WITH_SIL_SIMULATION:
		case INTEGRATION_TEST_WITH_SIL_SIMULATION:
			this.initialiseClock4Simulation(
					ClocksServerWithSimulation.STANDARD_INBOUNDPORT_URI,
					this.testScenario.getClockURI());
			this.executeTestScenario(this.testScenario);
			break;
		case UNIT_TEST_WITH_HIL_SIMULATION:
		case INTEGRATION_TEST_WITH_HIL_SIMULATION:
			throw new BCMException("HIL simulation not implemented yet!");
		case STANDARD:
		default:
		}
		this.traceMessage("Multi-fan tester ends execution.\n");
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		for (FanOutboundPort port : this.fanPorts.values()) {
			this.doPortDisconnection(port.getPortURI());
		}
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			for (FanOutboundPort port : this.fanPorts.values()) {
				port.unpublishPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	public void			turnOnAll() throws Exception
	{
		for (FanOutboundPort port : this.fanPorts.values()) {
			port.turnOn();
		}
	}

	public void			setHighAll() throws Exception
	{
		for (FanOutboundPort port : this.fanPorts.values()) {
			port.setHigh();
		}
	}

	public void			setMediumAll() throws Exception
	{
		for (FanOutboundPort port : this.fanPorts.values()) {
			port.setMedium();
		}
	}

	public void			setLowAll() throws Exception
	{
		for (FanOutboundPort port : this.fanPorts.values()) {
			port.setLow();
		}
	}

	public void			turnOffAll() throws Exception
	{
		for (FanOutboundPort port : this.fanPorts.values()) {
			port.turnOff();
		}
	}

	protected static boolean	invariants(MultiFanUserTester instance)
	{
		assert	instance != null : new PreconditionException("instance != null");
		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				instance.fanInboundPortURIs != null,
				MultiFanUserTester.class,
				instance,
				"fanInboundPortURIs != null");
		return ret;
	}
}
