package com.github.isatwospirit.kittyslilhelpers.command.createmaze;

public class Labyrinth {
	private LabyrinthCell[][] cells;
	
	public int getSizeNS(){
		try{
			return this.cells[0].length;
		}catch(Exception e){
			return 0;
		}
	}
	public int getSizeWE(){
		return this.cells.length;
	}

	//South: larger z
	//North: smaller z
	//East: larger x
	//West: smaller x
	
	public LabyrinthCell getCellAt(int x, int z){
		if(x >= 0 && x < this.cells.length){
			if(z >= 0 && z < this.cells[x].length){
				return this.cells[x][z];
			}else{
				throw new IllegalArgumentException("z coordinate out of range, must be >=0 and <" + this.cells[x].length + ".");
			}
		}else{
			throw new IllegalArgumentException("x coordinate out of range, must be >=0 and <" + this.cells.length + ".");
		}
	}
	public void setCellAt(int x, int z, LabyrinthCell cell){
		if(x >= 0 && x < this.cells.length){
			if(z >= 0 && z < this.cells[x].length){
				this.cells[x][z] = cell;
			}else{
				throw new IllegalArgumentException("z coordinate out of range, must be >=0 and <" + this.cells[x].length + ".");
			}
		}else{
			throw new IllegalArgumentException("x coordinate out of range, must be >=0 and <" + this.cells.length + ".");
		}
	}
	public void setCellRow(int x, String[] patterns){
		if(x >= 0 && x < this.getSizeWE()){
			if(patterns.length == this.getSizeNS()){
				for(int i=0; i<this.getSizeNS(); i++){
					this.setCellAt(x, i, new LabyrinthCell(patterns[i], true));
				}
			}else{
				throw new IllegalArgumentException("Invalid pattern length, expected " + this.getSizeNS() + " got " + patterns.length + ".");
			}
		}else{
			throw new IllegalArgumentException("x coordinate out of range, must be >=0 and <" + this.cells.length + ".");
		}
	}
	
	public Labyrinth(int sizeWE, int sizeNS){
		this.cells = new LabyrinthCell[sizeWE][sizeNS];
	}
}
