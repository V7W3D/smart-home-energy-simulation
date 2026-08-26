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

import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.connections.RefrigeratorConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.connections.RefrigeratorOutboundPort;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

// -----------------------------------------------------------------------------
/**
 * The class <code>RefrigeratorTesterCyPhy</code>.
 *
 * <p>Created on : 2026-01-15</p>
 *
 * @author	Softweavers
 */
@RequiredInterfaces(required = {RefrigeratorUserCI.class})
public class			RefrigeratorTesterCyPhy
extends		AbstractCyPhyComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static boolean		VERBOSE = false;
	public static int			X_RELATIVE_POSITION = 0;
	public static int			Y_RELATIVE_POSITION = 0;

	public static final String	REFLECTION_INBOUND_PORT_URI =
			"refrigerator-unit-tester-RIP-URI";

	protected static int			NUMBER_OF_STANDARD_THREADS = 1;
	protected static int			NUMBER_OF_SCHEDULABLE_THREADS = 1;

	protected RefrigeratorOutboundPort	rop;
	protected String						refrigeratorInboundPortURI;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			RefrigeratorTesterCyPhy(
		String refrigeratorInboundPortURI,
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

		this.initialise(refrigeratorInboundPortURI);
	}

	protected void		initialise(String refrigeratorInboundPortURI)
	throws Exception
	{
		assert	refrigeratorInboundPortURI != null
					&& !refrigeratorInboundPortURI.isEmpty() :
				new PreconditionException(
						"refrigeratorInboundPortURI != null && !refrigeratorInboundPortURI.isEmpty()");

		this.refrigeratorInboundPortURI = refrigeratorInboundPortURI;
		this.rop = new RefrigeratorOutboundPort(this);
		this.rop.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Refrigerator tester component");
			this.tracer.get().setRelativePosition(
										X_RELATIVE_POSITION,
										Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
	}

	// -------------------------------------------------------------------------
	// Test action methods
	// -------------------------------------------------------------------------

	public void			turnOnRefrigerator() throws Exception
	{
		this.rop.turnOn();
	}

	public void			setTargetTemperature(double target) throws Exception
	{
		this.rop.setTargetTemperature(target);
	}

	public void			turnOffRefrigerator() throws Exception
	{
		this.rop.turnOff();
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();
		try {
			this.doPortConnection(
							this.rop.getPortURI(),
							refrigeratorInboundPortURI,
							RefrigeratorConnector.class.getCanonicalName());
		} catch (Throwable e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public synchronized void	execute() throws Exception
	{
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
		default:
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
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}
// -----------------------------------------------------------------------------
