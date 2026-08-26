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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMRuntimeException;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.components.utils.tests.TestStep;
import fr.sorbonne_u.components.utils.tests.TestStepI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.models.time.TimeUtils;
import fr.sorbonne_u.exceptions.VerboseException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>CVMUnitTest</code>.
 *
 * <p>Created on : 2026-01-10</p>
 *
 * @author	Softweavers
 */
public class			CVMUnitTest
extends		AbstractCVM
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static long			DELAY_TO_START = 3000L;
	public static long			END_SLEEP_DURATION = 10000L;

	public static TimeUnit		SIMULATION_TIME_UNIT = TimeUnit.HOURS;
	public static Time 			SIMULATION_START_TIME =
										new Time(0.0, SIMULATION_TIME_UNIT);
	public static Duration		SIMULATION_DURATION =
										new Duration(0.5, SIMULATION_TIME_UNIT);
	public static double		ACCELERATION_FACTOR = 360.0;
	public static long			EXECUTION_DURATION =
								DELAY_TO_START +
									TimeUnit.NANOSECONDS.toMillis(
											TimeUtils.toNanos(
													SIMULATION_DURATION.getSimulatedDuration() /
																ACCELERATION_FACTOR,
													SIMULATION_DURATION.getTimeUnit()));

	public static ExecutionMode	FAN_EXECUTION_MODE =
										ExecutionMode.UNIT_TEST_WITH_SIL_SIMULATION;

	public static ExecutionMode	FAN_TESTER_EXECUTION_MODE =
										ExecutionMode.UNIT_TEST;

	public static String		CLOCK_URI = "fan-test-clock";
	public static String		START_INSTANT = "2026-02-06T08:00:00.00Z";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				CVMUnitTest() throws Exception
	{
		FanTesterCyPhy.VERBOSE = true;
		FanTesterCyPhy.X_RELATIVE_POSITION = 0;
		FanTesterCyPhy.Y_RELATIVE_POSITION = 1;
		FanCyPhy.VERBOSE = true;
		FanCyPhy.X_RELATIVE_POSITION = 1;
		FanCyPhy.Y_RELATIVE_POSITION = 1;
	}

	// -------------------------------------------------------------------------
	// CVM life-cycle
	// -------------------------------------------------------------------------

	@Override
	public void			deploy() throws Exception
	{
		if (FAN_EXECUTION_MODE.isStandard()) {

			AbstractComponent.createComponent(
					FanCyPhy.class.getCanonicalName(),
					new Object[]{
							FanCyPhy.REFLECTION_INBOUND_PORT_URI,
							FanCyPhy.INBOUND_PORT_URI
					});

			AbstractComponent.createComponent(
					FanTesterCyPhy.class.getCanonicalName(),
					new Object[]{
							FanCyPhy.INBOUND_PORT_URI
					});

		} else if (FAN_EXECUTION_MODE.isTestWithoutSimulation()) {

			long current = System.currentTimeMillis();
			long unixEpochStartTimeInMillis = current + DELAY_TO_START;
			Instant	startInstant = Instant.parse(START_INSTANT);
			TestScenario testScenario = unitTestScenario();

			AbstractComponent.createComponent(
					FanCyPhy.class.getCanonicalName(),
					new Object[]{
							FanCyPhy.REFLECTION_INBOUND_PORT_URI,
							FanCyPhy.INBOUND_PORT_URI,
							FAN_EXECUTION_MODE
					});

			AbstractComponent.createComponent(
					FanTesterCyPhy.class.getCanonicalName(),
					new Object[]{
							FanCyPhy.INBOUND_PORT_URI,
							FAN_TESTER_EXECUTION_MODE,
							testScenario
					});

			AbstractComponent.createComponent(
					ClocksServer.class.getCanonicalName(),
					new Object[]{
							CLOCK_URI,
							TimeUnit.MILLISECONDS.toNanos(
											unixEpochStartTimeInMillis),
							startInstant,
							ACCELERATION_FACTOR
					});

		} else if (FAN_EXECUTION_MODE.isSILTest()) {

			long current = System.currentTimeMillis();
			long unixEpochStartTimeInMillis = current + DELAY_TO_START;
			Instant	startInstant = Instant.parse(START_INSTANT);
			TestScenario testScenario = unitTestScenarioWithSimulation();

			AbstractComponent.createComponent(
					FanCyPhy.class.getCanonicalName(),
					new Object[]{
							FanCyPhy.REFLECTION_INBOUND_PORT_URI,
							FanCyPhy.INBOUND_PORT_URI,
							FAN_EXECUTION_MODE,
							testScenario,
							FanCyPhy.UNIT_TEST_ARCHITECTURE_URI,
							ACCELERATION_FACTOR
					});

			AbstractComponent.createComponent(
					FanTesterCyPhy.class.getCanonicalName(),
					new Object[]{
							FanCyPhy.INBOUND_PORT_URI,
							FAN_TESTER_EXECUTION_MODE,
							testScenario
					});

			AbstractComponent.createComponent(
					ClocksServerWithSimulation.class.getCanonicalName(),
					new Object[]{
							CLOCK_URI,
							TimeUnit.MILLISECONDS.toNanos(
											unixEpochStartTimeInMillis),
							startInstant,
							ACCELERATION_FACTOR,
							DELAY_TO_START,
							SIMULATION_START_TIME,
							SIMULATION_DURATION});
		}

		super.deploy();
	}

	// -------------------------------------------------------------------------
	// Executing
	// -------------------------------------------------------------------------

	public static void	main(String[] args)
	{
		VerboseException.VERBOSE = true;
		VerboseException.PRINT_STACK_TRACE = true;
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			cvm.startStandardLifeCycle(EXECUTION_DURATION);
			Thread.sleep(END_SLEEP_DURATION);
			System.exit(0);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------
	// Test scenarios
	// -------------------------------------------------------------------------

	public static TestScenario	unitTestScenario() throws Exception
	{
		Instant startInstant = Instant.parse(START_INSTANT);
		long d = TimeUnit.NANOSECONDS.toSeconds(
						TimeUtils.toNanos(SIMULATION_DURATION));
		Instant endInstant = startInstant.plusSeconds(d);

		Instant fanTurnOn = startInstant.plusSeconds(60);
		Instant fanSetMedium = startInstant.plusSeconds(120);
		Instant fanSetHigh = startInstant.plusSeconds(180);
		Instant fanSetLow = startInstant.plusSeconds(240);
		Instant fanTurnOff = startInstant.plusSeconds(300);

		return new TestScenario(
			CLOCK_URI,
			startInstant,
			endInstant,
			new TestStepI[] {
				new TestStep(
					CLOCK_URI,
					FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
					fanTurnOn,
					owner ->  {
						try {
							((FanTesterCyPhy)owner).turnOnFan();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
					fanSetMedium,
					owner ->  {
						try {
							((FanTesterCyPhy)owner).setMediumFan();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
					fanSetHigh,
					owner ->  {
						try {
							((FanTesterCyPhy)owner).setHighFan();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
					fanSetLow,
					owner ->  {
						try {
							((FanTesterCyPhy)owner).setLowFan();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
					fanTurnOff,
					owner ->  {
						try {
							((FanTesterCyPhy)owner).turnOffFan();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					})
			});
	}

	public static TestScenarioWithSimulation	unitTestScenarioWithSimulation()
	throws Exception
	{
		Instant startInstant = Instant.parse(START_INSTANT);
		long d = TimeUnit.NANOSECONDS.toSeconds(
						TimeUtils.toNanos(SIMULATION_DURATION));
		Instant endInstant = startInstant.plusSeconds(d);

		Instant fanTurnOn = startInstant.plusSeconds(60);
		Instant fanSetMedium = startInstant.plusSeconds(120);
		Instant fanSetHigh = startInstant.plusSeconds(180);
		Instant fanSetLow = startInstant.plusSeconds(240);
		Instant fanTurnOff = startInstant.plusSeconds(300);

		return new TestScenarioWithSimulation(
			CLOCK_URI,
			startInstant,
			endInstant,
			"global-archi",
			SIMULATION_START_TIME,
			(ts, simParams) -> { },
			new TestStepI[] {
				new TestStep(
					CLOCK_URI,
					FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
					fanTurnOn,
					owner ->  {
						try {
							((FanTesterCyPhy)owner).turnOnFan();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
					fanSetMedium,
					owner ->  {
						try {
							((FanTesterCyPhy)owner).setMediumFan();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
					fanSetHigh,
					owner ->  {
						try {
							((FanTesterCyPhy)owner).setHighFan();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
					fanSetLow,
					owner ->  {
						try {
							((FanTesterCyPhy)owner).setLowFan();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
					fanTurnOff,
					owner ->  {
						try {
							((FanTesterCyPhy)owner).turnOffFan();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					})
			});
	}
}
// -----------------------------------------------------------------------------
