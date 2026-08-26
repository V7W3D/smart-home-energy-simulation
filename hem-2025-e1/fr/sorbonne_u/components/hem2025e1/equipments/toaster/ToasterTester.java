package fr.sorbonne_u.components.hem2025e1.equipments.toaster;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterImplementationI.ToasterState;
import fr.sorbonne_u.components.hem2025e1.equipments.toaster.connections.ToasterConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.toaster.connections.ToasterOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>ToasterTester</code> implements a component that tests
 * the toaster component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-12-26</p>
 * 
 * @author	Softweavers
 */
@RequiredInterfaces(required = {ToasterUserCI.class})
public class			ToasterTester
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** when true, methods trace their actions.								*/
	public static boolean		VERBOSE = true;
	/** X position of the tracer window.									*/
	public static int			X_RELATIVE_POSITION = 0;
	/** Y position of the tracer window.									*/
	public static int			Y_RELATIVE_POSITION = 0;

	/** outbound port to connect to the toaster.							*/
	protected ToasterOutboundPort	toasterOutboundPort;
	/** URI of the toaster inbound port to connect to.						*/
	protected String			toasterInboundPortURI;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a toaster tester component.
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
	protected			ToasterTester() throws Exception
	{
		this(Toaster.INBOUND_PORT_URI);
	}

	/**
	 * create a toaster tester component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code toasterInboundPortURI != null && !toasterInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param toasterInboundPortURI	URI of the toaster inbound port.
	 * @throws Exception			<i>to do</i>.
	 */
	protected			ToasterTester(String toasterInboundPortURI) throws Exception
	{
		super(1, 0);

		this.toasterInboundPortURI = toasterInboundPortURI;
		this.toasterOutboundPort = new ToasterOutboundPort(this);
		this.toasterOutboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Toaster tester component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
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
			this.doPortConnection(
					this.toasterOutboundPort.getPortURI(),
					this.toasterInboundPortURI,
					ToasterConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void	execute() throws Exception
	{
		this.runAllTests();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.toasterOutboundPort.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.toasterOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	/**
	 * run all tests on the toaster component.
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
	protected void		runAllTests() throws Exception
	{
		System.out.println(">>> ToasterTester: Starting tests <<<");
		this.traceMessage("Starting toaster tests...\n");

		// Test initial state
		this.traceMessage("Test 1: Initial state should be OFF\n");
		assert this.toasterOutboundPort.getState() == ToasterState.OFF :
				"Initial state should be OFF";
		this.traceMessage("  -> PASSED\n");
		System.out.println(">>> ToasterTester: Test 1 PASSED - Initial state = OFF <<<");

		// Test initial power level
		this.traceMessage("Test 2: Initial power level should be 0\n");
		assert Math.abs(this.toasterOutboundPort.getCurrentPowerLevel() - 
				ToasterImplementationI.OFF_POWER) < 0.01 :
				"Initial power level should be 0";
		this.traceMessage("  -> PASSED\n");
		System.out.println(">>> ToasterTester: Test 2 PASSED - Initial power = 0W <<<");

		// Test pushDown
		this.traceMessage("Test 3: pushDown() should start toasting\n");
		this.toasterOutboundPort.pushDown();
		assert this.toasterOutboundPort.getState() == ToasterState.TOASTING :
				"State after pushDown should be TOASTING";
		this.traceMessage("  -> PASSED\n");
		System.out.println(">>> ToasterTester: Test 3 PASSED - After pushDown, state = TOASTING <<<");

		// Test power level when toasting
		this.traceMessage("Test 4: Power level when toasting should be 1000W\n");
		assert Math.abs(this.toasterOutboundPort.getCurrentPowerLevel() - 
				ToasterImplementationI.TOASTING_POWER) < 0.01 :
				"Power level when toasting should be 1000W";
		this.traceMessage("  -> PASSED\n");
		System.out.println(">>> ToasterTester: Test 4 PASSED - Toasting power = 1000W <<<");

		// Wait a bit to simulate toasting
		Thread.sleep(2000);

		// Test pullUp
		this.traceMessage("Test 5: pullUp() should stop toasting\n");
		this.toasterOutboundPort.pullUp();
		assert this.toasterOutboundPort.getState() == ToasterState.OFF :
				"State after pullUp should be OFF";
		this.traceMessage("  -> PASSED\n");
		System.out.println(">>> ToasterTester: Test 5 PASSED - After pullUp, state = OFF <<<");

		// Test power level after pullUp
		this.traceMessage("Test 6: Power level after pullUp should be 0\n");
		assert Math.abs(this.toasterOutboundPort.getCurrentPowerLevel() - 
				ToasterImplementationI.OFF_POWER) < 0.01 :
				"Power level after pullUp should be 0";
		this.traceMessage("  -> PASSED\n");
		System.out.println(">>> ToasterTester: Test 6 PASSED - After pullUp, power = 0W <<<");

		// Test pullUp when already off
		this.traceMessage("Test 7: pullUp() when already off should work\n");
		this.toasterOutboundPort.pullUp();
		assert this.toasterOutboundPort.getState() == ToasterState.OFF :
				"State should still be OFF";
		this.traceMessage("  -> PASSED\n");
		System.out.println(">>> ToasterTester: Test 7 PASSED - pullUp when OFF works <<<");

		// Test another cycle
		this.traceMessage("Test 8: Another toasting cycle\n");
		this.toasterOutboundPort.pushDown();
		assert this.toasterOutboundPort.getCurrentPowerLevel() > 0 :
				"Power level should be > 0 when toasting";
		Thread.sleep(1000);
		this.toasterOutboundPort.pullUp();
		assert this.toasterOutboundPort.getState() == ToasterState.OFF :
				"State after pullUp should be OFF";
		this.traceMessage("  -> PASSED\n");
		System.out.println(">>> ToasterTester: Test 8 PASSED - Second cycle complete <<<");

		this.traceMessage("All toaster tests completed successfully!\n");
		System.out.println(">>> ToasterTester: ALL TESTS PASSED <<<");
	}
}
// -----------------------------------------------------------------------------
