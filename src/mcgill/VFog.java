package mcgill;

public class VFog {
	String id;
	private Fog f;
	private Pool p;
	private Cloud c;
	
	public VFog(String ID, Fog fog, Pool pool, Cloud cloud){
		this.id = ID;
		this.f = fog;
		this.p = pool;
		this.c = cloud;
	}
}
