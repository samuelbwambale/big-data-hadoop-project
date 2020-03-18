package tests;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Comparator;



public class CustomPairWritable implements Comparable<CustomPairWritable> {
	private String key;
	private String value;


	public CustomPairWritable(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

//	@Override
//	public void readFields(DataInput in) throws IOException {
//		// TODO Auto-generated method stub
//		key.readFields(in);
//		value.readFields(in);
//
//	}
//
//	@Override
//	public void write(DataOutput out) throws IOException {
//		// TODO Auto-generated method stub
//		key.write(out);
//		value.write(out);
//	}

	@Override
	public int hashCode() {
		int result = key != null ? key.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "(" + key + ", " + value + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj.getClass() != this.getClass()) return false;
		CustomPairWritable object = (CustomPairWritable) obj;
		if (object.getKey().equals(this.getKey())) {
			return object.getValue().equals(this.getValue());
		}
		return false;
	}

	@Override
	public int compareTo(CustomPairWritable key) {
		int k = this.key.compareTo(key.key);
		if (k != 0) return k;
		return this.value.compareTo(key.value);
	}

}
