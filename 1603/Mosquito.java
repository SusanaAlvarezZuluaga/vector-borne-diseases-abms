package humans_1603;

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




/**
 * @author rafaelmateus
 *
 */
public class Mosquito {
	//hold space and grid in which zombie will be located
	private ContinuousSpace <Object> space;
	private Grid <Object> grid;
	private boolean moved;
	
	//Crear metodo constructor Zombie
	public Mosquito (ContinuousSpace <Object> space, Grid <Object> grid) {
		this.space = space;
		this.grid = grid;
	}
	
	//Annotation to make the step method to be called on every iteration
	@ScheduledMethod (start = 1, interval = 1)
	public void step() {
		//get grid location
		GridPoint pt = grid.getLocation(this);
		
		//GridCellNgh -> crea GridCells para los vecinos cercanos
		GridCellNgh <Human> nghCreator = new GridCellNgh <Human>(grid, pt,
				Human.class, 1, 1);
		
		List <GridCell<Human>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		GridPoint pointWithMostHumans = null;
		int maxCount = -1;
		for (GridCell <Human> cell : gridCells) {
			if(cell.size() > maxCount) {
				pointWithMostHumans = cell.getPoint();
				maxCount = cell.size();
			}
		}
		moveTowards(pointWithMostHumans);
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
			
			moved = true;
		}
	}
	
}


