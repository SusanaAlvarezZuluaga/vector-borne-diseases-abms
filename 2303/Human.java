package humans_2303;

import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

public class Human {

	private ContinuousSpace <Object> space; 
	private Grid <Object> grid;
	
	public Human (ContinuousSpace <Object> space, Grid <Object> grid) {
		this.space = space;
		this.grid = grid;
	}
	
	@ScheduledMethod (start = 1, interval = 1)
	public void step() {
		//get grid location
		GridPoint pt = grid.getLocation(this);
		
		//GridCellNgh -> crea GridCells para los vecinos cercanos
		GridCellNgh <Object> nghCreator = new GridCellNgh <Object>(grid, pt,
				Object.class, 1, 1);
		
		List <GridCell<Object>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		GridCell<Object> cell = gridCells.get(0); 
		
		GridPoint point_to_move = cell.getPoint();

		moveTowards(point_to_move);
		
	}
	
	public void moveTowards(GridPoint pt) {
		//moverse solamente si no se esta ya en la location del grid
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, 
					myPoint, otherPoint);
			space.moveByVector(this, 1, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
			
		}
	}
}
