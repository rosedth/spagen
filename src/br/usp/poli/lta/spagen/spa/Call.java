package br.usp.poli.lta.spagen.spa;

public class Call extends Symbol {

	private Machine machine;
	
	public Call(Machine machine) {
		this.machine = machine;
	}
	
	public Machine getMachine() {
		return machine;
	}
	
	@Override
	public String getLabel() {
		return this.machine.getId();
	}
	
}
