package ca.dal.csci6405.project.generator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by sj on 28/03/17.
 * This is just an identity reducer
 */
public class HDPReducer extends Reducer<Text,IntWritable,Text,Text> {
    private final Text emptyWord = new Text();
    {
        emptyWord.set("");
    }
    public void reduce( Text key, Iterable<IntWritable> values, Context con )
        throws IOException, InterruptedException {
        con.write(key,emptyWord);
    }
}

