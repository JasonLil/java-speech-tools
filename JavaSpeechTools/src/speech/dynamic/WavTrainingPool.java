package speech.dynamic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class WavTrainingPool {
	private int nOut;
	
	WavTrainingPool(File root){

		HashMap<String,List<File>> set=new HashMap<String,List<File>> ();
			
		assert(root.isDirectory());
		for(File dir:root.listFiles()) {
			assert(dir.isDirectory());
			for(File f:dir.listFiles()) {
				String key=f.getName();
				if (set.containsKey(key)){
					set.get(key).add(f);
				} else {
					List<File> list=new ArrayList<File>();
					list.add(f);
					set.put(key,list);
				}
			}
		}
			
		nOut=set.size();
		for(String key:set.keySet()){
			List<File> list=set.get(key);
			
			
		}
		
	}
	

	class TrainingData {
		
		int id;
		
		TrainingData(File file,int id){
			this.id=id;
		}
	}
	
}
