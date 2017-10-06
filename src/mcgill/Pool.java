package mcgill;

import java.util.ArrayList;
import java.util.List;

public class Pool {

	double cpuPool = 0; double memPool = 0;
	// cpu and mem are contributions from individual fogs.
	public void cPool(List<Fog> googleFogs){
		for (Fog fog : googleFogs) {
			cpuPool += fog.cpu * 0.3; // alpha = 0.3
			memPool += fog.memory * 0.3;
			
			fog.cpu = fog.cpu*0.7;
			fog.memory = fog.memory *0.7;
		}
	}
}
