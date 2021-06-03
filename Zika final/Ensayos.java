package humans_2303;

import java.io.IOException;
import java.util.ArrayList;


public class Ensayos {
	
	public static void main(String[] args) throws IOException {
		/*ArrayList<Integer> temp_list = ReadData.loadFromExcel();
		int suceptible=10;
		int exposed=3;
		int infected=3;
		int type = 0;
		int temp=temp_list.get(0);
		MyPatch patch=new MyPatch(suceptible,exposed,infected,temp,type);
		//patch.run(10, temp_list);*/
		for(int i=1; i<=10; i++) {
			ArrayList<ArrayList<Integer>> p = ReadData.loadFromExcel();
			System.out.println(p);
			System.out.println(p.size());
		}
	}
}
