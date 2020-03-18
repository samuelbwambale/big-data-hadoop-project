package Part2_HybridApproach;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ReducerClass extends Reducer<CustomPairWritable, IntWritable, Text, CustomMapWritable> {

	private String prevTerm;
	private HashMap<String, Integer> hashMap;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		prevTerm = null;
		hashMap = new HashMap<>();
	}

	@Override
	protected void reduce(CustomPairWritable key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		if (!key.getKey().toString().equals(prevTerm) && prevTerm != null) {
			emit(context);
			hashMap.clear();
		}
		hashMap.put(key.getValue().toString(), sum(values));
		prevTerm = key.getKey().toString();
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		emit(context);
	}

	private int sum(Iterable<IntWritable> values) {
		int sum = 0;
		for (IntWritable value : values) {
			sum += value.get();
		}
		return sum;
	}

	private int total() {
		int sum = 0;
		for (Map.Entry entry : hashMap.entrySet()) {
			sum += (Integer) entry.getValue();
		}
		return sum;
	}

	private void emit(Context context) throws IOException, InterruptedException {
		int total = total();
		CustomMapWritable result = new CustomMapWritable();
		for (Map.Entry entry : hashMap.entrySet()) {
			result.put(new Text((String) entry.getKey()), new Text(entry.getValue() + "/" + total));
		}
		context.write(new Text(prevTerm), result);
	}
}
