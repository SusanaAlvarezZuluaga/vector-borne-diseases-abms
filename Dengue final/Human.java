package humans_2303;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.valueLayer.GridValueLayer;

public class Human {
	
	private ContinuousSpace <Object> space;
	private Grid <Object> grid;	
	int xdim = Human2303.xdim;
	int ydim = Human2303.ydim;
	//variables globales
	private static double naturalEmergenceRate=0.3;
	private static double deathRate=0.071428571428571;
	private static double mosquitoCarryingCapacity=1000;
	private static double mosquitoBiteDemand=0.5;
	private static double maxBitesPerHuman=19;
	private static double probabilityOfTransmissionHToM=0.333;
	
	private static double probabilityOfTransmissionMToH=0.333;
	public static ArrayList<ArrayList<Integer>> maxMinTempLists ;
	
	//se crea una distribucion weibull para generar el tiempo de incubacion 
	GammaDistribution distTiempoIncubacion = new GammaDistribution(5.5, 1.12);
	//se pone la duracion del tiempo de infeccion como una dist uniforme entre 3-7 dias
	//se crea la distribucion uniforme para generar el tiempo de infeccion 
	UniformRealDistribution distTiempoInfeccion = new UniformRealDistribution(2, 7.1);
			
			
	//variables de estado de los humanos
	private String infectionState;// susceptible,exposed,infected,recovered
	private int age;
	private Integer timeSinceSuccesfullBite;
	private Integer timeSinceInfection;
	private GridPoint[] activities;
	private GridPoint homeLocation;
	
	//constructor 
	public Human(ContinuousSpace <Object> space, Grid <Object> grid, String infectionState, int age, Integer timeSinceSuccesfullBite, Integer timeSinceInfection, GridPoint[] activities, GridPoint homeLocation){ 
		this.space = space;
		this.grid = grid;	
		this.setInfectionState(infectionState);
		this.setAge(age);
		this.setTimeSinceSuccesfullBite(timeSinceSuccesfullBite);
		this.setTimeSinceInfection(timeSinceInfection);
		this.setActivities(activities);
		this.setHome(homeLocation);	
	}
	
	
	@ScheduledMethod (start = 1, pick = 1, priority = 100)
	public void step1() throws IOException {;
		maxMinTempLists= ReadData.loadFromExcel();
			
	}
	
	@ScheduledMethod (start = 1, pick = 1, interval = 1, priority = 80)
	public void writeCsv() {
		//Set file name
		
		String filePath = Human2303.filePath;
		
		File f = new File(filePath);
		if(f.exists() && !f.isDirectory()) { 
			
			FileWriter csvWriter;
			try {
			
				csvWriter = new FileWriter(filePath, true);
				
				double tick = RepastEssentials.GetTickCount();
				int tick_int = (int) tick; 
				csvWriter.append(String.valueOf(tick_int));
				csvWriter.append(",");
				
				csvWriter.append(String.valueOf(Human2303.totalHumans));
				csvWriter.append(",");
				
				//Susceptible
				csvWriter.append(String.valueOf(countTotalSusceptible()));
				csvWriter.append(",");
				
				//Exposed
				csvWriter.append(String.valueOf(countTotalExposed()));
				csvWriter.append(",");
				
				//Infected
				csvWriter.append(String.valueOf(countTotalInfected()));
				csvWriter.append(",");
				
				//Recovered
				csvWriter.append(String.valueOf(countTotalRecovered()));
				csvWriter.append("\n");
				
				csvWriter.flush();
				csvWriter.close();
			
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		} else {

			FileWriter csvWriter;
			try {
				csvWriter = new FileWriter(filePath);
				csvWriter.append("Tick");
				csvWriter.append(",");
				csvWriter.append("Total Humans");
				csvWriter.append(",");
				csvWriter.append("Infected");
				csvWriter.append(",");
				csvWriter.append("Susceptible");
				csvWriter.append(",");
				csvWriter.append("Exposed");
				csvWriter.append("\n");
				
				csvWriter.flush();
				csvWriter.close();

			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
	

	}
	
	//actualizacion de los patches en cada tick
	@ScheduledMethod (start = 1, interval = 1, pick = 1, priority = 40)
	public void step2() throws IOException {
		double tick = RepastEssentials.GetTickCount();
		for (int i = 0; i < xdim; i++) {
			for (int j = 0; j < ydim; j++) {
				//actualizo la temperatura con la temepratura del nuevo dia
				int temp_max = (int) maxMinTempLists.get(0).get((int) tick);
				int temp_min = (int) maxMinTempLists.get(1).get((int) tick);
				int temperature=RandomHelper.nextIntFromTo(temp_min, temp_max);
				GridValueLayer vl4= (GridValueLayer) ContextUtils.getContext(this).getValueLayer("Temperature");
				vl4.set(temperature,i,j);
				 
				//cojo el valor de mosquitos suceptibles, infectados y expuestos del dia anterior que utilizare para calcular el de este dia 
				double susceptible = (double) ContextUtils.getContext(this).getValueLayer("Susceptible Mosquitoes").get(i,j);
				double exposed = (double) ContextUtils.getContext(this).getValueLayer("Exposed Mosquitoes").get(i,j);
				double infected = (double) ContextUtils.getContext(this).getValueLayer("Infected Mosquitoes").get(i,j);
					
				//cojo el tipo de patch para poder crearlo 	
				int type = (int) ContextUtils.getContext(this).getValueLayer("Type").get(i,j);
					
				// se crea un objeto tipo patch con la temperatura de este dia 
				MyPatch patch=new MyPatch(susceptible, exposed, infected, temperature,type);
					
				//se recalcula el SEIR y entonces se recalculan las primeras 4 layers 
				double []ActualizedSEIR=patch.step(tick,i,j,this.grid);
					
	            GridValueLayer vl0= (GridValueLayer) ContextUtils.getContext(this).getValueLayer("Susceptible Mosquitoes");
	            vl0.set(ActualizedSEIR[0],i,j);
	            GridValueLayer vl1= (GridValueLayer) ContextUtils.getContext(this).getValueLayer("Exposed Mosquitoes");
	            vl1.set(ActualizedSEIR[1],i,j);
	            GridValueLayer vl2= (GridValueLayer) ContextUtils.getContext(this).getValueLayer("Infected Mosquitoes");
	            vl2.set(ActualizedSEIR[2],i,j);
	            GridValueLayer vl3= (GridValueLayer) ContextUtils.getContext(this).getValueLayer("Total Mosquitoes");
	            vl3.set(ActualizedSEIR[3],i,j);
	       }
		}
	}
	
	
	
	@ScheduledMethod (start = 1, interval = 1, priority = 1)
	// correr cada humano
	public void step3() throws IOException {
		double tick = RepastEssentials.GetTickCount();
		//System.out.println(this.activities[0]);
		//System.out.println(this.activities[1]);
		//Movimineto humano
		for(GridPoint p:this.activities) {
			grid.moveTo(this,p.getX(),p.getY());
			space.moveTo(this,p.getX(),p.getY());
			actualizeSEIRStatus();//humano
			int i = p.getX();
			int j = p.getY();
			double susceptible = (double) ContextUtils.getContext(this).getValueLayer("Susceptible Mosquitoes").get(i,j);
			double exposed = (double) ContextUtils.getContext(this).getValueLayer("Exposed Mosquitoes").get(i,j);
			double infected = (double) ContextUtils.getContext(this).getValueLayer("Infected Mosquitoes").get(i,j);
			int temp = (int) ContextUtils.getContext(this).getValueLayer("Temperature").get(i,j);
			int type = (int) ContextUtils.getContext(this).getValueLayer("Type").get(i,j);
			
			// se crea un objeto tipo patch
			MyPatch patch=new MyPatch(susceptible, exposed, infected, temp,type);

			//se recalcula el SEIR la temperatura no cambia porque estamos en el mismo dia 
			double []ActualizedSEIR=patch.step(tick,i,j,this.grid);
            GridValueLayer vl0= (GridValueLayer) ContextUtils.getContext(this).getValueLayer("Susceptible Mosquitoes");
            vl0.set(ActualizedSEIR[0],i,j);
            GridValueLayer vl1= (GridValueLayer) ContextUtils.getContext(this).getValueLayer("Exposed Mosquitoes");
            vl1.set(ActualizedSEIR[1],i,j);
            GridValueLayer vl2= (GridValueLayer) ContextUtils.getContext(this).getValueLayer("Infected Mosquitoes");
            vl2.set(ActualizedSEIR[2],i,j);
            GridValueLayer vl3= (GridValueLayer) ContextUtils.getContext(this).getValueLayer("Total Mosquitoes");
            vl3.set(ActualizedSEIR[3],i,j);
		}
		grid.moveTo(this,this.homeLocation.getX(),this.homeLocation.getY());
		space.moveTo(this,this.homeLocation.getX(),this.homeLocation.getY());
		actualizeTimes();
	}
	
	
	

	
	//Metodo para actualizar los tiempos de infeccion y de incubacion
	public void actualizeTimes(){
		//solo se le suma un dia si esta es diferente de nula
		if (this.timeSinceSuccesfullBite != null) {
			timeSinceSuccesfullBite = timeSinceSuccesfullBite + 1;
		}
		//solo se le suma un dia si esta es diferente de nula
		if (this.timeSinceInfection != null) {
			timeSinceInfection = timeSinceInfection + 1;
		}
	}
	 
	public int countHumansInPatch() {
		GridPoint pt = grid.getLocation(this);
		Iterable<Object> HumansinP=grid.getObjectsAt(pt.getX(),pt.getY());
		int contH=0;
		for(Object h:HumansinP) {
			contH=contH+1;
		}
		return contH;
    }
	 
	 public int countTotalSusceptible() {
		 int S = 0;
		 for (Object obj: grid.getObjects()) {
			 Human h = (Human)obj;
			 String estado = h.infectionState; 
			 if (estado == "susceptible") {
				S += 1;
			}
		 }
		 return S;
	 }
	 
	 public int countTotalExposed() {
		 int E = 0;
		 for (Object obj: grid.getObjects()) {
			 Human h = (Human)obj;
			 String estado = h.infectionState; 
			 if (estado == "exposed") {
				E += 1;
			}
		 }
		 return E;
	 }
	 
	 public int countTotalInfected() {
		 int I = 0;
		 for (Object obj: grid.getObjects()) {
			 Human h = (Human)obj;
			 String estado = h.infectionState; 
			 if (estado == "infected") {
				I += 1;
			}
		 }
		 return I;
	 }
	 
	 public int countTotalRecovered() {
		 int R = 0;
		 for (Object obj: grid.getObjects()) {
			 Human h = (Human)obj;
			 String estado = h.infectionState; 
			 if (estado == "recovered") {
				R += 1;
			}
		 }
		 return R;
	 }
	 
	public double calculateInfectionProbabilityHuman() {
			GridPoint pt = grid.getLocation(this);
	        int x=pt.getX();
	        int y=pt.getY();

	        //obtengo el numero de mosquitos de cada tipo del patch en el que estoy parada
	        double susceptibleMosquitoes = (double) ContextUtils.getContext(this).getValueLayer("Susceptible Mosquitoes").get(x,y);
	        double exposedMosquitoes = (double) ContextUtils.getContext(this).getValueLayer("Exposed Mosquitoes").get(x,y);
	        double infectedMosquitoes = (double) ContextUtils.getContext(this).getValueLayer("Infected Mosquitoes").get(x,y);

	        //obtengo el numero de humanos que hay en el patch que estoy parada
	        double totalHumans=countHumansInPatch();
	        
	        //contar cuantos humanos hay en el patch que estoy parada
	        double totalMosquitoes=susceptibleMosquitoes+exposedMosquitoes+infectedMosquitoes;

	        
	        double successfulBitesPerHuman=0;
	        if (totalHumans>0){
		        double totalSuccesfulBites=(mosquitoBiteDemand*totalMosquitoes*maxBitesPerHuman*totalHumans)/(mosquitoBiteDemand*totalMosquitoes+maxBitesPerHuman*totalHumans);
		        successfulBitesPerHuman=totalSuccesfulBites/totalHumans;
	        }

	        double infectionRateHumans=probabilityOfTransmissionMToH*successfulBitesPerHuman*infectedMosquitoes/totalMosquitoes;

	        double humanInfectionProbability=1-Math.exp(-infectionRateHumans);

	        return humanInfectionProbability;
	    }
		
	
	
	//Metodo para actualizar variable de estado SEIR
	 public void actualizeSEIRStatus() {
		 //si la persona esta en estado suceptible se calcula la probabilidad de pasar a infectado y se determina si pasa a infectado o no
		 if(this.infectionState == "susceptible") {
			 //hallar la probabilidad de infeccion de este patch en el que el humano esta parado        
		     double probabilityIOfInfectionHuman=calculateInfectionProbabilityHuman();
		     if(Math.random() <= probabilityIOfInfectionHuman) {
		    	 this.infectionState = "exposed";
		    	 this.timeSinceSuccesfullBite = 0;    
		     }
		 }

			
		//si la persona esta en estado expuesto se determina si pasa a infectado 
		if (this.infectionState == "exposed") {
			//Hallemos la siguiente probabilidad p(tiempoDeIncubacion<=tiempoDesdeMordidaExitosa)
			double acumProb = distTiempoIncubacion.cumulativeProbability(this.timeSinceSuccesfullBite);
			if (Math.random() <= acumProb) {
				this.infectionState = "infected";
				this.timeSinceInfection = 0;
			}
		}
		
		//si la persona esta infectada se determina si pasa a recuperado
		if(this.infectionState == "infected") {
			double acumProb = distTiempoInfeccion.cumulativeProbability(this.timeSinceInfection);
			if (Math.random() <= acumProb) {
				this.infectionState = "recovered";
			}	
		}	
	}

	
	//set edades
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

	
	//set estado de infeccion
	public String getInfectionState() {
		return infectionState;
	}
	public void setInfectionState(String infectionState) {
		this.infectionState = infectionState;
	}
	
	
	//set tiempo desde mordida
	public Integer getTimeSinceSuccesfullBite() {
		return timeSinceSuccesfullBite;
	}
	public void setTimeSinceSuccesfullBite(Integer timeSinceSuccesfullBite) {
		this.timeSinceSuccesfullBite = timeSinceSuccesfullBite;
	}
	
	
	//set tiempo de infeccion
	public Integer getTimeSinceInfection() {
		return timeSinceInfection;
	}
	public void setTimeSinceInfection(Integer timeSinceInfection) {
		this.timeSinceInfection = timeSinceInfection;
	}
	
	//set activities
	public GridPoint[] getActivities() {
		return activities;
	}
	public void setActivities(GridPoint[] activities) {
		this.activities = activities;
	}
	
	
	//set home location
	public GridPoint getHome() {
		return homeLocation;
	}
	public void setHome(GridPoint homeLocation) {
		this.homeLocation = homeLocation;
	}
}