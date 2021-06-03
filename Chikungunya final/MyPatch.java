package humans_2303;

import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
//import repast.simphony.context.Context;
//import repast.simphony.space.grid.Grid;
//import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.valueLayer.GridValueLayer;

public class MyPatch {

	//variables globales(son iguales para cada patch)
	private double naturalEmergenceRate=0.3;
	private double deathRate=0.071428571428571;
	private double mosquitoCarryingCapacity=1000;
	private double mosquitoBiteDemand=0.5;
	private double maxBitesPerHuman=19;
	private double probabilityOfTransmissionHToM=0.333;
	//otras variables globales 
	private double probabilityOfTransmissionMToH=0.333;
		
	//variables de estado de los patches (diferentes en cada patch)
	private double suceptibleMosquitoes;
	private double exposedMosquitoes;
	private double infectedMosquitoes;
	private double temperaturePatch;
	private int type;
			
	//constructor 	
	public MyPatch(double suceptibleMosquitoes,double exposedMosquitoes,double infectedMosquitoes,double temperaturePatch, int type) {
		this.suceptibleMosquitoes=suceptibleMosquitoes;
		this.exposedMosquitoes=exposedMosquitoes;
		this.infectedMosquitoes=infectedMosquitoes;
		this.temperaturePatch=temperaturePatch;
		this.type=type;
	}

	
	//el metodo step indica lo que ocurre en cada tick
	public double[]step(double time, int x, int y, Grid <Object> grid) { 
		//se obtiene un nuevo valor de suceptibles expuestos e infectados
		recalculateSEIR(x, y, grid);
		System.out.println("La temperatura es: "+ this.temperaturePatch);
		//System.out.println(temp_list.size());
		double susc = this.suceptibleMosquitoes;
		double exp = this.exposedMosquitoes;
		double inf = this.infectedMosquitoes;
		System.out.println("El numero de suceptibles es: "+ susc);
		System.out.println("El numero de expuestos es: "+ exp);
		System.out.println("El numero de infectados es: "+ inf);
		double total = susc + exp + inf;
		double []resp=new double[]{susc,exp,inf,total};
		return resp;
	}
	
	//define el tamaño de paso que se va a utilizar en el runge kutta 
	public void recalculateSEIR(int x, int y,Grid <Object> grid) {
		double timeStep=0.1;
		solveRK4(timeStep, x, y, grid);
	}
	
	public void solveRK4(double h, int x, int y,Grid <Object> grid) {
		
		double s0=this.suceptibleMosquitoes;
		double e0=this.exposedMosquitoes;
		double i0=this.infectedMosquitoes;
		
		double birthRate=calculateBirthRate();
		double infectionRate=calculateInfectionRate(x,y,grid);
		double niter=1/h;
		int iter=1;
		while (iter<=niter) {
			double k1i=h*infected_function(s0,e0,i0);
			double k1e=h*exposed_function(s0,e0,i0,infectionRate);
			double k1s=h*suceptible_function(s0,e0,i0,birthRate,infectionRate);
			
			
			
			double k2i=h*infected_function(s0+k1s/2,e0+k1e/2,i0+k1i/2);
			double k2e=h*exposed_function(s0+k1s/2,e0+k1e/2,i0+k1i/2,infectionRate);
			double k2s=h*suceptible_function(s0+k1s/2,e0+k1e/2,i0+k1i/2,birthRate,infectionRate);
		
			
			double k3i=h*infected_function(s0+k2s/2,e0+k2e/2,i0+k2i/2);
			double k3e=h*exposed_function(s0+k2s/2,e0+k2e/2,i0+k2i/2,infectionRate);
			double k3s=h*suceptible_function(s0+k2s/2,e0+k2e/2,i0+k2i/2,birthRate,infectionRate);
			
			double k4i=h*infected_function(s0+k3s,e0+k3e,i0+k3i);
			double k4e=h*exposed_function(s0+k3s,e0+k3e,i0+k3i,infectionRate);
			double k4s=h*suceptible_function(s0+k3s,e0+k3e,i0+k3i,birthRate,infectionRate);
		
			
			i0=i0+(k1i+2*k2i+2*k3i+k4i)/6;
			this.infectedMosquitoes=i0;
	
			
			e0=e0+(k1e+2*k2e+2*k3e+k4e)/6;
			this.exposedMosquitoes=e0;
			
			s0=s0+(k1s+2*k2s+2*k3s+k4s)/6;
			this.suceptibleMosquitoes=s0;
			
			
			birthRate=calculateBirthRate();
			infectionRate=calculateInfectionRate(x,y,grid);
			iter=iter+1;
		}
	
	}
	
	public double suceptible_function(double suceptible,double exposed,double infected,double birthRate, double infectionRate) {
		double s1=birthRate-infectionRate*suceptible-deathRate*suceptible;
		return s1;
	}
	
	public double exposed_function(double suceptible,double exposed,double infected,double infectionRate) {
		double exposedToinfectedRate=calculateExposedToinfectedRate();
		double e1=infectionRate*suceptible-exposedToinfectedRate*exposed-deathRate*exposed;
		return e1;
	}
	
	public double infected_function(double suceptible,double exposed,double infected) {
		double exposedToinfectedRate=calculateExposedToinfectedRate();
		double i1=exposedToinfectedRate*exposed-deathRate*infected;
		return i1;
	}
	
	public double calculateBirthRate() {
		double totalMosquitoes=this.suceptibleMosquitoes+this.infectedMosquitoes+this.exposedMosquitoes;
		double mosquitoPopulationGrowthRate=naturalEmergenceRate-deathRate;
		double birthRate=totalMosquitoes*(naturalEmergenceRate-mosquitoPopulationGrowthRate*totalMosquitoes/mosquitoCarryingCapacity);
		return birthRate;
	}
			
	public double calculateInfectionRate(int x, int y,Grid <Object> grid) {
		double totalHumans=calculateTotalHumansInPatch(x, y, grid);
		double humansInfected=calculateInfectedHumansInPatch(x, y,grid);
		double totalMosquitoes=this.suceptibleMosquitoes+this.infectedMosquitoes+this.exposedMosquitoes;
		
		double totalSuccesfulBites=(mosquitoBiteDemand*totalMosquitoes*maxBitesPerHuman*totalHumans)/(mosquitoBiteDemand*totalMosquitoes+maxBitesPerHuman*totalHumans);
		double successfulBitesPerMosquito=totalSuccesfulBites/totalMosquitoes;
		double infectionRateMosquitoes=0;
		if (totalHumans>0) {
		infectionRateMosquitoes=successfulBitesPerMosquito*probabilityOfTransmissionHToM*(humansInfected/totalHumans);
		}
		return infectionRateMosquitoes;
	}
	
	public double calculateExposedToinfectedRate() {
		double exposedToInfectedRate;
		if (this.temperaturePatch<12) {
			exposedToInfectedRate=0;
		}
		else {
			double patchIncubationPeriod=4+Math.exp(5.15-0.123*this.temperaturePatch);
			exposedToInfectedRate=1/patchIncubationPeriod;
		}
		return exposedToInfectedRate;
	}
	
	// se cuenta la cantidad de humanos en el patch
	public int calculateTotalHumansInPatch(int x, int y,Grid <Object> grid) {	
		GridPoint pt =new GridPoint(x,y);
		Iterable<Object> HumansinP=grid.getObjectsAt(x,y);
		int contH=0;
		for (Object h:HumansinP) {
			contH=contH+1;
		}
		return contH;
	}
	
	// se cuenta la cantidad de humanos infectados en el patch
	public double calculateInfectedHumansInPatch(int x, int y,Grid <Object> grid) {
		GridPoint pt =new GridPoint(x,y);
		Iterable<Object> HumansinP=grid.getObjectsAt(x,y);
		int contInf=0;
		for (Object h:HumansinP) {
			Human h1=(Human)h;
			if(h1.getInfectionState()=="infected"){
				contInf=contInf+1; 
			} 
		}
		return contInf;
	}
	
	public int getPatchTemperature(ArrayList<Integer> temp_list, double time) {
		Double t = time;
		int new_time = t.intValue();
		int temp = temp_list.get(new_time);
		return temp;
	}
}
