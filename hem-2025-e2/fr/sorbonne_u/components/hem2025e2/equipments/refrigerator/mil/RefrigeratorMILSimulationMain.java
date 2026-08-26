package fr.sorbonne_u.components.hem2025e2.equipments.refrigerator.mil;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import fr.sorbonne_u.components.hem2025e2.equipments.refrigerator.mil.events.CompressorActive;
import fr.sorbonne_u.components.hem2025e2.equipments.refrigerator.mil.events.CompressorInactive;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RefrigeratorMILSimulationMain</code>.
 *
 * <p>Created on : 2026-01-15</p>
 *
 * @author	Softweavers
 */
public class			RefrigeratorMILSimulationMain
{
	public static void	main(String[] args)
	{
		RefrigeratorSimulationConfigurationI.staticInvariants();
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
											new HashMap<>();

			atomicModelDescriptors.put(
					RefrigeratorElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							RefrigeratorElectricityModel.class,
							RefrigeratorElectricityModel.URI,
							RefrigeratorSimulationConfigurationI.TIME_UNIT,
							null));
			atomicModelDescriptors.put(
					RefrigeratorUserModel.URI,
					AtomicModelDescriptor.create(
							RefrigeratorUserModel.class,
							RefrigeratorUserModel.URI,
							RefrigeratorSimulationConfigurationI.TIME_UNIT,
							null));

			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
											new HashMap<>();

			Set<String> submodels = new HashSet<String>();
			submodels.add(RefrigeratorElectricityModel.URI);
			submodels.add(RefrigeratorUserModel.URI);

			Map<EventSource,EventSink[]> connections =
											new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(RefrigeratorUserModel.URI,
									CompressorActive.class),
					new EventSink[] {
							new EventSink(RefrigeratorElectricityModel.URI,
										  CompressorActive.class)
					});
			connections.put(
					new EventSource(RefrigeratorUserModel.URI,
									CompressorInactive.class),
					new EventSink[] {
							new EventSink(RefrigeratorElectricityModel.URI,
										  CompressorInactive.class)
					});

			coupledModelDescriptors.put(
					RefrigeratorCoupledModel.URI,
					new CoupledModelDescriptor(
							RefrigeratorCoupledModel.class,
							RefrigeratorCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null));

			ArchitectureI architecture =
					new Architecture(
							RefrigeratorCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							RefrigeratorSimulationConfigurationI.TIME_UNIT);

			SimulatorI se = architecture.constructSimulator();
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

			se.doStandAloneSimulation(0.0, 3.0);
			SimulationReportI sr = se.getSimulatedModel().getFinalReport();
			System.out.println(sr);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
// -----------------------------------------------------------------------------
