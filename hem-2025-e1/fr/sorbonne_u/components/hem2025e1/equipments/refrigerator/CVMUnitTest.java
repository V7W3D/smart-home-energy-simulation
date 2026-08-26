package fr.sorbonne_u.components.hem2025e1.equipments.refrigerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

/**
 * The class <code>CVMUnitTest</code> deploys and runs a unit test for the
 * <code>Refrigerator</code> component.
 *
 * <p>Created on : 2025-12-27</p>
 * 
 * @author	Softweavers
 */
public class			CVMUnitTest
extends		AbstractCVM
{
	public				CVMUnitTest() throws Exception
	{
		super();
	}

	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				Refrigerator.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				RefrigeratorTester.class.getCanonicalName(),
				new Object[]{true});

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVMUnitTest c = new CVMUnitTest();
			c.startStandardLifeCycle(10000L);
			Thread.sleep(1000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
