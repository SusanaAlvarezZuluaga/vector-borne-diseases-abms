package differential_equation;

public class MyPatch {
	//variables globales de todos los patches
	private double naturalEmergenceRate=0.3;
	private double deathRate=0.0714286;//1/14xs
	private double mosquitoCarryingCapacity=1000;
	private double mosquitoBiteDemand=0.5;
	private double maxBitesPerHuman=19;
	private double probabilityOfTransmissionHToM=0.333;
	public static double exposedToinfectedRate=0.1;		
	
	
	//variables de estado de los patches
	private double suceptible;
	private double exposed;
	private double infected;
		
	//constructor 	
	public MyPatch(double suceptible,double infected,double exposed) {
		this.suceptible=suceptible;
		this.exposed=exposed;
		this.infected=infected;
	}
	
	//se corre muchas veces el metodo step 
	public void run (int time) {
		for(int i=1;i<=time;i++) {
		step();
		}
	}
	//el metodo step indica lo que ocurre en cada tick
	public void step () { 
		//se obtiene un nuevo valor de suceptibles expuestos e infectados
		recalculateSEIR();
		System.out.println("El numero de suceptibles es:"+ this.suceptible);
		System.out.println("El numero de expuestos es:"+ this.exposed);
		System.out.println("El numero de infectados es:"+ this.infected);
	}
	//define el tamaño de paso que se va a utilizar en el runge kutta 
	public void recalculateSEIR() {
		double timeStep=0.1;
		solveRK4(timeStep);
	}
	
	public void solveRK4(double timeStep) {
	
		double t0=0;
		double h=timeStep;
		double tf=1;
		
		double s0=this.suceptible;
		double e0=this.exposed;
		double i0=this.infected;
		
		double birthRate=calculateBirthRate();
		double infectionRate=calculateInfectionRate();
		
		while ( t0 <=tf) {
			
			double k1s=h*suceptible_function(t0,s0,e0,i0,birthRate,infectionRate);
			double k1e=h*exposed_function(t0,s0,e0,i0,infectionRate);
			double k1i=h*infected_function(t0,s0,e0,i0);
			
			
			double k2s=h*suceptible_function(t0+h/2,s0+k1s/2,e0+k1e/2,i0+k1i/2,birthRate,infectionRate);
			double k2e=h*exposed_function(t0+h/2,s0+k1s/2,e0+k1e/2,i0+k1i/2,infectionRate);
			double k2i=h*infected_function(t0+h/2,s0+k1s/2,e0+k1e/2,i0+k1i/2);
			
			double k3s=h*suceptible_function(t0+h/2,s0+k2s/2,e0+k2e/2,i0+k2i/2,birthRate,infectionRate);
			double k3e=h*exposed_function(t0+h/2,s0+k2s/2,e0+k2e/2,i0+k2i/2,infectionRate);
			double k3i=h*infected_function(t0+h/2,s0+k2s/2,e0+k2e/2,i0+k2i/2);
			
			double k4s=h*suceptible_function(t0+h,s0+k3s,e0+k3e,i0+k3i,birthRate,infectionRate);
			double k4e=h*exposed_function(t0+h,s0+k3s,e0+k3e,i0+k3i,infectionRate);
			double k4i=h*infected_function(t0+h,s0+k3s,e0+k3e,i0+k3i);
			
			s0=s0+(k1s+2*k2s+2*k3s+k4s)/6;
			this.suceptible=s0;
			e0=e0+(k1e+2*k2e+2*k3e+k4e)/6;
			this.exposed=e0;
			i0=i0+(k1i+2*k2i+2*k3i+k4i)/6;
			this.infected=i0;
			t0=t0+h;
			
			birthRate=calculateBirthRate();
			infectionRate=calculateInfectionRate();
		 
		}
	
	}
	public double suceptible_function(double t0,double suceptible,double exposed,double infected,double birthRate, double infectionRate) {
		double s1=birthRate-infectionRate*suceptible-deathRate*suceptible;
		return s1;
	}
	
	public double exposed_function(double t0,double suceptible,double exposed,double infected,double infectionRate) {
		double e1=infectionRate*suceptible-exposedToinfectedRate*exposed-deathRate*exposed;
		return e1;
	}
	
	public double infected_function(double t0,double suceptible,double exposed,double infected) {
		double i1=exposedToinfectedRate*exposed-deathRate*infected;
		return i1;
	}
	public double calculateBirthRate() {
		double totalMosquitoes=this.suceptible+this.infected+this.exposed;
		double mosquitoPopulationGrowthRate=naturalEmergenceRate-deathRate;
		double birthRate=totalMosquitoes*(naturalEmergenceRate-mosquitoPopulationGrowthRate*totalMosquitoes/mosquitoCarryingCapacity);
		return birthRate;
	}
			
	public double calculateInfectionRate() {
		double totalHumans=calculateTotalHumansInPatch();
		double humansInfected=calculateInfectedHumansInPatch();
		double totalMosquitoes=this.suceptible+this.infected+this.exposed;
		
		double totalSuccesfulBites=(mosquitoBiteDemand*totalMosquitoes*maxBitesPerHuman*totalHumans)/(mosquitoBiteDemand*totalMosquitoes+maxBitesPerHuman*totalHumans);
		double successfulBitesPerMosquito=totalSuccesfulBites/totalMosquitoes;
		double infectionRate=successfulBitesPerMosquito*probabilityOfTransmissionHToM*(humansInfected/totalHumans);
		return infectionRate;
	}
	public double calculateTotalHumansInPatch() {	
		return 20;
	}
	
	public double calculateInfectedHumansInPatch() {	
		return 10;
	}


}
