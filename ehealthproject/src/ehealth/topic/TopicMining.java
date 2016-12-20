package ehealth.topic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.json.JSONObject;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class TopicMining {
	private InstanceList instances = null;
	private ParallelTopicModel model = null;
	private String lang = "en";
	private static int numTopics = 5;
	private static int topN = 0;
	private static int maxTopicNum = 0;
	private static int numIterations = 0;
	private static String stopwordsDir = null;
	
	public static void initialize(String paramFile){
		Properties prop = new Properties( );
		try {
			prop.load(new FileReader(paramFile));//  
		} catch (Exception e) {
			e.printStackTrace();
		}
		numTopics = Integer.parseInt(prop.getProperty("numTopics"));
		topN = Integer.parseInt(prop.getProperty("topNwords"));
		maxTopicNum = Integer.parseInt(prop.getProperty("maxResveredTopicNum"));
		numIterations = Integer.parseInt(prop.getProperty("numIterations"));
		stopwordsDir = prop.getProperty("stopwordsDir");
	}
	
	//"stoplists/ar.txt"
	public TopicMining(String lang){
		this.lang = lang;
		String stopwordFile = stopwordsDir + "/"+lang+".txt";
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File(stopwordFile), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );

        instances = new InstanceList (new SerialPipes(pipeList));
//        this.numTopics = numTopics;
        model = new ParallelTopicModel(numTopics, 1.0, 0.01);

	}
	
	public void setDocument(String docFileName)throws Exception{
		Reader fileReader = new InputStreamReader(new FileInputStream(new File(docFileName)), "UTF-8");
		instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1)); // data, label, name fields
	}
	
	public void mineTopics()throws Exception{
        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(2);

        // Run the model for 50 iterations and stop (this is for testing only, 
        //  for real applications, use 1000 to 2000 iterations)     
        model.setNumIterations(numIterations);//use 1000 to 2000 iterations
        model.estimate();

	}
	
	public void outputTopic(String topicFile)throws Exception{
        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();
        
        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = new ArrayList(Arrays.asList(model.getSortedWords())) ;//每一个topic里对应的token，并排序
        
        // Show top 5 words in topics with proportions for the first document
        TopicWords[] tws = new TopicWords[numTopics];
        int index = 0;
        for (int topic = 0; topic < numTopics; topic++) {

        	Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();//遍历一个topic里面的每一个token        		
        	TopicWords tw = new TopicWords();
        	tw.setTopicId(index);
        	int rank = 0;
        	while (iterator.hasNext() && rank < topN) {//输出前5个
        		IDSorter idCountPair = iterator.next();
        		tw.addWord((String)dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
        		rank++;
        	}
        	tws[topic] = tw;
        	index ++;
        }
        
        
//        ArrayList<TopicWords> choosed = choose(topN,maxTopicNum,tws);
//        PrintStream pw = new PrintStream(topicFile,"UTF-8");
//        for(TopicWords tw : choosed){
//        	pw.println(new JSONObject(tw));
//        }
//        pw.close();
        
        PrintStream pw = new PrintStream(topicFile,"UTF-8");
        int count = 0;
        for(TopicWords tw : tws){
        	pw.println(new JSONObject(tw));
        	count ++;
        	if(count >= maxTopicNum){
        		break;
        	}
        }
        pw.close();
        
        
	}
	
	public ArrayList<TopicWords> choose(int topN,int maxTopicNum,TopicWords[] tws){
		ArrayList<TopicWords> result = new ArrayList();
		ArrayList<TreeSet<IDSorter>> topicSortedWords = new ArrayList(Arrays.asList(model.getSortedWords())) ;//每一个topic里对应的token，并排序
		boolean[] bs = new boolean[topicSortedWords.size()];
		Alphabet dataAlphabet = instances.getDataAlphabet();
		for(int i = 0;i < topicSortedWords.size();i ++){
			TreeSet<IDSorter> set = topicSortedWords.get(i);
			Iterator<IDSorter> iterator = set.iterator();//每一个topic
			int rank = 0;
			StringBuilder topicZeroText = new StringBuilder();
			while (iterator.hasNext() && rank < topN) {
				IDSorter idCountPair = iterator.next();
				topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");//第0个topic的前5个词
				rank++;
			}

			// Create a new instance named "test instance" with empty target and source fields.
			InstanceList testing = new InstanceList(instances.getPipe());
			testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

			TopicInferencer inferencer = model.getInferencer();//这是个验证
			double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 100, 1, 5);
			tws[i].setTopicProb(testProbabilities[i]);
			bs[i] = isTrueTopic(testProbabilities,i);
			System.out.println("topic-"+i+"\t" + testProbabilities[i]+"\t"+bs[i]);

			if(bs[i]){
				result.add(tws[i]);
			}
		}
		Collections.sort(result);
		ArrayList<TopicWords> finalresult = new ArrayList();
		int i = 0;
		for(TopicWords  tw : result){
			if(i < maxTopicNum){
				finalresult.add(tw);
			}
			i++;
		}
		return finalresult;
	}
	
	
//	public boolean[] validate(int topN){
//        // Create a new instance with high probability of topic 0
//        ArrayList<TreeSet<IDSorter>> topicSortedWords = new ArrayList(Arrays.asList(model.getSortedWords())) ;//每一个topic里对应的token，并排序
//        boolean[] result = new boolean[topicSortedWords.size()];
//        Alphabet dataAlphabet = instances.getDataAlphabet();
//        for(int i = 0;i < topicSortedWords.size();i ++){
//        	TreeSet<IDSorter> set = topicSortedWords.get(i);
//        	StringBuilder topicZeroText = new StringBuilder();
//        	 Iterator<IDSorter> iterator = set.iterator();//每一个topic
//        	 int rank = 0;
//        	 while (iterator.hasNext() && rank < topN) {
//        		 IDSorter idCountPair = iterator.next();
//        		 topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");//第0个topic的前5个词
//        		 rank++;
//        	 }
//        	 
//        	 // Create a new instance named "test instance" with empty target and source fields.
//        	 InstanceList testing = new InstanceList(instances.getPipe());
//        	 testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));
//        	 
//        	 TopicInferencer inferencer = model.getInferencer();//这是个验证
//        	 double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 100, 1, 5);
//        	 result[i] = isTrueTopic(testProbabilities,i);
//        	 System.out.println("topic-"+i+"\t" + testProbabilities[i]+"\t"+result[i]);
//        }
//        return result;
//       
//	}
	
	private boolean isTrueTopic(double[] testProbabilities,int index){
		int maxIndex = -1;
		double max = -1;
		for(int i = 0;i < testProbabilities.length;i ++){
			if(testProbabilities[i] > max){
				max = testProbabilities[i];
				maxIndex = i;
			}
		}
		return maxIndex == index;
	}
	
	// java -cp ../lib/'*' ehealth.topic.TopicMining ap.txt ap_topic.json stoplists/en.txt 10 4 10
	public static void main(String[] args) throws Exception{
//		TopicMining tm = new TopicMining(10,"stoplists/en.txt");
//		tm.setDocument("ap.txt");
//		tm.mineTopics();
//		tm.outputTopic("ap_topic.txt", 10, 5);
		
		String communitytweets = "data/ap.txt";//args[0];
		String topicfile = "data/ap_topic.txt";//args[1];
//		int numTopics = Integer.parseInt(args[3]);
//		int numTopicReserved = Integer.parseInt(args[4]);
//		int topnwords = Integer.parseInt(args[5]);
		TopicMining.initialize("data/topic-param.properties");
		TopicMining tm = new TopicMining("en");
		tm.setDocument(communitytweets);
		tm.mineTopics();
		tm.outputTopic(topicfile);
		
	}

}
