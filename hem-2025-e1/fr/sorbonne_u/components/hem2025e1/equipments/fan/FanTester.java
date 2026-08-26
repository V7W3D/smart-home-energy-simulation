package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI.FanState;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanOutboundPort;

/**
 * The class <code>FanTester</code> implements a component that tests
 * the fan component.
 *
 * <p>Created on : 2025-12-25</p>
 * 
 * @author	Softweavers
 */
@RequiredInterfaces(required={FanUserCI.class})
public class			FanTester
extends		AbstractComponent
{
	public static boolean		VERBOSE = false;
	public static int			X_RELATIVE_POSITION = 0;
	public static int			Y_RELATIVE_POSITION = 0;

	protected FanOutboundPort	fop;
	protected boolean			isUnitTest;

	protected			FanTester(boolean isUnitTest) throws Exception
	{
		super(1, 0);
		this.isUnitTest = isUnitTest;
		this.initialise();
	}

	protected void		initialise() throws Exception
	{
		this.fop = new FanOutboundPort(this);
		this.fop.publishPort();

		if (FanTester.VERBOSE) {
			this.tracer.get().setTitle("Fan tester component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
	}

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();
		try {
			this.doPortConnection(
					this.fop.getPortURI(),
					Fan.INBOUND_PORT_URI,
					FanConnector.class.getCanonicalName());
		} catch (Throwable e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public synchronized void	execute() throws Exception
	{
		if (this.isUnitTest) {
			this.runAllTests();
		}
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
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	protected void		runAllTests() throws Exception
	{
		System.out.println(">>> FanTester: Starting tests <<<");
		this.traceMessage("Testing Fan component...\n");

		FanState state = this.fop.getState();
		assert state == FanState.OFF : "Initial state should be OFF";
		this.traceMessage("Initial state test passed: " + state + "\n");
		System.out.println(">>> FanTester: Initial state = " + state + " <<<");

		this.fop.turnOn();
		state = this.fop.getState();
		assert state == FanState.LOW : "State after turnOn should be LOW";
		this.traceMessage("Turn on test passed: " + state + "\n");
		System.out.println(">>> FanTester: After turnOn, state = " + state + " <<<");

		double power = this.fop.getCurrentPowerLevel();
		assert power == FanImplementationI.LOW_POWER : "Power in LOW mode should be 50W";
		this.traceMessage("LOW power test passed: " + power + "W\n");
		System.out.println(">>> FanTester: LOW power = " + power + "W <<<");

		this.fop.setMedium();
		state = this.fop.getState();
		assert state == FanState.MEDIUM : "State should be MEDIUM";
		power = this.fop.getCurrentPowerLevel();
		assert power == FanImplementationI.MEDIUM_POWER : "Power in MEDIUM mode should be 100W";
		this.traceMessage("MEDIUM test passed: " + state + ", " + power + "W\n");
		System.out.println(">>> FanTester: MEDIUM state = " + state + ", power = " + power + "W <<<");

		this.fop.setHigh();
		state = this.fop.getState();
		assert state == FanState.HIGH : "State should be HIGH";
		power = this.fop.getCurrentPowerLevel();
		assert power == FanImplementationI.HIGH_POWER : "Power in HIGH mode should be 150W";
		this.traceMessage("HIGH test passed: " + state + ", " + power + "W\n");
		System.out.println(">>> FanTester: HIGH state = " + state + ", power = " + power + "W <<<");

		this.fop.setLow();
		state = this.fop.getState();
		assert state == FanState.LOW : "State should be LOW";
		this.traceMessage("setLow test passed: " + state + "\n");
		System.out.println(">>> FanTester: Back to LOW state = " + state + " <<<");

		this.fop.turnOff();
		state = this.fop.getState();
		assert state == FanState.OFF : "State after turnOff should be OFF";
		power = this.fop.getCurrentPowerLevel();
		assert power == FanImplementationI.OFF_POWER : "Power when OFF should be 0W";
		this.traceMessage("Turn off test passed: " + state + ", " + power + "W\n");
		System.out.println(">>> FanTester: After turnOff, state = " + state + ", power = " + power + "W <<<");

		this.traceMessage("All Fan tests completed successfully!\n");
		System.out.println(">>> FanTester: ALL TESTS PASSED <<<");
	}
}
