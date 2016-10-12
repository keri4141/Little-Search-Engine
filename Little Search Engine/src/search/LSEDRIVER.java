package search;

import java.io.FileNotFoundException;

/*Little Search engine
 * Edit docs.txt file to incorporate doc files that will be scanned using this search engine.
 * 
 * Noise words are commonplace words (such as "the") that will be ignored by the search engine.
 */



public class LSEDRIVER
{
	
	public static void main(String[] args) throws FileNotFoundException
	{
		LittleSearchEngine f= new LittleSearchEngine();
		f.makeIndex("docs.txt", "noisewords.txt");
		System.out.println(f.top5search("coming","farts"));
		
		
	}
	
	
	
	
	
	
}