package fr.sorbonne_u.components.hem2025e1.equipments.refrigerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI.RefrigeratorState;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.connections.RefrigeratorConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.connections.RefrigeratorOutboundPort;

/**
 * The class <code>RefrigeratorTester</code> implements a component that tests
 * the refrigerator component.
 *
 * <p>Created on : 2025-12-27</p>
 * 
 * @author	Softweavers
 */
@RequiredInterfaces(required={RefrigeratorUserCI.class})
public class			RefrigeratorTester
extends		AbstractComponent
{
	public static boolean				VERBOSE = false;
	public static int					X_RELATIVE_POSITION = 0;
	public static int					Y_RELATIVE_POSITION = 0;

	protected RefrigeratorOutboundPort	rop;
	protected boolean					isUnitTest;

	protected			RefrigeratorTester(boolean isUnitTest) throws Exception
	{
		super(1, 0);
		this.isUnitTest = isUnitTest;
		this.initialise();
	}

	protected void		initialise() throws Exception
	{
		this.rop = new RefrigeratorOutboundPort(this);
		this.rop.publishPort();

		if (RefrigeratorTester.VERBOSE) {
			this.tracer.get().setTitle("Refrigerator tester component");
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
					this.rop.getPortURI(),
					Refrigerator.INBOUND_PORT_URI,
					RefrigeratorConnector.class.getCanonicalName());
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
		this.doPortDisconnection(this.rop.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.rop.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	protected void		runAllTests() throws Exception
	{
		System.out.println(">>> RefrigeratorTester: Starting tests <<<");
		this.traceMessage("Testing Refrigerator component...\n");

		RefrigeratorState state = this.rop.getState();
		assert state == RefrigeratorState.OFF : "Initial state should be OFF";
		this.traceMessage("Initial state test passed: " + state + "\n");
		System.out.println(">>> RefrigeratorTester: Initial state = " + state + " <<<");

		this.rop.turnOn();
		state = this.rop.getState();
		assert state == RefrigeratorState.ON : "State after turnOn should be ON";
		this.traceMessage("Turn on test passed: " + state + "\n");
		System.out.println(">>> RefrigeratorTester: After turnOn, state = " + state + " <<<");

		double power = this.rop.getCurrentPowerLevel();
		assert power == RefrigeratorImplementationI.COMPRESSOR_POWER : "Power when ON should be 150W";
		this.traceMessage("ON power test passed: " + power + "W\n");
		System.out.println(">>> RefrigeratorTester: ON power = " + power + "W <<<");

		double targetTemp = this.rop.getTargetTemperature();
		assert targetTemp == RefrigeratorImplementationI.DEFAULT_TARGET_TEMPERATURE : "Default target should be 4°C";
		this.traceMessage("Default target temperature test passed: " + targetTemp + "°C\n");
		System.out.println(">>> RefrigeratorTester: Default target temp = " + targetTemp + "°C <<<");

		this.rop.setTargetTemperature(3.0);
		targetTemp = this.rop.getTargetTemperature();
		assert targetTemp == 3.0 : "Target temperature should be 3°C";
		this.traceMessage("Set target temperature test passed: " + targetTemp + "°C\n");
		System.out.println(">>> RefrigeratorTester: New target temp = " + targetTemp + "°C <<<");

		this.rop.suspendCompressor();
		state = this.rop.getState();
		assert state == RefrigeratorState.SUSPENDED : "State should be SUSPENDED";
		assert this.rop.isCompressorSuspended() : "Compressor should be suspended";
		power = this.rop.getCurrentPowerLevel();
		assert power == RefrigeratorImplementationI.STANDBY_POWER : "Power when SUSPENDED should be 10W";
		this.traceMessage("Suspend compressor test passed: " + state + ", " + power + "W\n");
		System.out.println(">>> RefrigeratorTester: SUSPENDED state = " + state + ", power = " + power + "W <<<");

		this.rop.resumeCompressor();
		state = this.rop.getState();
		assert state == RefrigeratorState.ON : "State should be ON after resume";
		assert !this.rop.isCompressorSuspended() : "Compressor should not be suspended";
		this.traceMessage("Resume compressor test passed: " + state + "\n");
		System.out.println(">>> RefrigeratorTester: After resume, state = " + state + " <<<");

		this.rop.turnOff();
		state = this.rop.getState();
		assert state == RefrigeratorState.OFF : "State after turnOff should be OFF";
		power = this.rop.getCurrentPowerLevel();
		assert power == RefrigeratorImplementationI.OFF_POWER : "Power when OFF should be 0W";
		this.traceMessage("Turn off test passed: " + state + ", " + power + "W\n");
		System.out.println(">>> RefrigeratorTester: After turnOff, state = " + state + ", power = " + power + "W <<<");

		this.traceMessage("All Refrigerator tests completed successfully!\n");
		System.out.println(">>> RefrigeratorTester: ALL TESTS PASSED <<<");
	}
}
