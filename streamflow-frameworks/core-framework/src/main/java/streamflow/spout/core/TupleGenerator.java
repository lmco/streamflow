package streamflow.spout.core;

import java.util.Date;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.name.Named;


import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@SuppressWarnings("serial")
public class TupleGenerator extends BaseRichSpout
{			
	private int myDelay;
	private String myJson;
				
	private SpoutOutputCollector myCollector;

	public TupleGenerator()
	{
		System.out.println("TupleGenerator constructor");	
	}
	
	@Inject
	public void setDelay(@Named("jetstream_spout_core_tupleGenerator_delay") String delay)
	{
		myDelay = Integer.parseInt(delay);
		System.out.println("myDelay=" + myDelay);
	}
	
	@Inject
	public void setJson(@Named("jetstream_spout_core_tupleGenerator_json") String json)
	{
		myJson = json;
		System.out.println("myJson=" + myJson);
	}
	
	@Override
	public void open(
			Map conf, 
			TopologyContext context,
			SpoutOutputCollector collector)
	{
		myCollector = collector;						
	}

	@Override
	public void nextTuple()
	{	    
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		
		try {		
			System.out.println("myJson2=" + myJson);
                        myCollector.emit(new Values(myJson));	                 
		}
        catch (Exception e) {
        	e.printStackTrace();
        }
        finally {        	
        	try 
	        {
	        	System.out.println("Sleeping for " + myDelay + " seconds");
	        	Thread.sleep(myDelay * 1000);
	        }
	        catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
    		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
	}
	
	@Override
	public void close()
	{		
	}
	
	@Override
	public void ack(Object id)
	{
		System.out.println("TupleGenerator.ack " + id.toString());
	}

	@Override
	public void fail(Object id)
	{
		String key = id.toString();
		System.out.println("TupleGenerator.fail " + key);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer)
	{
		declarer.declare(new Fields("json"));
	}
}