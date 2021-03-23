package humans_2303;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
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
		
		//assigns values to patches
		double deflt_val = 10;
		GridValueLayer vl = new GridValueLayer("Patches field", deflt_val, true, 
				new repast.simphony.space.grid.WrapAroundBorders(), xdim, ydim);
		
		context.addValueLayer(vl);
		
		for (int i = 0; i < xdim; i++) {
			for (int j = 0; j < xdim; j++) {
				vl.set(RandomHelper.nextDoubleFromTo(0.2, 0.7), i, j);
			}
		}
		
		
		int humanCount = 25;
		for (int i = 0; i < humanCount; i++) {
			context.add(new Human(space, grid));
		}
		
		for (Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int)pt.getX(), (int)pt.getY());
		}
		
		return context; 
	}
}
