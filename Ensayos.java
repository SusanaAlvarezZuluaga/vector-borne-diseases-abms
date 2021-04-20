package differential_equation;

public class Ensayos {
	
	public static void main(String[] args) {
		int suceptible=10;
		int exposed=3;
		int infected=3;
		MyPatch patch=new MyPatch(suceptible,exposed,infected);
		patch.run(10);
	}
}
