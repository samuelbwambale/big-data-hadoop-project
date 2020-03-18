package Part1;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class Average {

	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {

		final String regex = "^(\\S+) (\\S+) (\\S+) " + "\\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S+)"
				+ " (\\S+)\\s*(\\S+)?\\s*\" (\\d{3}) (\\S+)";

		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString().toLowerCase();
			Matcher matcher = pattern.matcher(line);

			while (matcher.find()) {
				try {
					String ip = matcher.group(1);
					String Quantity = matcher.group(9);
					int quantity = Integer.parseInt(Quantity);
					if ((ip.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) && (Quantity != "-" && quantity != 0 )) {
						Text ipAddress = new Text(ip);
						IntWritable qty = new IntWritable(quantity);
						context.write(ipAddress, qty);
					}
				} catch (Exception ex) {
				}

			}
		}
	}

	public static class Reduce extends Reducer<Text, IntWritable, Text, DoubleWritable> {

		public void reduce(Text ipAddress, Iterable<IntWritable> quantities, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			int count = 0;
			for (IntWritable val : quantities) {
				sum += val.get();
				count++;
			}
			context.write(ipAddress, new DoubleWritable(sum / count));
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();

		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "WordCountAverage");
		job.setJarByClass(Average.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
	}

}
