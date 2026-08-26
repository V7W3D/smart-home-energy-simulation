package fr.sorbonne_u.components.hem2025e3;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMRuntimeException;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.Batteries;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.Generator;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.SolarPanel;
import fr.sorbonne_u.components.hem2025e2.equipments.batteries.mil.BatteriesSimulationConfiguration;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.GeneratorSimulationConfiguration;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.DeterministicSunIntensityModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.DeterministicSunRiseAndSetModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SolarPanelSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SunIntensityModelI;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SunRiseAndSetModelI;
import fr.sorbonne_u.components.hem2025e2.equipments.refrigerator.mil.RefrigeratorElectricityModel;
import fr.sorbonne_u.components.hem2025e3.equipments.batteries.BatteriesCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.batteries.sil.BatteriesPowerSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.batteries.sil.BatteriesStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.batteries.sil.BatteriesUnitTesterSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sil.FanElectricitySILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.MultiFanUserTester;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.GeneratorCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.GeneratorFuelSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.GeneratorPowerSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.GeneratorUnitTesterSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.HeaterController;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.HeaterCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.HeaterController.ControlMode;
import fr.sorbonne_u.components.hem2025e3.equipments.hem.HEMCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.meter.ElectricMeterCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.meter.sil.ElectricMeterElectricitySILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.refrigerator.RefrigeratorCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.refrigerator.RefrigeratorTesterCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.solar_panel.SolarPanelCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.solar_panel.sil.SolarPanelPowerSILModel;
import fr.sorbonne_u.components.utils.tests.TestStep;
import fr.sorbonne_u.components.utils.tests.TestStepI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.models.time.TimeUtils;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.exceptions.VerboseException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class			CVM_Global
extends		AbstractCVM
{
	public static long			DELAY_TO_START = 15000L;
	public static long			END_SLEEP_DURATION = 100000L;

	public static TimeUnit		SIMULATION_TIME_UNIT = TimeUnit.HOURS;
	public static Time 			SIMULATION_START_TIME =
						new Time(0.0, SIMULATION_TIME_UNIT);
	public static Duration		SIMULATION_DURATION =
						new Duration(6.0, SIMULATION_TIME_UNIT);
	public static double		ACCELERATION_FACTOR = 3600.0;
	public static long			EXECUTION_DURATION =
			DELAY_TO_START +
				TimeUnit.NANOSECONDS.toMillis(
						TimeUtils.toNanos(
								SIMULATION_DURATION.getSimulatedDuration() /
										ACCELERATION_FACTOR,
								SIMULATION_DURATION.getTimeUnit()));

	public static ExecutionMode	GLOBAL_EXECUTION_MODE =
						ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION;

	public static String		CLOCK_URI = "global-test-clock";
	public static Instant		START_INSTANT =
						Instant.parse("2025-12-02T12:00:00.00Z");

	public			CVM_Global() throws Exception
	{
		ClocksServer.VERBOSE = true;
		ClocksServer.X_RELATIVE_POSITION = 0;
		ClocksServer.Y_RELATIVE_POSITION = 0;
		HEMCyPhy.VERBOSE = true;
		HEMCyPhy.DEBUG = true;
		HEMCyPhy.X_RELATIVE_POSITION = 0;
		HEMCyPhy.Y_RELATIVE_POSITION = 1;
		ElectricMeterCyPhy.VERBOSE = true;
		ElectricMeterCyPhy.DEBUG = true;
		ElectricMeterCyPhy.X_RELATIVE_POSITION = 1;
		ElectricMeterCyPhy.Y_RELATIVE_POSITION = 0;
		ElectricMeterElectricitySILModel.DEBUG = true;
		BatteriesCyPhy.VERBOSE = true;
		BatteriesCyPhy.X_RELATIVE_POSITION = 2;
		BatteriesCyPhy.Y_RELATIVE_POSITION = 0;
		GeneratorCyPhy.VERBOSE = true;
		GeneratorCyPhy.X_RELATIVE_POSITION = 0;
		GeneratorCyPhy.Y_RELATIVE_POSITION = 2;
		SolarPanelCyPhy.VERBOSE = true;
		SolarPanelCyPhy.X_RELATIVE_POSITION = 3;
		SolarPanelCyPhy.Y_RELATIVE_POSITION = 1;
		FanCyPhy.VERBOSE = true;
		FanCyPhy.X_RELATIVE_POSITION = 4;
		FanCyPhy.Y_RELATIVE_POSITION = 0;
		RefrigeratorCyPhy.VERBOSE = true;
		RefrigeratorCyPhy.X_RELATIVE_POSITION = 5;
		RefrigeratorCyPhy.Y_RELATIVE_POSITION = 1;
		RefrigeratorElectricityModel.VERBOSE = false;
		RefrigeratorElectricityModel.DEBUG = false;

		assert	implementationInvariants(this) :
				new InvariantException("CVM_Global.implementationInvariants(this)");
	}

	protected static boolean	implementationInvariants(CVM_Global cvm)
	{
		assert	cvm != null : new PreconditionException("cvm != null");
		return true;
	}

	@Override
	public void			deploy() throws Exception
	{
		TestScenarioWithSimulation testScenario = refrigeratorCrisisScenario();

		if (ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION.equals(
													GLOBAL_EXECUTION_MODE)) {

			long unixEpochStartTimeInMillis =
								System.currentTimeMillis() + DELAY_TO_START;

			AbstractComponent.createComponent(
				ClocksServerWithSimulation.class.getCanonicalName(),
				new Object[]{
						CLOCK_URI,
						TimeUnit.MILLISECONDS.toNanos(
										unixEpochStartTimeInMillis),
						START_INSTANT,
						ACCELERATION_FACTOR,
						DELAY_TO_START,
						SIMULATION_START_TIME,
						SIMULATION_DURATION});

			AbstractComponent.createComponent(
				GlobalSupervisor.class.getCanonicalName(),
				new Object[]{
						testScenario,
						GlobalSupervisor.SIL_SIM_ARCHITECTURE_URI
				});
			AbstractComponent.createComponent(
					CoordinatorComponent.class.getCanonicalName(),
					new Object[]{});

			AbstractComponent.createComponent(
				HEMCyPhy.class.getCanonicalName(),
				new Object[]{
						ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
						testScenario
				});

			AbstractComponent.createComponent(
				ElectricMeterCyPhy.class.getCanonicalName(),
				new Object[]{
						ElectricMeterCyPhy.REFLECTION_INBOUND_PORT_URI,
						ElectricMeterCyPhy.ELECTRIC_METER_INBOUND_PORT_URI,
						ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
						testScenario,
						ElectricMeterCyPhy.LOCAL_ARCHITECTURE_URI,
						ACCELERATION_FACTOR
				});

			AbstractComponent.createComponent(
				BatteriesCyPhy.class.getCanonicalName(),
				new Object[]{
						BatteriesCyPhy.REFLECTION_INBOUND_PORT_URI,
						BatteriesCyPhy.STANDARD_INBOUND_PORT_URI,
						BatteriesSimulationConfiguration.
												NUMBER_OF_PARALLEL_CELLS,
						BatteriesSimulationConfiguration.
												NUMBER_OF_CELL_GROUPS_IN_SERIES,
						ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
						testScenario,
						BatteriesCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
						ACCELERATION_FACTOR});

			AbstractComponent.createComponent(
				SolarPanelCyPhy.class.getCanonicalName(),
				new Object[]{
						SolarPanelCyPhy.REFLECTION_INBOUND_PORT_URI,
						SolarPanelCyPhy.STANDARD_INBOUND_PORT_URI,
						SolarPanelSimulationConfigurationI.NB_SQUARE_METERS,
						ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
						testScenario,
						SolarPanelCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
						ACCELERATION_FACTOR
				});

			AbstractComponent.createComponent(
				GeneratorCyPhy.class.getCanonicalName(),
				new Object[]{
						GeneratorCyPhy.STANDARD_INBOUND_PORT_URI,
						GeneratorCyPhy.MAX_POWER,
						GeneratorCyPhy.TANK_CAPACITY,
						GeneratorCyPhy.MIN_FUEL_CONSUMPTION,
						GeneratorCyPhy.MAX_FUEL_CONSUMPTION,
						ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
						testScenario,
						GeneratorCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
						ACCELERATION_FACTOR});


			AbstractComponent.createComponent(
				HeaterCyPhy.class.getCanonicalName(),
				new Object[]{
						HeaterCyPhy.REFLECTION_INBOUND_PORT_URI,
						HeaterCyPhy.USER_INBOUND_PORT_URI,
						HeaterCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
						HeaterCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
						HeaterCyPhy.SENSOR_INBOUND_PORT_URI,
						HeaterCyPhy.ACTUATOR_INBOUND_PORT_URI,
						ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
						testScenario,
						HeaterCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
						ACCELERATION_FACTOR
				});
			AbstractComponent.createComponent(
				HeaterController.class.getCanonicalName(),
				new Object[]{
						HeaterCyPhy.SENSOR_INBOUND_PORT_URI,
						HeaterCyPhy.ACTUATOR_INBOUND_PORT_URI,
						HeaterController.STANDARD_HYSTERESIS,
						HeaterController.STANDARD_CONTROL_PERIOD,
						ControlMode.PULL,
						ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
						ACCELERATION_FACTOR
				});

			AbstractComponent.createComponent(
				RefrigeratorCyPhy.class.getCanonicalName(),
				new Object[]{
						RefrigeratorCyPhy.REFLECTION_INBOUND_PORT_URI,
						RefrigeratorCyPhy.INBOUND_PORT_URI,
						ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
						testScenario,
						RefrigeratorCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
						ACCELERATION_FACTOR
				});

			AbstractComponent.createComponent(
				RefrigeratorTesterCyPhy.class.getCanonicalName(),
				new Object[]{
						RefrigeratorCyPhy.INBOUND_PORT_URI,
						ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
						testScenario
				});

			for (int i = 0; i < FanDeployment.FAN_COUNT; i++) {
				int fanX = 4 + (i % 2);
				int fanY = i / 2;
				String fanTitle = "Fan " + FanDeployment.FAN_INSTANCE_NAMES[i];
				AbstractComponent.createComponent(
					FanCyPhy.class.getCanonicalName(),
					new Object[]{
							FanDeployment.FAN_REFLECTION_INBOUND_PORT_URIS[i],
							FanDeployment.FAN_INBOUND_PORT_URIS[i],
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							testScenario,
							FanCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
							ACCELERATION_FACTOR,
							FanDeployment.FAN_IDS[i],
							FanDeployment.FAN_XML_DESCRIPTOR_PATHS[i],
							FanDeployment.FAN_STATE_MODEL_URIS[i],
							fanTitle,
							fanX,
							fanY
					});
			}

			AbstractComponent.createComponent(
				MultiFanUserTester.class.getCanonicalName(),
				new Object[]{
						FanDeployment.FAN_INBOUND_PORT_URIS,
						ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
						testScenario
				});
		}

		super.deploy();
	}

	public static TestScenarioWithSimulation	refrigeratorCrisisScenario()
	throws Exception
	{
		long d = TimeUnit.NANOSECONDS.toSeconds(
									TimeUtils.toNanos(SIMULATION_DURATION));
		Instant endInstant = START_INSTANT.plusSeconds(d);

		Instant refrigeratorTurnOn = START_INSTANT.plusSeconds(1200);
		Instant refrigeratorSetTarget = START_INSTANT.plusSeconds(1300);
		Instant fansTurnOn = START_INSTANT.plusSeconds(1600);
		Instant fansSetHigh = START_INSTANT.plusSeconds(1700);
		//Instant fansTurnOff = START_INSTANT.plusSeconds(1200);

		return new TestScenarioWithSimulation(
			CLOCK_URI,
			START_INSTANT,
			endInstant,
			GlobalSupervisor.SIL_SIM_ARCHITECTURE_URI,
			new Time(0.0, TimeUnit.HOURS),
			(ts, simParams) -> {
				simParams.put(
					ModelI.createRunParameterName(
						RefrigeratorElectricityModel.URI,
						RefrigeratorElectricityModel.INITIAL_TEMPERATURE_RPNAME),
					10.0);
				simParams.put(
					ModelI.createRunParameterName(
						RefrigeratorElectricityModel.URI,
						RefrigeratorElectricityModel.TARGET_TEMPERATURE_RPNAME),
					5.0);
				for (int i = 0; i < FanDeployment.FAN_COUNT; i++) {
					simParams.put(
						ModelI.createRunParameterName(
							FanDeployment.FAN_ELECTRICITY_MODEL_URIS[i],
							FanElectricitySILModel.APPLIANCE_ID_RPNAME),
						FanDeployment.FAN_IDS[i]);
				}
				simParams.put(
					ModelI.createRunParameterName(
						BatteriesPowerSILModel.URI,
						BatteriesPowerSILModel.CAPACITY_RP_NAME),
					BatteriesSimulationConfiguration.NUMBER_OF_PARALLEL_CELLS
						* BatteriesSimulationConfiguration.
											NUMBER_OF_CELL_GROUPS_IN_SERIES
							* Batteries.CAPACITY_PER_UNIT.getData());
				simParams.put(
					ModelI.createRunParameterName(
						BatteriesPowerSILModel.URI,
						BatteriesPowerSILModel.IN_POWER_RP_NAME),
					BatteriesSimulationConfiguration.NUMBER_OF_PARALLEL_CELLS
						* Batteries.IN_POWER_PER_CELL.getData());
				simParams.put(
					ModelI.createRunParameterName(
						BatteriesPowerSILModel.URI,
						BatteriesPowerSILModel.MAX_OUT_POWER_RP_NAME),
					BatteriesSimulationConfiguration.NUMBER_OF_PARALLEL_CELLS
						* Batteries.MAX_OUT_POWER_PER_CELL.getData());
				simParams.put(
					ModelI.createRunParameterName(
						BatteriesPowerSILModel.URI,
						BatteriesPowerSILModel.LEVEL_QUANTUM_RP_NAME),
					BatteriesSimulationConfiguration.
										STANDARD_LEVEL_INTEGRATION_QUANTUM);
				simParams.put(
					ModelI.createRunParameterName(
						BatteriesPowerSILModel.URI,
						BatteriesPowerSILModel.INITIAL_LEVEL_RATIO_RP_NAME),
					BatteriesSimulationConfiguration.
										INITIAL_BATTERIES_LEVEL_RATIO);
				simParams.put(
					ModelI.createRunParameterName(
						BatteriesStateSILModel.URI,
						BatteriesPowerSILModel.INITIAL_LEVEL_RATIO_RP_NAME),
					BatteriesSimulationConfiguration.
										INITIAL_BATTERIES_LEVEL_RATIO);
				simParams.put(
					ModelI.createRunParameterName(
						BatteriesUnitTesterSILModel.URI,
						BatteriesUnitTesterSILModel.TEST_SCENARIO_RP_NAME),
					ts);
				simParams.put(
					ModelI.createRunParameterName(
						DeterministicSunRiseAndSetModel.URI,
						SunRiseAndSetModelI.LATITUDE_RP_NAME),
					SolarPanelSimulationConfigurationI.LATITUDE);
				simParams.put(
					ModelI.createRunParameterName(
						DeterministicSunRiseAndSetModel.URI,
						SunRiseAndSetModelI.LONGITUDE_RP_NAME),
					SolarPanelSimulationConfigurationI.LONGITUDE);
				simParams.put(
					ModelI.createRunParameterName(
						DeterministicSunRiseAndSetModel.URI,
						SunRiseAndSetModelI.START_INSTANT_RP_NAME),
					START_INSTANT);
				simParams.put(
					ModelI.createRunParameterName(
						DeterministicSunRiseAndSetModel.URI,
						SunRiseAndSetModelI.ZONE_ID_RP_NAME),
					SolarPanelSimulationConfigurationI.ZONE);
				simParams.put(
					ModelI.createRunParameterName(
						DeterministicSunIntensityModel.URI,
						SunIntensityModelI.LATITUDE_RP_NAME),
					SolarPanelSimulationConfigurationI.LATITUDE);
				simParams.put(
					ModelI.createRunParameterName(
						DeterministicSunIntensityModel.URI,
						SunIntensityModelI.LONGITUDE_RP_NAME),
					SolarPanelSimulationConfigurationI.LONGITUDE);
				simParams.put(
					ModelI.createRunParameterName(
						DeterministicSunIntensityModel.URI,
						SunIntensityModelI.START_INSTANT_RP_NAME),
					START_INSTANT);
				simParams.put(
					ModelI.createRunParameterName(
						DeterministicSunIntensityModel.URI,
						SunIntensityModelI.ZONE_ID_RP_NAME),
					SolarPanelSimulationConfigurationI.ZONE);
				simParams.put(
					ModelI.createRunParameterName(
						DeterministicSunIntensityModel.URI,
						SunIntensityModelI.SLOPE_RP_NAME),
					SolarPanelSimulationConfigurationI.SLOPE);
				simParams.put(
					ModelI.createRunParameterName(
						DeterministicSunIntensityModel.URI,
						SunIntensityModelI.ORIENTATION_RP_NAME),
					SolarPanelSimulationConfigurationI.ORIENTATION);
				simParams.put(
					ModelI.createRunParameterName(
						DeterministicSunIntensityModel.URI,
						SunIntensityModelI.COMPUTATION_STEP_RP_NAME),
					0.25);
				simParams.put(
					ModelI.createRunParameterName(
						SolarPanelPowerSILModel.URI,
						SolarPanelPowerSILModel.LATITUDE_RP_NAME),
					SolarPanelSimulationConfigurationI.LATITUDE);
				simParams.put(
					ModelI.createRunParameterName(
						SolarPanelPowerSILModel.URI,
						SolarPanelPowerSILModel.LONGITUDE_RP_NAME),
					SolarPanelSimulationConfigurationI.LONGITUDE);
				simParams.put(
					ModelI.createRunParameterName(
						SolarPanelPowerSILModel.URI,
						SolarPanelPowerSILModel.START_INSTANT_RP_NAME),
					START_INSTANT);
				simParams.put(
					ModelI.createRunParameterName(
						SolarPanelPowerSILModel.URI,
						SolarPanelPowerSILModel.ZONE_ID_RP_NAME),
					SolarPanelSimulationConfigurationI.ZONE);
				simParams.put(
					ModelI.createRunParameterName(
						SolarPanelPowerSILModel.URI,
						SolarPanelPowerSILModel.MAX_POWER_RP_NAME),
					SolarPanelSimulationConfigurationI.NB_SQUARE_METERS *
								SolarPanel.CAPACITY_PER_SQUARE_METER.getData());
				simParams.put(
					ModelI.createRunParameterName(
						SolarPanelPowerSILModel.URI,
						SolarPanelPowerSILModel.COMPUTATION_STEP_RP_NAME),
					0.10);
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorFuelSILModel.URI,
						GeneratorFuelSILModel.CAPACITY_RP_NAME),
					GeneratorSimulationConfiguration.TANK_CAPACITY);
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorFuelSILModel.URI,
						GeneratorFuelSILModel.INITIAL_LEVEL_RP_NAME),
					GeneratorSimulationConfiguration.INITIAL_TANK_LEVEL);
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorFuelSILModel.URI,
						GeneratorFuelSILModel.MIN_FUEL_CONSUMPTION_RP_NAME),
					Generator.MIN_FUEL_CONSUMPTION.getData());
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorFuelSILModel.URI,
						GeneratorFuelSILModel.MAX_FUEL_CONSUMPTION_RP_NAME),
					Generator.MAX_FUEL_CONSUMPTION.getData());
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorFuelSILModel.URI,
						GeneratorFuelSILModel.LEVEL_QUANTUM_RP_NAME),
					GeneratorSimulationConfiguration.
										STANDARD_LEVEL_INTEGRATION_QUANTUM);
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorFuelSILModel.URI,
						GeneratorFuelSILModel.MAX_OUT_POWER_RP_NAME),
					Generator.MAX_POWER.getData());
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorPowerSILModel.URI,
						GeneratorPowerSILModel.MAX_OUT_POWER_RP_NAME),
					Generator.MAX_POWER.getData());
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorUnitTesterSILModel.URI,
						GeneratorUnitTesterSILModel.TEST_SCENARIO_RP_NAME),
					ts);
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorUnitTesterSILModel.URI,
						GeneratorUnitTesterSILModel.INITIAL_LEVEL_RP_NAME),
					GeneratorSimulationConfiguration.INITIAL_TANK_LEVEL);
			},
			new TestStepI[] {
				new TestStep(
					CLOCK_URI,
					RefrigeratorTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
					refrigeratorTurnOn,
					owner ->  {
						try {
							((RefrigeratorTesterCyPhy)owner).turnOnRefrigerator();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					RefrigeratorTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
					refrigeratorSetTarget,
					owner ->  {
						try {
							((RefrigeratorTesterCyPhy)owner).setTargetTemperature(5.0);
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					MultiFanUserTester.REFLECTION_INBOUND_PORT_URI,
					fansTurnOn,
					owner ->  {
						try {
							((MultiFanUserTester)owner).turnOnAll();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					MultiFanUserTester.REFLECTION_INBOUND_PORT_URI,
					fansSetHigh,
					owner ->  {
						try {
							((MultiFanUserTester)owner).setHighAll();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					})
				/*,
				new TestStep(
					CLOCK_URI,
					MultiFanUserTester.REFLECTION_INBOUND_PORT_URI,
					fansTurnOff,
					owner ->  {
						try {
							((MultiFanUserTester)owner).turnOffAll();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					})*/
			});
	}

	public static void	main(String[] args)
	{
		VerboseException.VERBOSE = true;
		VerboseException.PRINT_STACK_TRACE = true;
		try {
			CVM_Global cvm = new CVM_Global();
			cvm.startStandardLifeCycle(EXECUTION_DURATION);
			Thread.sleep(END_SLEEP_DURATION);
			System.exit(0);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
