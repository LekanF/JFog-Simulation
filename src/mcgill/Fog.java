package mcgill;

import umontreal.iro.lecuyer.simprocs.*;
public class Fog {
	Integer id;
	String zone_id;
	Double longitude, latitude, cpu, memory;
	Fog fog;
	Resource jresource;
	Util.UtilizationMap utilise; //= new Util.UtilizationMap();
	
	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public boolean isAvailable(){
		// Check if it can handle the task, by checking its availability with current run tasks
		return true;
	}
	public Fog(int ID, double lng, double lat){
		this.id= ID;
		this.longitude = lng;
		this.latitude = lat;
	}
	
	public Fog(Integer ID, Fog f, double c, double mem, Resource r ){
		this.id = ID;
		this.fog = f;
		this.cpu = c;
		this.memory = mem;
	}
	
	public Fog(Integer ID, Fog f, double c, double mem){
		this.id = ID;
		this.fog = f;
		this.cpu = c;
		this.memory = mem;
//		jresource = new Resource((Integer)mem.intValue(), ID.toString());
//		jresource = new Resource(100, ID.toString());
		jresource = new Resource(this.cpu.intValue(), ID.toString());
		utilise = new Util.UtilizationMap();
	}
	
	public int getCapacity(){
		return this.cpu.intValue();
		
	}
//	public void acceptRequest(Device r){
//		r = new Device();
//		if (r.getZone_id().equals(zone_id)){
//			processRequest();
//		}
//	}
	
	public String toString()
	{
		return this.id.toString() + " " + this.longitude.toString() + " " + this.latitude.toString();
	}
	
	public String toGString()
	{
		return "Fog capacities:  " + this.fog.toString() + " " + this.cpu.toString() +" " + this.memory.toString();
	}
	private void processRequest() {
		// TODO Auto-generated method stub
		
	}
}
