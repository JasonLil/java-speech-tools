package speech.dynamic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import speech.ReadFeatureVectors;

public class TrainingData {

	int id;
	private ArrayList<double[]> featSeq;

	TrainingData(File file, int id, ReadFeatureVectors reader)
			throws IOException {
		this.id = id;
		featSeq = reader.readVectors(file);
	}

	public ArrayList<double[]> featureSequence() {
		return featSeq;
	}
}
