package fr.sorbonne_u.components.hem2025e1.equipments.washingmachine;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI.WashProgram;
import fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI.WashingMachineState;
import fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.connections.WashingMachineConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.connections.WashingMachineOutboundPort;

/**
 * The class <code>WashingMachineTester</code> implements a component that tests
 * the washing machine component.
 *
 * <p>Created on : 2025-12-29</p>
 * 
 * @author	Softweavers
 */
@RequiredInterfaces(required={WashingMachineUserCI.class})
public class			WashingMachineTester
extends		AbstractComponent
{
	public static boolean					VERBOSE = false;
	public static int						X_RELATIVE_POSITION = 0;
	public static int						Y_RELATIVE_POSITION = 0;

	protected WashingMachineOutboundPort	wmop;
	protected boolean						isUnitTest;

	protected			WashingMachineTester() throws Exception
	{
		this(true);
	}

	protected			WashingMachineTester(boolean isUnitTest) throws Exception
	{
		super(1, 0);
		this.isUnitTest = isUnitTest;
		this.initialise();
	}

	protected void		initialise() throws Exception
	{
		this.wmop = new WashingMachineOutboundPort(this);
		this.wmop.publishPort();

		if (WashingMachineTester.VERBOSE) {
			this.tracer.get().setTitle("WashingMachine tester component");
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
					this.wmop.getPortURI(),
					WashingMachine.INBOUND_PORT_URI,
					WashingMachineConnector.class.getCanonicalName());
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
		this.doPortDisconnection(this.wmop.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.wmop.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	protected void		runAllTests() throws Exception
	{
		this.traceMessage("Testing WashingMachine component...\n");

		WashingMachineState state = this.wmop.getState();
		assert state == WashingMachineState.OFF : "Initial state should be OFF";
		this.traceMessage("Initial state test passed: " + state + "\n");

		this.wmop.turnOn();
		state = this.wmop.getState();
		assert state == WashingMachineState.IDLE : "State after turnOn should be IDLE";
		this.traceMessage("Turn on test passed: " + state + "\n");

		WashProgram program = this.wmop.getSelectedProgram();
		assert program == WashProgram.STANDARD : "Default program should be STANDARD";
		this.traceMessage("Default program test passed: " + program + "\n");

		this.wmop.selectProgram(WashProgram.QUICK);
		program = this.wmop.getSelectedProgram();
		assert program == WashProgram.QUICK : "Program should be QUICK";
		this.traceMessage("Program selection test passed: " + program + "\n");

		this.wmop.selectProgram(WashProgram.INTENSIVE);
		program = this.wmop.getSelectedProgram();
		assert program == WashProgram.INTENSIVE : "Program should be INTENSIVE";
		this.traceMessage("Program INTENSIVE test passed: " + program + "\n");

		this.wmop.selectProgram(WashProgram.QUICK);
		this.wmop.startCycle();
		state = this.wmop.getState();
		assert state == WashingMachineState.RUNNING : "State should be RUNNING";
		this.traceMessage("Start cycle test passed: " + state + "\n");

		double power = this.wmop.getCurrentPowerLevel();
		assert power == 500.0 : "Power during QUICK cycle should be 500W";
		this.traceMessage("QUICK cycle power test passed: " + power + "W\n");

		this.wmop.suspendCycle();
		state = this.wmop.getState();
		assert state == WashingMachineState.SUSPENDED : "State should be SUSPENDED";
		assert this.wmop.isSuspended() : "isSuspended should be true";
		this.traceMessage("Suspend cycle test passed: " + state + "\n");

		power = this.wmop.getCurrentPowerLevel();
		assert power == 0.0 : "Power when suspended should be 0W";
		this.traceMessage("Suspended power test passed: " + power + "W\n");

		this.wmop.resumeCycle();
		state = this.wmop.getState();
		assert state == WashingMachineState.RUNNING : "State should be RUNNING after resume";
		assert !this.wmop.isSuspended() : "isSuspended should be false";
		this.traceMessage("Resume cycle test passed: " + state + "\n");

		this.wmop.cancelCycle();
		state = this.wmop.getState();
		assert state == WashingMachineState.IDLE : "State should be IDLE after cancel";
		this.traceMessage("Cancel cycle test passed: " + state + "\n");

		this.wmop.selectProgram(WashProgram.STANDARD);
		int delay = 60;
		this.wmop.scheduleCycle(delay);
		state = this.wmop.getState();
		assert state == WashingMachineState.SCHEDULED : "State should be SCHEDULED";
		int scheduledDelay = this.wmop.getScheduledDelay();
		assert scheduledDelay == delay : "Scheduled delay should match";
		this.traceMessage("Schedule cycle test passed: delay=" + scheduledDelay + " min\n");

		this.wmop.cancelCycle();
		this.wmop.turnOff();
		state = this.wmop.getState();
		assert state == WashingMachineState.OFF : "State should be OFF after turnOff";
		this.traceMessage("Turn off test passed: " + state + "\n");

		this.traceMessage("All WashingMachine tests completed successfully!\n");
	}
}
