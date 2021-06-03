package humans_2303;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.valueLayer.GridValueLayer;

public class Human2303 implements ContextBuilder<Object> {
	static int xdim = 32;
	static int ydim = 32; 
	static int simulationTime = 365;
	static int totalHumans = 120571;
	static int infectedHumans = 1000;
	static String filePath = "C:/Users/Asus/Documents/MAJO/Universidad/SEMESTRE 6/PRACTICA INVESTIGATIVA 1/Resultados/SimResults.csv";
	
	@Override 
	public Context <Object> build (Context <Object> context) {
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
		
		//NetworkBuilder <Object > netBuilder = new NetworkBuilder <Object > ("network", context , true);
		//netBuilder.buildNetwork();
		
		//Variables de estado de los patches (assigns values to patches)
		GridValueLayer vl0 = new GridValueLayer("Susceptible Mosquitoes", true, 
				new repast.simphony.space.grid.WrapAroundBorders(), xdim, ydim);
		
		GridValueLayer vl1 = new GridValueLayer("Exposed Mosquitoes", true, 
				new repast.simphony.space.grid.WrapAroundBorders(), xdim, ydim);
		
		GridValueLayer vl2 = new GridValueLayer("Infected Mosquitoes", true, 
				new repast.simphony.space.grid.WrapAroundBorders(), xdim, ydim);
		
		GridValueLayer vl3 = new GridValueLayer("Total Mosquitoes", true, 
				new repast.simphony.space.grid.WrapAroundBorders(), xdim, ydim);
		
		GridValueLayer vl4 = new GridValueLayer("Temperature", true, 
				new repast.simphony.space.grid.WrapAroundBorders(), xdim, ydim);
		
		GridValueLayer vl5 = new GridValueLayer("Type", true, 
				new repast.simphony.space.grid.WrapAroundBorders(), xdim, ydim);
		
		context.addValueLayer(vl0); 		
		context.addValueLayer(vl1);
		context.addValueLayer(vl2);
		context.addValueLayer(vl3);
		context.addValueLayer(vl4);
		context.addValueLayer(vl5);
		
		//Inicialización patches
		for (int i = 0; i < xdim; i++) {
			for (int j = 0; j < ydim; j++) {
				int rand_susc = RandomHelper.nextIntFromTo(0, 100); 
				int rand_exp = RandomHelper.nextIntFromTo(0, 5); 
				int rand_inf = RandomHelper.nextIntFromTo(0, 5); 
				vl0.set(rand_susc, i, j);
				vl1.set(rand_exp, i, j);
				vl2.set(rand_inf, i, j);
				int N = rand_susc + rand_exp + rand_inf;
				vl3.set(N, i, j);
				try {
					ArrayList<ArrayList<Integer>> maxMinTempLists = ReadData.loadFromExcel();//me devuelve un arraylist que contiene dos arraylist; una de temps maximas y otra de temps minimas
					int temp_max = (int) maxMinTempLists.get(0).get((int) 0);
					int temp_min = (int) maxMinTempLists.get(1).get((int) 0);
					int temperature = RandomHelper.nextIntFromTo(temp_min, temp_max);
					vl4.set(temperature, i, j);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (i<=xdim/2 && j<=ydim/2) {
					vl5.set(1, i, j);//hace referencia a zona residencial
				}
				if (i<=xdim/2 && j>ydim/2) {
					vl5.set(2, i, j);//hace referencia a la zona de estudio
				}
				if (i>xdim/2 && j<=ydim/2) {
					vl5.set(3, i, j);//hace referencia a la zona de trabajo
				}
				if (i>xdim/2 && j>ydim/2) {
					vl5.set(4, i, j);//hace referencia a zona de otras actividades
				}	
			}
		}
		
		
		//Inicializacion de los humanos susceptibles
		

		for (int i = 0; i < totalHumans-infectedHumans; i++) {
			//inicializacion de todo lo relacionado el SEIR
			String infection_state = "susceptible";
			Integer timeSinceSuccesfullBite = null;
			Integer timeSinceInfection = null;
			
			int age = ageSetter(RandomHelper.nextDoubleFromTo(0,1)); 
			
			GridPoint[] activities=activitiesLocationSetter(age) ; 
			//inicializar la casa y ubicarlo en la casa 
			GridPoint homeLocation = homeLocationSetter(); 
			Human h=new Human(space, grid, infection_state, age, timeSinceSuccesfullBite, timeSinceInfection, activities, homeLocation);
			context.add(h);
			grid.moveTo(h, homeLocation.getX(), homeLocation.getY());
			space.moveTo(h, homeLocation.getX(), homeLocation.getY());
		}
		
		
		//Inicializacion de los humanos infectados
		for (int i = 0; i < infectedHumans; i++) {
		int age = ageSetter(RandomHelper.nextDoubleFromTo(0,1)); 
		String infection_state = "infected";
		Integer timeSinceSuccesfullBite = 0;
		Integer timeSinceInfection = 0;
		
		GridPoint[] activities=activitiesLocationSetter(age) ;
		
		
		GridPoint homeLocation=homeLocationSetter(); //falta esta
		
		Human h=new Human(space, grid, infection_state, age, timeSinceSuccesfullBite, timeSinceInfection, activities, homeLocation);
		context.add(h);
		
		//context.add(new Human(space, grid, infection_state, age, timeSinceSuccesfullBite, timeSinceInfection, activities, homeLocation));
		
		grid.moveTo(h, homeLocation.getX(), homeLocation.getY());
		space.moveTo(h, homeLocation.getX(), homeLocation.getY());
		//NdPoint pt = space.getLocation(homeLocation);
		//grid.moveTo(h, (int)pt.getX(), (int)pt.getY());
		}
		 
		/*
		for (Object obj : context) {

			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int)pt.getX(), (int)pt.getY());
		}
		*/
		
		
		//String fileName1 = "/Users/rafaelmateus/Library/Mobile Documents/com~apple~CloudDocs/2021-1/PI/Resultados/"; 
		//String fileName2 = "SimResults.csv";
		//String filePath = fileName1 + fileName2;
		
		FileWriter csvWriter;
		try {
			csvWriter = new FileWriter(filePath);
			csvWriter.append("Tick");
			csvWriter.append(",");
			csvWriter.append("Total Humans");
			csvWriter.append(",");

			csvWriter.append("Susceptible");
			csvWriter.append(",");
			csvWriter.append("Exposed");
			csvWriter.append(",");			
			csvWriter.append("Infected");
			csvWriter.append(",");
			csvWriter.append("Recovered");			
			csvWriter.append("\n");
			
			csvWriter.flush();
			csvWriter.close();

		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		
		RunEnvironment.getInstance().endAt(simulationTime);
		return context; 
		
		
	
	}
	
	//Edades de la poblacion
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
	
	public static GridPoint homeLocationSetter() {
		int xCasa=RandomHelper.nextIntFromTo(0,xdim/2);
		int YCasa=RandomHelper.nextIntFromTo(0,ydim/2);
		GridPoint casa=new GridPoint(xCasa,YCasa);
		return casa;
	}
	

	
	public static GridPoint[] activitiesLocationSetter(int age) {
		int xOtro=RandomHelper.nextIntFromTo(xdim/2,xdim);
		int YOtro=RandomHelper.nextIntFromTo(ydim/2, ydim);
		GridPoint otro=new GridPoint(xOtro,YOtro);
		
		GridPoint estudioOTrabajo=null;
		
		if (age<=24){
		int xEstudioOTrabajo=RandomHelper.nextIntFromTo(0, xdim/2);
		int yEstudioOTrabajo=RandomHelper.nextIntFromTo(ydim/2, ydim);
		estudioOTrabajo=new GridPoint(xEstudioOTrabajo,yEstudioOTrabajo);
		}
		
		if (age>24){
			int xEstudioOTrabajo=RandomHelper.nextIntFromTo(xdim/2,xdim);
			int yEstudioOTrabajo=RandomHelper.nextIntFromTo(0, ydim/2);
			estudioOTrabajo=new GridPoint(xEstudioOTrabajo,yEstudioOTrabajo);
		}
		GridPoint[] activitiesCoordinates=new GridPoint[]{estudioOTrabajo,otro};
		return activitiesCoordinates;
	}
	
}