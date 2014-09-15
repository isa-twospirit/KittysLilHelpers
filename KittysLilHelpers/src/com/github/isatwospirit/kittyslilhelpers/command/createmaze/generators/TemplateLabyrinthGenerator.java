package com.github.isatwospirit.kittyslilhelpers.command.createmaze.generators;

import com.github.isatwospirit.kittyslilhelpers.command.createmaze.Labyrinth;
import com.github.isatwospirit.kittyslilhelpers.command.createmaze.LabyrinthCell;
import com.github.isatwospirit.kittyslilhelpers.command.createmaze.LabyrinthGenerator;

public class TemplateLabyrinthGenerator implements LabyrinthGenerator {

	@Override
	public Labyrinth generate(int sizeNS, int sizeWE, String[] args) {
		String templateName = "cretan";
		Labyrinth lab;
		
		if(templateName == "cretan"){
			lab = new Labyrinth(13,15);
			//								0     1     2     3     4     5     6     7     8     9     10    11    12    13    14
			lab.setCellRow(0, new String[]{"NS", "NS", "NS", "NS", "NS", "NS", "NE", "SE", "NS", "NS", "NS", "NS", "NS", "NS", "NE"});
			lab.setCellRow(1, new String[]{"SE", "NS", "NS", "NS", "NS", "NE", "WE", "WE", "SE", "NS", "NS", "NS", "NS", "NE", "WE"});
			lab.setCellRow(2, new String[]{"WE", "SE", "NS", "NS", "NS", "NW", "WE", "WE", "SW", "NS", "NS", "NS", "NE", "WE", "WE"});
			lab.setCellRow(3, new String[]{"WE", "WE", "SE", "NS", "NS", "NS", "NW", "SW", "NS", "NS", "NS", "NE", "WE", "WE", "WE"});
			lab.setCellRow(4, new String[]{"WE", "WE", "WE", "SE", "NS", "NS", "NE", "SE", "NS", "NS", "NE", "WE", "WE", "WE", "WE"});
			lab.setCellRow(5, new String[]{"WE", "WE", "WE", "WE", "SE", "NE", "WE", "W ", "SE", "NE", "WE", "WE", "WE", "WE", "WE"});
			lab.setCellRow(6, new String[]{"WE", "WE", "WE", "WE", "WE", "WE", "SW", "NS", "NW", "WE", "WE", "WE", "WE", "WE", "WE"});
			lab.setCellRow(7, new String[]{"WE", "WE", "WE", "WE", "WE", "SW", "NS", "NS", "NS", "NW", "WE", "WE", "WE", "WE", "WE"});
			lab.setCellRow(8, new String[]{"WE", "WE", "WE", "WE", "SW", "NS", "NS", "NS", "NS", "NS", "NW", "WE", "WE", "WE", "WE"});
			lab.setCellRow(9, new String[]{"WE", "WE", "WE", "SW", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NW", "WE", "WE", "WE"});
			lab.setCellRow(10,new String[]{"WE", "WE", "SW", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NW", "WE", "WE"});
			lab.setCellRow(11,new String[]{"WE", "SW", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NW", "WE"});
			lab.setCellRow(12,new String[]{"SW", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NS", "NW"});
		}else{
			lab = new Labyrinth(1,1);
			lab.setCellAt(0, 0, new LabyrinthCell("", true));
		}
		return lab;
	}

}
