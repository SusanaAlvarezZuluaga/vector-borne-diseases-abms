package humans_2303;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.valueLayer.GridValueLayer;

public class Human2303 implements ContextBuilder<Object> {
	
	@Override 
	public Context <Object> build (Context <Object> context){
		//int xdim = 5000;
		//int ydim = 5000; 
		int xdim = 50;
		int ydim = 50; 
		context.setId("humans_2303");
		
		
		//create space world
		ContinuousSpaceFactory spaceFactory = 
				ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace <Object> space = 
				spaceFactory.createContinuousSpace("space", context, 
						new RandomCartesianAdder <Object>(),
						new repast.simphony.space.continuous.WrapAroundBorders(),
						xdim, ydim); 
		
		//create grid world
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid <Object> grid = gridFactory.createGrid("grid", context, 
				new GridBuilderParameters <Object> (new WrapAroundBorders(), 
						new SimpleGridAdder <Object>(), 
						true, xdim, ydim)); 
		
		NetworkBuilder <Object > netBuilder = new NetworkBuilder <Object > ("network", context , true);
		netBuilder.buildNetwork();
		
		
		
		//assigns values to patches
		GridValueLayer vl0 = new GridValueLayer("Susceptible", true, 
				new repast.simphony.space.grid.WrapAroundBorders(), xdim, ydim);
		
		GridValueLayer vl1 = new GridValueLayer("Exposed", true, 
				new repast.simphony.space.grid.WrapAroundBorders(), xdim, ydim);
		
		GridValueLayer vl2 = new GridValueLayer("Infected", true, 
				new repast.simphony.space.grid.WrapAroundBorders(), xdim, ydim);
		
		context.addValueLayer(vl0); 		
		context.addValueLayer(vl1);
		context.addValueLayer(vl2);

		
		for (int i=0; i < xdim; i++) {
			for (int j=0; j< ydim; j++) {
				int suceptible=10;
				int exposed=3;
				int infected=3;
				MyPatch patch=new MyPatch(suceptible,exposed,infected);
				patch.run(10);
				vl0.set(patch.suceptible, i, j);
				vl1.set(patch.exposed, i, j);
				vl2.set(patch.infected, i, j);
			}
		}
		
		//int humanCount = 482286;
		int humanCount = 100;
		for (int i = 0; i < humanCount-1; i++) {
			//int age = RandomHelper.nextIntFromTo(3, 80);
			int age = ageSetter(RandomHelper.nextDoubleFromTo(0,1)); 
			String infection_state = "S";
			context.add(new Human(space, grid, age, infection_state));
		}
		
		//Humano que inicializa
		int age = ageSetter(RandomHelper.nextDoubleFromTo(0,1)); 
		String infection_state = "I";
		context.add(new Human(space, grid, age, infection_state));
		
		
		for (Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int)pt.getX(), (int)pt.getY());
		}
		
		return context; 
	}
	
	
	public static int ageSetter(double prob) {
		if (0.0811 <= prob && prob < 0.1615) {
			return RandomHelper.nextIntFromTo(5, 9);
		} else if (0.1615 <= prob && prob < 0.2435) {
			return RandomHelper.nextIntFromTo(10, 14);
		} else if (0.2435 <= prob && prob < 0.3292) {
			return RandomHelper.nextIntFromTo(15, 19);
		} else if (0.3292 <= prob && prob < 0.4249) {
			return RandomHelper.nextIntFromTo(20, 24);
		} else if (0.4249 <= prob && prob < 0.5196) {
			return RandomHelper.nextIntFromTo(25, 29);
		} else if (0.5196 <= prob && prob < 0.6025) {
			return RandomHelper.nextIntFromTo(30, 34);
		} else if (0.6025 <= prob && prob < 0.6801) {
			return RandomHelper.nextIntFromTo(35, 39);
		} else if (0.6801 <= prob && prob < 0.7506) {
			return RandomHelper.nextIntFromTo(40, 44);
		} else if (0.7506 <= prob && prob < 0.8097) {
			return RandomHelper.nextIntFromTo(45, 49);
		} else if (0.8097 <= prob && prob < 0.8626) {
			return RandomHelper.nextIntFromTo(50, 54);
		} else if (0.8626 <= prob && prob < 0.9062) {
			return RandomHelper.nextIntFromTo(55, 59);
		} else if (0.9062 <= prob && prob < 0.9378) {
			return RandomHelper.nextIntFromTo(60, 64);
		} else if (0.9378 <= prob && prob < 0.9601) {
			return RandomHelper.nextIntFromTo(65, 59);
		} else if (0.9601 <= prob && prob < 0.9768) {
			return RandomHelper.nextIntFromTo(70, 74);
		} else if (0.9768 <= prob && prob < 0.9890) {
			return RandomHelper.nextIntFromTo(75, 79);
		} else if (0.9890 <= prob && prob <= 1) {
			return RandomHelper.nextIntFromTo(80, 90);
		} else {
			return RandomHelper.nextIntFromTo(0, 4);
		}
		
	}
	
}
