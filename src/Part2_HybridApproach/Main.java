package Part2_HybridApproach;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import Part2_HybridApproach.CustomMapWritable;
import Part2_HybridApproach.CustomPairWritable;
import Part2_HybridApproach.MapClass;
import Part2_HybridApproach.ReducerClass;

public class Main {
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "HybridApproach");
        job.setJarByClass(Main.class);
        
		
		job.setMapOutputKeyClass(CustomPairWritable.class);
		job.setMapOutputValueClass(IntWritable.class);

        job.setMapperClass(MapClass.class);
        job.setReducerClass(ReducerClass.class);

        job.setMapOutputKeyClass(CustomPairWritable.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(CustomMapWritable.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));


		job.waitForCompletion(true);
	}

}